package id.ac.pnj.resortcampus.data

import id.ac.pnj.resortcampus.util.isEnglish

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

    fun levelFor(points: Int): Int = (points.coerceAtLeast(0) / 100) + 1
}

/** Badge dihitung dari jumlah sampah terverifikasi. */
data class Badge(val name: String, val nameEn: String, val rule: String, val threshold: Int) {
    fun displayName(): String = if (isEnglish()) nameEn else name
}

object Badges {
    fun earnedFor(items: Int): List<Badge> = ALL.filter { items >= it.threshold }

    fun nextFor(items: Int): Badge? = ALL.firstOrNull { items < it.threshold }

    val ALL = listOf(
        Badge("Pahlawan kelas", "Class Hero", "Badge awal (0 foto scan)", 0),
        Badge("Pahlawan Kampus", "Campus Hero", "10 foto scan (+10 foto)", 10),
        Badge("Pecinta alam", "Nature Lover", "25 foto scan (+15 foto)", 25),
        Badge("Pejuang Hijau", "Green Fighter", "45 foto scan (+20 foto)", 45),
        Badge("Duta Daur Ulang", "Recycle Envoy", "70 foto scan (+25 foto)", 70),
        Badge("Pelopor Eko", "Eco Pioneer", "100 foto scan (+30 foto)", 100),
        Badge("Penjaga Bumi", "Earth Guardian", "135 foto scan (+35 foto)", 135),
        Badge("Master Sortir", "Sorting Master", "175 foto scan (+40 foto)", 175),
        Badge("Teladan PNJ", "PNJ Role Model", "220 foto scan (+45 foto)", 220),
        Badge("Bintang Hijau", "Green Star", "270 foto scan (+50 foto)", 270),
        Badge("Sadar Lingkungan", "Eco Aware", "325 foto scan (+55 foto)", 325),
        Badge("Bebas Plastik", "Plastic Free", "385 foto scan (+60 foto)", 385),
        Badge("Karat Bersih", "Clean Metal", "450 foto scan (+65 foto)", 450),
        Badge("Pahlawan Kertas", "Paper Hero", "520 foto scan (+70 foto)", 520),
        Badge("Legenda Kampus", "Campus Legend", "600 foto scan (+80 foto)", 600),
    )
}
