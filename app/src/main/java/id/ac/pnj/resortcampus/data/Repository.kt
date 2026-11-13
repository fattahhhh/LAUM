package id.ac.pnj.resortcampus.data

import android.content.Context
import android.net.Uri
import android.util.Log
import id.ac.pnj.resortcampus.data.ai.WasteClassifier
import id.ac.pnj.resortcampus.data.ai.WasteResult
import id.ac.pnj.resortcampus.data.local.AppDatabase
import id.ac.pnj.resortcampus.data.local.BinLocationEntity
import id.ac.pnj.resortcampus.data.local.ContributionEntity
import id.ac.pnj.resortcampus.data.local.MajorEntity
import id.ac.pnj.resortcampus.data.local.MajorScoreRow
import id.ac.pnj.resortcampus.data.local.ScanSessionEntity
import id.ac.pnj.resortcampus.data.local.ScanStatus
import id.ac.pnj.resortcampus.data.local.UserEntity
import id.ac.pnj.resortcampus.data.local.UserStatsRow
import id.ac.pnj.resortcampus.util.ImageUtils
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID

/** Pesan error yang aman ditampilkan langsung ke user. */
class UserFacingException(message: String) : Exception(message)

enum class Period(val label: String, val days: Long) {
    WEEK("Minggu", 7),
    MONTH("Bulanan", 30),
    SEMESTER("Semester", 182),
}

data class CurrentUser(val user: UserEntity, val major: MajorEntity?)

data class LeaderboardEntry(
    val rank: Int,
    val majorId: String,
    val name: String,
    val faculty: String,
    val points: Int,
)

data class ScanAnalysis(
    val result: WasteResult,
    val usedMock: Boolean,
    /** Terisi hanya jika sampah valid & yakin; dipakai saat verifikasi tong. */
    val scanSessionId: String?,
    val message: String,
    /** URI foto asli yang diambil dari kamera/galeri (jika ada) untuk ditampilkan di layar Hasil. */
    val photoUri: String? = null,
)

data class RewardInfo(
    val points: Int,
    val co2eKg: Double,
    val itemName: String,
    val category: String,
    val usedMock: Boolean,
    val photoUri: String? = null,
)

