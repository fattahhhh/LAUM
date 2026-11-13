package id.ac.pnj.resortcampus.data.ai

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Klasifikasi sampah & verifikasi tong langsung di perangkat memakai Google ML Kit Image Labeling.
 * 100% gratis, tidak butuh API key, tanpa batas kuota (unlimited), dan tetap jalan saat offline.
 */
object OnDeviceWasteClassifier {

    suspend fun classify(jpeg: ByteArray): WasteResult {
        val labels = extractLabels(jpeg, minConfidence = 0.45f)
        return classifyLabels(labels)
    }

    suspend fun verifyBin(jpeg: ByteArray): BinVerifyResult {
        val labels = extractLabels(jpeg, minConfidence = 0.45f)
        return verifyBinLabels(labels)
    }

    private suspend fun extractLabels(jpeg: ByteArray, minConfidence: Float): List<Pair<String, Float>> =
        withContext(Dispatchers.Default) {
            val bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size)
                ?: throw IllegalArgumentException("Foto tidak dapat dibaca")
            val labeler = ImageLabeling.getClient(
                ImageLabelerOptions.Builder().setConfidenceThreshold(minConfidence).build(),
            )
            try {
                val input = InputImage.fromBitmap(bitmap, 0)
                suspendCancellableCoroutine { cont ->
                    labeler.process(input)
                        .addOnSuccessListener { items ->
                            if (cont.isActive) cont.resume(items.map { it.text to it.confidence })
                        }
                        .addOnFailureListener { err ->
                            if (cont.isActive) cont.resumeWithException(err)
                        }
                }
            } finally {
                labeler.close()
                if (!bitmap.isRecycled) bitmap.recycle()
            }
        }

    internal fun classifyLabels(labels: List<Pair<String, Float>>): WasteResult {
        if (labels.isEmpty()) {
            return notWasteResult(confidence = 0.35, reason = "Objek kurang jelas. Dekatkan kamera ke sampah dengan cahaya cukup.")
        }

        val lowerWords = labels.map { it.first.lowercase() }

        // Aturan botol: jika ada kata "bottle" / "plastic", bedakan botol plastik (PET) vs botol kaca.
        val hasBottle = lowerWords.any { "bottle" in it }
        val hasPlasticWord = lowerWords.any { "plastic" in it }
        val strongGlass = lowerWords.any {
            "glass bottle" in it || it == "jar" || "wine" in it || "beer" in it || "mason" in it
        }
        if (hasBottle || hasPlasticWord) {
            val topConf = labels.maxOf { it.second }.toDouble().coerceIn(0.62, 0.96)
            return if (strongGlass && !hasPlasticWord) {
                templateFor(
                    category = "kaca",
                    itemName = "Botol / Wadah Kaca",
                    material = "Kaca",
                    confidence = topConf,
                )
            } else {
                templateFor(
                    category = "plastik",
                    itemName = if (hasBottle) "Botol Plastik" else "Kemasan Plastik",
                    material = if (hasBottle) "Plastik PET" else "Plastik",
                    confidence = topConf,
                )
            }
        }

        // Cocokkan ke kamus label ML Kit berdasarkan skor tertinggi.
        var bestRule: LabelRule? = null
        var bestScore = 0f
        for ((rawText, conf) in labels) {
            val text = rawText.lowercase()
            val tokens = text.split(' ', '-', '_')
            for (rule in WASTE_RULES) {
                val matched = rule.keywords.any { kw ->
                    if (' ' in kw) kw in text else kw in tokens
                }
                if (matched && conf > bestScore) {
                    bestScore = conf
                    bestRule = rule
                }
            }
        }

        if (bestRule != null) {
            val boostedConf = bestScore.toDouble().coerceAtLeast(0.64).coerceAtMost(0.95)
            return templateFor(
                category = bestRule.category,
                itemName = bestRule.itemName,
                material = bestRule.material,
                confidence = boostedConf,
            )
        }

        // Jika jelas objek non-sampah (wajah, langit, tembok, kendaraan, dll.), tolak agar tidak curang.
        val nonWasteHit = labels.firstOrNull { (raw, _) ->
            val t = raw.lowercase()
            NON_WASTE_KEYWORDS.any { it in t }
        }
        if (nonWasteHit != null) {
            return notWasteResult(
                confidence = nonWasteHit.second.toDouble().coerceIn(0.5, 0.95),
                reason = "Terdeteksi bukan sampah (${nonWasteHit.first}). Arahkan kamera ke sampah yang ingin dipilah.",
            )
        }

        // Label generik (mis. Product / Object) dengan confidence cukup -> masuk kategori lainnya.
        val top = labels.maxByOrNull { it.second }!!
        return templateFor(
            category = "lainnya",
            itemName = "Sampah (${top.first})",
            material = "Campuran / Residu",
            confidence = top.second.toDouble().coerceIn(0.55, 0.85),
        )
    }

    internal fun verifyBinLabels(labels: List<Pair<String, Float>>): BinVerifyResult {
        if (labels.isEmpty()) return BinVerifyResult(isBin = false, confidence = 0.3)

        val binMatch = labels.filter { (raw, _) ->
            val t = raw.lowercase()
            BIN_KEYWORDS.any { it in t }
        }.maxByOrNull { it.second }

        if (binMatch != null) {
            return BinVerifyResult(
                isBin = true,
                confidence = binMatch.second.toDouble().coerceAtLeast(0.75).coerceAtMost(0.96),
            )
        }

        val nonBinHit = labels.firstOrNull { (raw, _) ->
            val t = raw.lowercase()
            NON_WASTE_KEYWORDS.any { it in t }
        }
        if (nonBinHit != null) {
            return BinVerifyResult(isBin = false, confidence = 0.35)
        }

        val topConf = labels.maxOf { it.second }.toDouble()
        return BinVerifyResult(isBin = topConf >= 0.55, confidence = topConf.coerceIn(0.5, 0.82))
    }

    private fun templateFor(
        category: String,
        itemName: String,
        material: String,
        confidence: Double,
    ): WasteResult = when (category) {
        "plastik" -> WasteResult(
            isWaste = true,
            itemName = itemName,
            category = "plastik",
            material = material,
            confidence = confidence,
            recyclable = true,
            disposalInstruction = "Kosongkan isi, remukkan agar hemat ruang, lalu buang ke tong anorganik.",
            binType = "anorganik",
            tips = "Pisahkan tutup dan bilas singkat bila masih ada sisa minuman.",
        )
        "kertas" -> WasteResult(
            isWaste = true,
            itemName = itemName,
            category = "kertas",
            material = material,
            confidence = confidence,
            recyclable = true,
            disposalInstruction = "Lipat rapi dalam kondisi kering lalu buang ke tong kertas.",
            binType = "kertas",
            tips = "Kertas yang basah atau berminyak sebaiknya dibuang ke tong residu/anorganik.",
        )
        "organik" -> WasteResult(
            isWaste = true,
            itemName = itemName,
            category = "organik",
            material = material,
            confidence = confidence,
            recyclable = false,
            disposalInstruction = "Buang ke tong organik agar bisa diolah menjadi kompos.",
            binType = "organik",
            tips = "Pastikan tidak tercampur bungkus plastik atau tusuk sate.",
        )
        "logam" -> WasteResult(
            isWaste = true,
            itemName = itemName,
            category = "logam",
            material = material,
            confidence = confidence,
            recyclable = true,
            disposalInstruction = "Kosongkan dan pipihkan kaleng/logam, lalu buang ke tong anorganik.",
            binType = "anorganik",
            tips = "Logam aluminium dapat didaur ulang berulang kali dengan nilai tinggi.",
        )
        "kaca" -> WasteResult(
            isWaste = true,
            itemName = itemName,
            category = "kaca",
            material = material,
            confidence = confidence,
            recyclable = true,
            disposalInstruction = "Kosongkan wadah kaca dan buang hati-hati ke tong anorganik.",
            binType = "anorganik",
            tips = "Bungkus dengan kertas tebal jika kaca pecah agar aman bagi petugas.",
        )
        "elektronik" -> WasteResult(
            isWaste = true,
            itemName = itemName,
            category = "elektronik",
            material = material,
            confidence = confidence,
            recyclable = true,
            disposalInstruction = "Jangan buang ke tong biasa. Masukkan ke Drop Box B3 kampus (Gedung TIK).",
            binType = "b3",
            tips = "Limbah B3/elektronik mengandung logam berat yang perlu penanganan khusus.",
        )
        else -> WasteResult(
            isWaste = true,
            itemName = itemName,
            category = "lainnya",
            material = material,
            confidence = confidence,
            recyclable = false,
            disposalInstruction = "Masukkan ke tong anorganik/residu terdekat.",
            binType = "anorganik",
            tips = "Jika berupa masker atau tisu bekas, lipat dan bungkus sebelum dibuang.",
        )
    }

    private fun notWasteResult(confidence: Double, reason: String) = WasteResult(
        isWaste = false,
        itemName = "Bukan Sampah",
        category = "lainnya",
        material = "-",
        confidence = confidence,
        recyclable = false,
        disposalInstruction = reason,
        binType = "anorganik",
        tips = "Pastikan objek sampah berada di tengah bingkai kamera.",
    )

    private data class LabelRule(
        val keywords: Set<String>,
        val category: String,
        val itemName: String,
        val material: String,
    )

    private val WASTE_RULES = listOf(
        LabelRule(
            keywords = setOf("battery", "electronics", "cable", "circuit", "phone", "mobile phone", "charger", "computer", "keyboard", "mouse", "gadget", "light bulb", "lamp"),
            category = "elektronik",
            itemName = "Limbah Elektronik / B3",
            material = "Komponen Elektronik",
        ),
        LabelRule(
            keywords = setOf("can", "tin", "tin can", "soda can", "beverage can", "metal", "aluminium", "aluminum", "foil", "steel"),
            category = "logam",
            itemName = "Kaleng / Logam Bekas",
            material = "Aluminium / Logam",
        ),
        LabelRule(
            keywords = setOf("jar", "glass bottle", "wine", "beer", "mason", "mirror", "windowpane", "goblet"),
            category = "kaca",
            itemName = "Botol / Wadah Kaca",
            material = "Kaca",
        ),
        LabelRule(
            keywords = setOf("mask", "surgical mask", "napkin", "tissue", "diaper", "cigarette"),
            category = "lainnya",
            itemName = "Masker / Tisu Bekas",
            material = "Residu Non-Daur Ulang",
        ),
        LabelRule(
            keywords = setOf("paper", "cardboard", "newspaper", "carton", "envelope", "book", "magazine", "notebook", "box"),
            category = "kertas",
            itemName = "Kertas / Kardus Bekas",
            material = "Kertas / Karton",
        ),
        LabelRule(
            keywords = setOf("cup", "drinkware", "packaging", "bag", "plastic bag", "wrapper", "straw", "tableware", "jug", "lid", "cap"),
            category = "plastik",
            itemName = "Gelas / Kemasan Plastik",
            material = "Plastik PP / PET",
        ),
        LabelRule(
            keywords = setOf("food", "fruit", "vegetable", "banana", "apple", "orange", "meal", "bread", "snack", "leaf", "plant", "flower", "peel", "rice", "dish", "baked goods"),
            category = "organik",
            itemName = "Sisa Makanan / Organik",
            material = "Organik",
        ),
    )

    private val NON_WASTE_KEYWORDS = setOf(
        "person", "selfie", "face", "smile", "hair", "skin", "portrait",
        "sky", "cloud", "wall", "ceiling", "building", "car", "vehicle", "landscape", "mountain",
    )

    private val BIN_KEYWORDS = setOf(
        "waste container", "trash", "garbage", "bin", "bucket", "basket",
        "container", "barrel", "tub", "cylinder", "box", "recycling", "plastic", "metal",
    )
}
