package id.ac.pnj.resortcampus.util

import android.os.SystemClock
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

data class LiveDetection(val name: String, val category: String, val confidence: Float)

/**
 * Analisis frame kamera secara realtime (maks 1 frame per ~0,8 detik) di perangkat.
 *
 * Ini HANYA petunjuk cepat. Hasil resmi tetap dari AI Gemini setelah foto dipotret.
 * Perbaikan: label generik ML Kit seperti "Glass" / "Drinkware" / "Bottle" sering muncul untuk
 * botol plastik bening, jadi kata itu tidak lagi langsung berarti "kaca".
 */
class LiveWasteAnalyzer(
    private val onResult: (LiveDetection?) -> Unit,
) : ImageAnalysis.Analyzer {

    @Volatile
    var enabled: Boolean = true

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder().setConfidenceThreshold(0.55f).build(),
    )
    private var lastRun = 0L

    // Penstabil: hasil baru baru ditampilkan kalau muncul 2 frame berturut-turut (kurangi kedip/salah tebak)
    private var candidate: String? = null
    private var streak = 0

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val media = image.image
        val now = SystemClock.elapsedRealtime()
        if (!enabled || media == null || now - lastRun < INTERVAL_MS) {
            image.close()
            return
        }
        lastRun = now
        val input = InputImage.fromMediaImage(media, image.imageInfo.rotationDegrees)
        labeler.process(input)
            .addOnSuccessListener { labels -> publish(pick(labels)) }
            .addOnFailureListener { publish(null) }
            .addOnCompleteListener { image.close() }
    }

    fun close() {
        labeler.close()
    }

    private fun publish(d: LiveDetection?) {
        val key = d?.category
        if (key == candidate) streak++ else { candidate = key; streak = 1 }
        if (d == null || streak >= 2) onResult(d)
    }

    private fun pick(labels: List<ImageLabel>): LiveDetection? {
        if (labels.isEmpty()) return null
        val words = labels.map { it.text.lowercase() }

        // Aturan khusus botol: kaca hanya jika ada bukti kuat, selain itu anggap plastik.
        val hasBottle = words.any { "bottle" in it }
        val hasPlasticWord = words.any { "plastic" in it }
        val strongGlass = words.any {
            "glass bottle" in it || it == "jar" || "wine" in it || "beer" in it || "mason" in it
        }
        if (hasBottle || hasPlasticWord) {
            val top = labels.maxByOrNull { it.confidence }!!
            return if (strongGlass && !hasPlasticWord) {
                LiveDetection("Botol kaca (perkiraan)", "kaca", top.confidence)
            } else {
                LiveDetection("Botol / sampah plastik", "plastik", top.confidence)
            }
        }

        // Skor per kategori = label dengan confidence tertinggi yang cocok kata kunci.
        val scores = HashMap<String, Float>()
        val names = HashMap<String, String>()
        for (label in labels) {
            val tokens = label.text.lowercase().split(' ', '-')
            for ((keys, category, name) in RULES) {
                if (tokens.any { it in keys } && label.confidence > (scores[category] ?: 0f)) {
                    scores[category] = label.confidence
                    names[category] = name
                }
            }
        }
        val best = scores.maxByOrNull { it.value } ?: return null
        return LiveDetection(names.getValue(best.key), best.key, best.value)
    }

    private companion object {
        const val INTERVAL_MS = 800L

        // "glass" TIDAK lagi dipakai sendirian: di ML Kit biasanya berarti gelas minum / kacamata.
        val RULES = listOf(
            Triple(setOf("jar", "mirror", "windowpane"), "kaca", "Sampah kaca"),
            Triple(setOf("plastic", "cup", "drinkware", "packaging", "bag", "tableware"), "plastik", "Sampah plastik"),
            Triple(setOf("paper", "cardboard", "newspaper", "carton", "envelope"), "kertas", "Sampah kertas"),
            Triple(setOf("can", "tin", "metal", "aluminium", "aluminum"), "logam", "Kaleng / logam"),
            Triple(setOf("food", "fruit", "vegetable", "banana", "apple", "orange", "meal"), "organik", "Sampah organik"),
            Triple(setOf("battery", "electronics", "cable", "circuit"), "elektronik", "Sampah elektronik"),
        )
    }
}