class Repository(
    private val context: Context,
    private val db: AppDatabase,
    private val session: SessionStore,
    private val classifier: WasteClassifier,
) {
    val isAiConfigured: Boolean get() = classifier.isConfigured

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) { Seeder.seedIfEmpty(db) }

    // ---------- Auth ----------

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUser: Flow<CurrentUser?> = session.userIdFlow.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else db.userDao().observeById(id).map { u ->
            u?.let { CurrentUser(it, it.majorId?.let { mid -> db.majorDao().getById(mid) }) }
        }
    }

    suspend fun isLoggedIn(): Boolean {
        val id = session.currentUserId() ?: return false
        return db.userDao().getById(id) != null
    }

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val normalized = email.trim().lowercase()
        if (!EMAIL_REGEX.matches(normalized)) throw UserFacingException("Format email tidak valid")
        if (password.isEmpty()) throw UserFacingException("Password wajib diisi")
        val user = db.userDao().findByEmail(normalized)
        val ok = user != null && withContext(Dispatchers.Default) { PasswordHasher.verify(password, user.passwordHash) }
        if (!ok) throw UserFacingException("Email atau password salah")
        session.save(user!!.id)
    }

    suspend fun register(name: String, email: String, password: String, majorId: String?): Result<Unit> = runCatching {
        val n = name.trim()
        val e = email.trim().lowercase()
        if (n.length < 2) throw UserFacingException("Nama minimal 2 karakter")
        if (!EMAIL_REGEX.matches(e)) throw UserFacingException("Format email tidak valid")
        if (password.length < 6) throw UserFacingException("Password minimal 6 karakter")
        if (majorId == null) throw UserFacingException("Pilih jurusan kamu")
        if (db.userDao().findByEmail(e) != null) throw UserFacingException("Email sudah terdaftar")
        val hash = withContext(Dispatchers.Default) { PasswordHasher.hash(password) }
        val user = UserEntity(UUID.randomUUID().toString(), n, e, hash, majorId, System.currentTimeMillis())
        db.userDao().insert(user)
        session.save(user.id)
    }

    suspend fun logout() = session.clear()

    suspend fun majors(): List<MajorEntity> = db.majorDao().getAll()

    // ---------- Scan ----------

    /** Langkah 1: foto sampah -> AI -> buat ScanSession (PENDING, 15 menit). */
    suspend fun analyzeWaste(photo: Uri): Result<ScanAnalysis> = runCatching {
        val userId = requireUserId()
        val now = System.currentTimeMillis()

        val dayStart = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        if (db.scanSessionDao().countSince(userId, dayStart) >= Points.DAILY_SCAN_LIMIT) {
            throw UserFacingException("Batas ${Points.DAILY_SCAN_LIMIT} scan per hari tercapai. Lanjut besok, ya.")
        }

        val sampleIdx = if (photo.scheme == "resort" && photo.host == "sample") {
            photo.lastPathSegment?.toIntOrNull()
        } else {
            photo.getQueryParameter("sample")?.toIntOrNull()
        }
        Log.d("Repository", "analyzeWaste photo=$photo scheme=${photo.scheme} sampleIdx=$sampleIdx")

        val jpeg: ByteArray
        val hash: String
        if (photo.scheme == "resort") {
            jpeg = ByteArray(0)
            hash = ImageUtils.sha256("$photo-$now".toByteArray())
        } else {
            jpeg = try {
                ImageUtils.compress(context, photo)
            } catch (e: Exception) {
                Log.w("Repository", "Kompres foto gagal: ${e.message}", e)
                ByteArray(0)
            }
            hash = if (jpeg.isNotEmpty() && sampleIdx == null) {
                ImageUtils.sha256(jpeg)
            } else {
                ImageUtils.sha256("$photo-$now".toByteArray())
            }
        }

        val realPhotoUri = if (photo.scheme != "resort") photo.toString() else null

        val ai = classifier.classify(jpeg, hash, sampleIdx)
        val r = ai.value
        if (!r.isWaste || r.confidence < Points.MIN_CONFIDENCE) {
            return@runCatching ScanAnalysis(
                result = r,
                usedMock = ai.usedMock,
                scanSessionId = null,
                message = if (!r.isWaste) "Ini sepertinya bukan sampah." else "AI kurang yakin. Coba foto ulang lebih dekat.",
                photoUri = realPhotoUri,
            )
        }

        db.scanSessionDao().cancelPendingForUser(userId)
        val s = ScanSessionEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            category = r.category,
            itemName = r.itemName,
            binType = r.binType,
            confidence = r.confidence,
            status = ScanStatus.PENDING,
            imageHash = hash,
            expiresAt = now + Points.SESSION_TTL_MS,
            createdAt = now,
        )
        db.scanSessionDao().insert(s)
        ScanAnalysis(
            result = r,
            usedMock = ai.usedMock,
            scanSessionId = s.id,
            message = "Sampah terdeteksi. Buang ke tong yang sesuai, lalu scan tongnya.",
            photoUri = realPhotoUri,
        )
    }

    /** Langkah 2: foto tong -> AI verifikasi -> poin masuk ke jurusan. */
    suspend fun verifyBinAndReward(scanSessionId: String, photo: Uri): Result<RewardInfo> = runCatching {
        val user = db.userDao().getById(requireUserId()) ?: throw UserFacingException("Sesi login berakhir")
        val majorId = user.majorId ?: throw UserFacingException("Atur jurusan di profil dulu")
        val now = System.currentTimeMillis()

        val s = db.scanSessionDao().getById(scanSessionId) ?: throw UserFacingException("Sesi scan tidak ditemukan")
        if (s.userId != user.id) throw UserFacingException("Sesi scan bukan milikmu")
        if (s.status != ScanStatus.PENDING) throw UserFacingException("Sesi scan sudah dipakai atau dibatalkan")
        if (now > s.expiresAt) {
            db.scanSessionDao().updateStatus(s.id, ScanStatus.EXPIRED)
            throw UserFacingException("Sesi scan kedaluwarsa (lebih dari 15 menit). Scan ulang sampahnya.")
        }

        val isSimulated = photo.scheme == "resort" || photo.getQueryParameter("sim") == "1"
        val jpeg = if (isSimulated) {
            ByteArray(0)
        } else {
            try {
                ImageUtils.compress(context, photo)
            } catch (e: Exception) {
                ByteArray(0)
            }
        }
        val bin = classifier.verifyBin(jpeg, isSimulated = isSimulated || jpeg.isEmpty())
        if (!bin.value.isBin || bin.value.confidence < Points.MIN_CONFIDENCE) {
            throw UserFacingException("Tong sampah tidak terdeteksi dengan jelas. Coba foto ulang.")
        }

        val rule = Points.ruleFor(s.category)
        db.withTransaction {
            db.scanSessionDao().updateStatus(s.id, ScanStatus.REWARDED)
            db.contributionDao().insert(
                ContributionEntity(
                    id = UUID.randomUUID().toString(),
                    userId = user.id,
                    majorId = majorId,
                    scanSessionId = s.id,
                    itemName = s.itemName,
                    category = s.category,
                    points = rule.points,
                    co2eKg = rule.co2eKg,
                    createdAt = now,
                ),
            )
        }
        val realBinPhotoUri = if (photo.scheme != "resort") photo.toString() else null
        RewardInfo(rule.points, rule.co2eKg, s.itemName, s.category, bin.usedMock, realBinPhotoUri)
    }

    suspend fun cancelSession(scanSessionId: String) {
        db.scanSessionDao().updateStatus(scanSessionId, ScanStatus.CANCELLED)
    }

    suspend fun expireOldSessions() = db.scanSessionDao().expireOld(System.currentTimeMillis())

    // ---------- Leaderboard & statistik ----------

    fun leaderboard(period: Period): Flow<List<LeaderboardEntry>> {
        val since = System.currentTimeMillis() - period.days * 24 * 60 * 60 * 1000L
        return db.contributionDao().observeLeaderboard(since).map { rows -> rows.ranked() }
    }

    fun userStats(userId: String): Flow<UserStatsRow> = db.contributionDao().observeUserStats(userId)

    fun history(userId: String): Flow<List<ContributionEntity>> = db.contributionDao().observeHistory(userId)

    /** Streak harian: jumlah hari berturut-turut (berakhir hari ini atau kemarin) yang punya kontribusi. */
    fun streak(userId: String): Flow<Int> = db.contributionDao().observeContributionTimes(userId).map { times ->
        val zone = ZoneId.systemDefault()
        val days = times.map { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }.distinct().sortedDescending()
        if (days.isEmpty()) return@map 0
        val today = LocalDate.now(zone)
        var cursor = days.first()
        if (ChronoUnit.DAYS.between(cursor, today) > 1) return@map 0
        var count = 1
        for (d in days.drop(1)) {
            if (ChronoUnit.DAYS.between(d, cursor) == 1L) {
                count++
                cursor = d
            } else break
        }
        count
    }

    fun bins(): Flow<List<BinLocationEntity>> = db.binDao().observeAll()

    private suspend fun requireUserId(): String =
        session.currentUserId() ?: throw UserFacingException("Silakan login terlebih dahulu")

    private fun List<MajorScoreRow>.ranked(): List<LeaderboardEntry> =
        mapIndexed { i, r -> LeaderboardEntry(i + 1, r.majorId, r.name, r.faculty, r.points) }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
