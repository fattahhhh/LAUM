package id.ac.pnj.resortcampus.data

/** Aturan poin & estimasi CO2e per kategori sampah. */
data class PointRule(val points: Int, val co2eKg: Double)

object Points {
    val CATEGORIES = listOf("plastik", "kertas", "organik", "kaca", "logam", "elektronik", "lainnya")

    private val RULES = mapOf(
        "plastik" to PointRule(15, 0.02),
        "kertas" to PointRule(10, 0.01),
        "organik" to PointRule(8, 0.005),
        "kaca" to PointRule(12, 0.015),
        "logam" to PointRule(20, 0.03),
        "elektronik" to PointRule(25, 0.05),
        "lainnya" to PointRule(5, 0.002),
    )

    fun ruleFor(category: String): PointRule = RULES[category] ?: RULES.getValue("lainnya")

    /** Target poin jurusan per minggu (ditampilkan di progres Beranda). */
    const val WEEKLY_MAJOR_TARGET = 2000

    /** Batas scan sampah per user per hari (anti-curang). */
    const val DAILY_SCAN_LIMIT = 30

    /** Masa berlaku sesi scan sebelum tong harus diverifikasi. */
    const val SESSION_TTL_MS = 15 * 60 * 1000L

    /** Ambang keyakinan minimum hasil AI. */
    const val MIN_CONFIDENCE = 0.6
}

/** Badge dihitung dari jumlah sampah terverifikasi. */
data class Badge(val name: String, val rule: String, val threshold: Int)

object Badges {
    val ALL = listOf(
        Badge("Pemula Hijau", "Sampah pertama terverifikasi", 1),
        Badge("Pengelola Sampah", "5 sampah terverifikasi", 5),
        Badge("Warrior Kampus", "10 sampah terverifikasi", 10),
        Badge("Juara Bumi", "25 sampah terverifikasi", 25),
    )
}
