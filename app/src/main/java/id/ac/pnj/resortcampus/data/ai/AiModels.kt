package id.ac.pnj.resortcampus.data.ai

/** Kontrak output AI klasifikasi sampah (sama dengan versi web). */
data class WasteResult(
    val isWaste: Boolean,
    val itemName: String,
    val category: String,
    val material: String,
    val confidence: Double,
    val recyclable: Boolean,
    val disposalInstruction: String,
    val binType: String,
    val tips: String,
)

data class BinVerifyResult(
    val isBin: Boolean,
    val confidence: Double,
)

/** Hasil AI beserta penanda apakah memakai mode demo (mock). */
data class AiOutcome<T>(val value: T, val usedMock: Boolean)
