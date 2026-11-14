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
        // Gedung Z - Teknik Grafika dan Penerbitan (2 tong)
        BinLocationEntity("tgp-1", "Tong Kertas Gedung Z Teknik Grafika", 0.20f, 0.12f, "Gedung Z Teknik Grafika", "kertas"),
        BinLocationEntity("tgp-2", "Tong Anorganik Gedung Z Teknik Grafika", 0.22f, 0.14f, "Gedung Z Teknik Grafika", "anorganik"),
        // Gedung Administrasi Niaga / Bisnis (2 tong)
        BinLocationEntity("an-1", "Tong Kertas Gedung Administrasi Niaga", 0.21f, 0.28f, "Gedung Administrasi Niaga", "kertas"),
        BinLocationEntity("an-2", "Tong Organik Gedung Administrasi Niaga", 0.23f, 0.30f, "Gedung Administrasi Niaga", "organik"),
        // Gedung F - Akuntansi (2 tong)
        BinLocationEntity("akt-1", "Tong Kertas Gedung F Akuntansi", 0.36f, 0.40f, "Gedung F Akuntansi", "kertas"),
        BinLocationEntity("akt-2", "Tong Anorganik Gedung F Akuntansi", 0.38f, 0.42f, "Gedung F Akuntansi", "anorganik"),
        // Gedung Q - Direktorat (2 tong)
        BinLocationEntity("dir-1", "Tong Anorganik Gedung Q Direktorat", 0.64f, 0.36f, "Gedung Q Direktorat", "anorganik"),
        BinLocationEntity("dir-2", "Tong Kertas Gedung Q Direktorat", 0.66f, 0.38f, "Gedung Q Direktorat", "kertas"),
        // Gedung D - Teknik Elektro (2 tong)
        BinLocationEntity("elektro-1", "Tong Anorganik Gedung D Teknik Elektro", 0.20f, 0.52f, "Gedung D Teknik Elektro", "anorganik"),
        BinLocationEntity("elektro-2", "Drop Box B3 Gedung D Teknik Elektro", 0.22f, 0.54f, "Gedung D Teknik Elektro", "b3"),
        // Gedung A & Gedung M - Teknik Mesin (2 tong)
        BinLocationEntity("mesin-1", "Tong Anorganik Gedung A Teknik Mesin", 0.38f, 0.52f, "Gedung A Teknik Mesin", "anorganik"),
        BinLocationEntity("mesin-2", "Drop Box B3 Gedung M Teknik Mesin", 0.72f, 0.80f, "Gedung M Teknik Mesin", "b3"),
        // Gedung AA - Teknik Informatika dan Komputer / TIK (2 tong)
        BinLocationEntity("tik-1", "Drop Box B3 Gedung AA TIK", 0.46f, 0.82f, "Gedung AA TIK", "b3"),
        BinLocationEntity("tik-2", "Tong Anorganik Gedung AA TIK", 0.48f, 0.84f, "Gedung AA TIK", "anorganik"),
        // Gedung T - Departemen Teknik Sipil (2 tong)
        BinLocationEntity("sipil-1", "Tong Anorganik Gedung T Teknik Sipil", 0.88f, 0.90f, "Gedung T Teknik Sipil", "anorganik"),
        BinLocationEntity("sipil-2", "Tong Kertas Gedung T Teknik Sipil", 0.90f, 0.92f, "Gedung T Teknik Sipil", "kertas"),
        // Gedung Y - Perpustakaan (1 tong)
        BinLocationEntity("perpus-1", "Tong Kertas Gedung Y Perpustakaan", 0.92f, 0.68f, "Gedung Y Perpustakaan", "kertas"),
        // Kantin Spirit & Kantin Bawah (masing-masing 1 tong)
        BinLocationEntity("kantin-1", "Tong Organik Kantin Spirit", 0.54f, 0.70f, "Kantin Spirit", "organik"),
        BinLocationEntity("kantin-2", "Tong Anorganik Kantin Bawah", 0.18f, 0.35f, "Kantin Bawah", "anorganik"),
        // Gedung Serba Guna (1 tong)
        BinLocationEntity("gsg-1", "Tong Organik Gedung Serba Guna", 0.42f, 0.28f, "Gedung Serba Guna", "organik"),
        // Masjid Darul Ilmi (1 tong)
        BinLocationEntity("masjid-1", "Tong Organik Masjid Darul Ilmi", 0.20f, 0.60f, "Masjid Darul Ilmi", "organik"),
    )

    /** Daftar mahasiswa PNJ contoh untuk leaderboard per user (sesuai pilihan jurusan saat register). */
    private data class SeedStudent(val id: String, val name: String, val majorId: String, val weight: Double)

    private val SEED_STUDENTS = listOf(
        SeedStudent("student-1", "Rizky Pratama", "elektro", 1.05),
        SeedStudent("student-2", "Nadia Putri", "sipil", 0.95),
        SeedStudent("student-3", "Dimas Saputra", "mesin", 0.86),
        SeedStudent("student-4", "Siti Rahma", "akuntansi", 0.76),
        SeedStudent("student-5", "Bagas Wijaya", "an", 0.70),
        SeedStudent("student-6", "Aulia Zahra", "tik", 0.65),
        SeedStudent("student-7", "Fajar Ramadhan", "tgp", 0.60),
        SeedStudent("student-8", "Dewi Lestari", "tik", 0.54),
        SeedStudent("student-9", "Reza Mahendra", "elektro", 0.48),
        SeedStudent("student-10", "Putri Maharani", "an", 0.42),
    )

    suspend fun seedIfEmpty(db: AppDatabase) {
        val expectedIds = BINS.map { it.id }.toSet()
        val existingIds = db.binDao().allIds().toSet()
        if (existingIds != expectedIds) {
            db.withTransaction {
                db.binDao().deleteAll()
                db.binDao().insertAll(BINS)
            }
        }

        val existingUserIds = db.userDao().allIds().toSet()
        val needsStudentSeed = SEED_STUDENTS.any { it.id !in existingUserIds } ||
            existingUserIds.any { it.startsWith("peer-") }
        if (db.majorDao().count() > 0 && !needsStudentSeed) return

        val now = System.currentTimeMillis()
        val rnd = Random(2026)

        val students = SEED_STUDENTS.map { s ->
            UserEntity(
                id = s.id,
                name = s.name,
                email = "${s.id}@pnj.ac.id",
                passwordHash = "disabled",
                majorId = s.majorId,
                createdAt = now,
            )
        }

        val samples = listOf(
            "Botol Plastik" to "plastik", "Kertas Bekas" to "kertas", "Sisa Makanan" to "organik",
            "Kaleng Minuman" to "logam", "Botol Kaca" to "kaca", "Gelas Plastik" to "plastik",
        )
        val contributions = mutableListOf<ContributionEntity>()
        val day = 24 * 60 * 60 * 1000L
        for (s in SEED_STUDENTS) {
            for (d in 0 until 150) {
                val perDay = (rnd.nextDouble() * 5 * s.weight).toInt()
                repeat(perDay) { i ->
                    val (item, cat) = samples[rnd.nextInt(samples.size)]
                    val rule = Points.ruleFor(cat)
                    contributions += ContributionEntity(
                        id = "seed-${s.id}-$d-$i",
                        userId = s.id,
                        majorId = s.majorId,
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
            db.userDao().deleteLegacySeedUsers()
            db.userDao().insertAll(students)
            db.binDao().insertAll(BINS)
            db.contributionDao().insertAll(contributions)
        }
    }
}
