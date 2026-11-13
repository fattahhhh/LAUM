package id.ac.pnj.resortcampus.data

import id.ac.pnj.resortcampus.data.local.AppDatabase
import id.ac.pnj.resortcampus.data.local.BinLocationEntity
import id.ac.pnj.resortcampus.data.local.ContributionEntity
import id.ac.pnj.resortcampus.data.local.MajorEntity
import id.ac.pnj.resortcampus.data.local.UserEntity
import androidx.room.withTransaction
import kotlin.random.Random

/** Data awal: jurusan PNJ, akun demo, titik tong sampah, dan riwayat kontribusi contoh untuk leaderboard. */
object Seeder {
    const val DEMO_EMAIL = "demo@pnj.ac.id"
    const val DEMO_PASSWORD = "rahasia123"

    private val MAJORS = listOf(
        MajorEntity("tik", "Teknik Informatika dan Komputer", "Rekayasa"),
        MajorEntity("sipil", "Teknik Sipil", "Rekayasa"),
        MajorEntity("mesin", "Teknik Mesin", "Rekayasa"),
        MajorEntity("elektro", "Teknik Elektro", "Rekayasa"),
        MajorEntity("tgp", "Teknik Grafika dan Penerbitan", "Rekayasa"),
        MajorEntity("akuntansi", "Akuntansi", "Tata Niaga"),
        MajorEntity("an", "Administrasi Niaga", "Tata Niaga"),
    )

    private val BINS = listOf(
        BinLocationEntity("bin-1", "Tong Depan Gedung Direktorat", 0.22f, 0.24f, "Gedung Direktorat", "anorganik"),
        BinLocationEntity("bin-2", "Tong Kantin Pusat", 0.72f, 0.56f, "Kantin", "organik"),
        BinLocationEntity("bin-3", "Tong Perpustakaan", 0.80f, 0.17f, "Perpustakaan", "kertas"),
        BinLocationEntity("bin-4", "Tong Parkiran Motor", 0.20f, 0.80f, "Parkiran", "anorganik"),
        BinLocationEntity("bin-5", "Tong Lapangan Olahraga", 0.78f, 0.86f, "Lapangan", "organik"),
        BinLocationEntity("bin-6", "Drop Box B3 Gedung TIK", 0.42f, 0.42f, "Gedung TIK", "b3"),
    )

    /** Bobot aktivitas tiap jurusan agar leaderboard demo bervariasi. */
    private val ACTIVITY = mapOf(
        "tik" to 1.0, "sipil" to 0.8, "mesin" to 0.65, "elektro" to 0.9,
        "tgp" to 0.45, "akuntansi" to 0.7, "an" to 0.55,
    )

    suspend fun seedIfEmpty(db: AppDatabase) {
        if (db.majorDao().count() > 0) return
        val now = System.currentTimeMillis()
        val rnd = Random(2026)

        val demoUser = UserEntity(
            id = "user-demo",
            name = "Demo User",
            email = DEMO_EMAIL,
            passwordHash = PasswordHasher.hash(DEMO_PASSWORD),
            majorId = "tik",
            createdAt = now,
        )
        // Akun "teman sejurusan" sebagai pemilik kontribusi contoh. Tidak bisa dipakai login.
        val peers = MAJORS.map { m ->
            UserEntity("peer-${m.id}", "Mahasiswa ${m.name}", "peer-${m.id}@seed.local", "disabled", m.id, now)
        }

        val samples = listOf(
            "Botol Plastik" to "plastik", "Kertas Bekas" to "kertas", "Sisa Makanan" to "organik",
            "Kaleng Minuman" to "logam", "Botol Kaca" to "kaca", "Gelas Plastik" to "plastik",
        )
        val contributions = mutableListOf<ContributionEntity>()
        val day = 24 * 60 * 60 * 1000L
        for (m in MAJORS) {
            val weight = ACTIVITY.getValue(m.id)
            for (d in 0 until 150) {
                val perDay = (rnd.nextDouble() * 6 * weight).toInt()
                repeat(perDay) { i ->
                    val (item, cat) = samples[rnd.nextInt(samples.size)]
                    val rule = Points.ruleFor(cat)
                    contributions += ContributionEntity(
                        id = "seed-${m.id}-$d-$i",
                        userId = "peer-${m.id}",
                        majorId = m.id,
                        scanSessionId = null,
                        itemName = item,
                        category = cat,
                        points = rule.points,
                        co2eKg = rule.co2eKg,
                        createdAt = now - d * day - rnd.nextLong(day),
                    )
                }
            }
        }

        db.withTransaction {
            db.majorDao().insertAll(MAJORS)
            db.userDao().insertAll(listOf(demoUser) + peers)
            db.binDao().insertAll(BINS)
            db.contributionDao().insertAll(contributions)
        }
    }
}
