package id.ac.pnj.resortcampus.data.ai

/**
 * Hasil cadangan saat API key belum diisi atau jaringan gagal,
 * supaya demo tetap berjalan offline.
 */
object MockAi {
    val SAMPLES = listOf(
        WasteResult(
            isWaste = true,
            itemName = "Botol Plastik",
            category = "plastik",
            material = "PET",
            confidence = 0.97,
            recyclable = true,
            disposalInstruction = "Kosongkan isi, remukkan, lalu buang ke tong anorganik.",
            binType = "anorganik",
            tips = "Lepas tutup dan label kalau bisa, keduanya beda jenis plastik.",
        ),
        WasteResult(
            isWaste = true,
            itemName = "Kertas Bekas",
            category = "kertas",
            material = "Kertas HVS",
            confidence = 0.94,
            recyclable = true,
            disposalInstruction = "Lipat rapi, buang ke tong kertas.",
            binType = "kertas",
            tips = "Kertas berminyak atau basah tidak bisa didaur ulang, masukkan ke residu.",
        ),
        WasteResult(
            isWaste = true,
            itemName = "Sisa Makanan",
            category = "organik",
            material = "Organik",
            confidence = 0.91,
            recyclable = false,
            disposalInstruction = "Buang ke tong organik untuk dikompos.",
            binType = "organik",
            tips = "Pisahkan dari bungkus plastik atau styrofoam sebelum dibuang.",
        ),
        WasteResult(
            isWaste = true,
            itemName = "Kaleng Minuman",
            category = "logam",
            material = "Aluminium",
            confidence = 0.95,
            recyclable = true,
            disposalInstruction = "Kosongkan, pipihkan, buang ke tong anorganik.",
            binType = "anorganik",
            tips = "Aluminium bisa didaur ulang berkali-kali tanpa turun kualitas.",
        ),
        WasteResult(
            isWaste = true,
            itemName = "Gelas Plastik Kopi",
            category = "plastik",
            material = "PP",
            confidence = 0.88,
            recyclable = true,
            disposalInstruction = "Bilas sisa minuman, pisahkan sedotan, buang ke tong anorganik.",
            binType = "anorganik",
            tips = "Bawa tumbler sendiri untuk mengurangi gelas sekali pakai.",
        ),
        WasteResult(
            isWaste = true,
            itemName = "Baterai Bekas",
            category = "elektronik",
            material = "Alkaline",
            confidence = 0.9,
            recyclable = true,
            disposalInstruction = "Jangan dibuang ke tong biasa. Serahkan ke drop box B3 kampus.",
            binType = "b3",
            tips = "Tutup kutub baterai dengan selotip agar tidak korsleting.",
        ),
    )

    fun classify(imageHash: String, sampleIndex: Int? = null): WasteResult {
        if (sampleIndex != null && sampleIndex in SAMPLES.indices) {
            return SAMPLES[sampleIndex]
        }
        val idx = (imageHash.take(8).toLongOrNull(16) ?: 0L).mod(SAMPLES.size.toLong()).toInt()
        return SAMPLES[idx]
    }

    fun verifyBin(): BinVerifyResult = BinVerifyResult(isBin = true, confidence = 0.93)
}
