package id.ac.pnj.resortcampus.data.ai

import android.util.Base64
import android.util.Log
import id.ac.pnj.resortcampus.BuildConfig
import id.ac.pnj.resortcampus.data.Points
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Klasifikasi sampah & verifikasi tong dengan rantai fallback otomatis:
 * 1. Cloud Vision AI (Gemini API / Groq Cloud Vision gratis) jika API key diisi dan kuota tersedia.
 * 2. On-Device AI (Google ML Kit Image Labeling) otomatis saat API key kosong, kuota habis/limit (429),
 *    atau perangkat offline -> 100% gratis & tanpa batas limit!
 * 3. Sampel demo galeri (jpeg kosong) -> [MockAi].
 */
class WasteClassifier(
    private val apiKey: String = BuildConfig.GEMINI_API_KEY,
    private val model: String = BuildConfig.GEMINI_MODEL,
    private val groqApiKey: String = BuildConfig.GROQ_API_KEY,
    private val groqModel: String = BuildConfig.GROQ_MODEL,
) {
    private val http = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .callTimeout(45, TimeUnit.SECONDS)
        .build()

    val isConfigured: Boolean
        get() = apiKey.isNotBlank() || groqApiKey.isNotBlank()

    suspend fun classify(
        jpeg: ByteArray,
        imageHash: String,
        sampleIndex: Int? = null,
    ): AiOutcome<WasteResult> {
        Log.d("WasteClassifier", "classify: jpeg=${jpeg.size} byte, cloudConfigured=$isConfigured, sample=$sampleIndex")
        if (jpeg.isEmpty()) {
            delay(500)
            return AiOutcome(MockAi.classify(imageHash, sampleIndex), usedMock = true)
        }

        val cloudResult = tryCloudWaste(jpeg)
        if (cloudResult != null) {
            return AiOutcome(normalize(cloudResult), usedMock = false)
        }

        // Cloud tidak dikonfigurasi / kena limit / offline -> pakai ML Kit On-Device (gratis & tanpa limit).
        return try {
            val local = OnDeviceWasteClassifier.classify(jpeg)
            AiOutcome(normalize(local), usedMock = false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("WasteClassifier", "On-device ML Kit gagal: ${e.message}")
            AiOutcome(MockAi.classify(imageHash, sampleIndex), usedMock = true)
        }
    }

    private suspend fun tryCloudWaste(jpeg: ByteArray): WasteResult? {
        if (apiKey.isNotBlank()) {
            val geminiCandidates = listOf(model, "gemini-3.5-flash-lite", "gemini-3.8-flash")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
            for (m in geminiCandidates) {
                try {
                    val raw = callGemini(m, jpeg, WASTE_SYSTEM, WASTE_USER)
                    return parseWaste(raw)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w("WasteClassifier", "Gemini ($m) gagal: ${e.message}")
                }
            }
        }

        if (groqApiKey.isNotBlank()) {
            try {
                val raw = callGroq(groqModel.ifBlank { DEFAULT_GROQ_MODEL }, jpeg, WASTE_SYSTEM, WASTE_USER)
                return parseWaste(raw)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("WasteClassifier", "Groq ($groqModel) gagal: ${e.message}")
            }
        }

        return null
    }

    /**
     * Pengaman konsistensi: kalau kategori "kaca" tapi bahan/nama jelas plastik (atau sebaliknya),
     * percaya bahan yang disebut. Mencegah botol PET terbaca sebagai botol kaca.
     */
    private fun normalize(r: WasteResult): WasteResult {
        val text = "${r.material} ${r.itemName}".lowercase()
        val saysPlastic = PLASTIC_WORDS.any { it in text }
        val saysGlass = GLASS_WORDS.any { it in text }
        val fixed = when {
            r.category == "kaca" && saysPlastic && !saysGlass -> "plastik"
            r.category == "plastik" && saysGlass && !saysPlastic -> "kaca"
            else -> r.category
        }
        return if (fixed == r.category) r else r.copy(category = fixed, binType = "anorganik")
    }

    suspend fun verifyBin(jpeg: ByteArray, isSimulated: Boolean = false): AiOutcome<BinVerifyResult> {
        if (isSimulated || jpeg.isEmpty()) {
            delay(500)
            return AiOutcome(MockAi.verifyBin(), usedMock = true)
        }

        val cloudBin = tryCloudBin(jpeg)
        if (cloudBin != null) {
            return if (!cloudBin.isBin || cloudBin.confidence < Points.MIN_CONFIDENCE) {
                AiOutcome(MockAi.verifyBin(), usedMock = true)
            } else {
                AiOutcome(cloudBin, usedMock = false)
            }
        }

        return try {
            val localBin = OnDeviceWasteClassifier.verifyBin(jpeg)
            AiOutcome(localBin, usedMock = false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("WasteClassifier", "On-device verifyBin gagal: ${e.message}")
            AiOutcome(MockAi.verifyBin(), usedMock = true)
        }
    }

    private suspend fun tryCloudBin(jpeg: ByteArray): BinVerifyResult? {
        if (apiKey.isNotBlank()) {
            val geminiCandidates = listOf(model, "gemini-3.5-flash-lite", "gemini-3.8-flash")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
            for (m in geminiCandidates) {
                try {
                    return parseBin(callGemini(m, jpeg, BIN_SYSTEM, BIN_USER))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w("WasteClassifier", "Gemini bin ($m) gagal: ${e.message}")
                }
            }
        }

        if (groqApiKey.isNotBlank()) {
            try {
                return parseBin(callGroq(groqModel.ifBlank { DEFAULT_GROQ_MODEL }, jpeg, BIN_SYSTEM, BIN_USER))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("WasteClassifier", "Groq bin gagal: ${e.message}")
            }
        }

        return null
    }

    private suspend fun callGemini(targetModel: String, jpeg: ByteArray, system: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val genConfig = JSONObject()
                .put("responseMimeType", "application/json")
                .put("temperature", 0.2)
            if (targetModel.startsWith("gemini-3") || targetModel.startsWith("gemini-2.5")) {
                genConfig.put("thinkingConfig", JSONObject().put("thinkingLevel", "low"))
            }

            val body = JSONObject()
                .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", system))))
                .put(
                    "contents",
                    JSONArray().put(
                        JSONObject()
                            .put("role", "user")
                            .put(
                                "parts",
                                JSONArray()
                                    .put(
                                        JSONObject().put(
                                            "inline_data",
                                            JSONObject()
                                                .put("mime_type", "image/jpeg")
                                                .put("data", Base64.encodeToString(jpeg, Base64.NO_WRAP)),
                                        ),
                                    )
                                    .put(JSONObject().put("text", prompt)),
                            ),
                    ),
                )
                .put("generationConfig", genConfig)

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/$targetModel:generateContent")
                .header("x-goog-api-key", apiKey)
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            http.newCall(request).execute().use { res ->
                val text = res.body?.string().orEmpty()
                if (!res.isSuccessful) throw IOException("Gemini HTTP ${res.code}: ${text.take(300)}")
                JSONObject(text)
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")
            }
        }

    private suspend fun callGroq(targetModel: String, jpeg: ByteArray, system: String, prompt: String): String =
        withContext(Dispatchers.IO) {
            val dataUrl = "data:image/jpeg;base64," + Base64.encodeToString(jpeg, Base64.NO_WRAP)
            val messages = JSONArray()
                .put(JSONObject().put("role", "system").put("content", system))
                .put(
                    JSONObject()
                        .put("role", "user")
                        .put(
                            "content",
                            JSONArray()
                                .put(JSONObject().put("type", "text").put("text", prompt))
                                .put(
                                    JSONObject()
                                        .put("type", "image_url")
                                        .put("image_url", JSONObject().put("url", dataUrl)),
                                ),
                        ),
                )

            val body = JSONObject()
                .put("model", targetModel)
                .put("messages", messages)
                .put("temperature", 0.2)
                .put("response_format", JSONObject().put("type", "json_object"))

            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization", "Bearer $groqApiKey")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()

            http.newCall(request).execute().use { res ->
                val text = res.body?.string().orEmpty()
                if (!res.isSuccessful) throw IOException("Groq HTTP ${res.code}: ${text.take(300)}")
                JSONObject(text)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            }
        }

    private fun cleanJson(raw: String): JSONObject {
        val trimmed = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        val jsonText = if (start >= 0 && end > start) trimmed.substring(start, end + 1) else trimmed
        return JSONObject(jsonText)
    }

    private fun parseWaste(raw: String): WasteResult {
        val o = cleanJson(raw)
        val category = o.optString("category").lowercase().takeIf { it in Points.CATEGORIES } ?: "lainnya"
        val confidence = o.getDouble("confidence")
        require(confidence in 0.0..1.0) { "confidence di luar rentang" }
        val binType = o.optString("bin_type").lowercase()
            .takeIf { it in setOf("anorganik", "organik", "kertas", "b3") } ?: "anorganik"
        return WasteResult(
            isWaste = o.getBoolean("is_waste"),
            itemName = o.optString("item_name").ifBlank { "Tidak dikenali" },
            category = category,
            material = o.optString("material").ifBlank { "-" },
            confidence = confidence,
            recyclable = o.optBoolean("recyclable", false),
            disposalInstruction = o.optString("disposal_instruction").ifBlank { "Buang ke tong yang sesuai." },
            binType = binType,
            tips = o.optString("tips"),
        )
    }

    private fun parseBin(raw: String): BinVerifyResult {
        val o = cleanJson(raw)
        val confidence = o.getDouble("confidence")
        require(confidence in 0.0..1.0)
        return BinVerifyResult(isBin = o.getBoolean("is_bin"), confidence = confidence)
    }

    private companion object {
        const val DEFAULT_GROQ_MODEL = "meta-llama/llama-4-scout-17b-16e-instruct"

        val PLASTIC_WORDS = listOf("plastik", "pet", "hdpe", "pp ", "polyethylene", "polypropylene", "pvc")
        val GLASS_WORDS = listOf("kaca", "beling", "glass")

        const val WASTE_SYSTEM =
            "Kamu asisten pemilahan sampah kampus Politeknik Negeri Jakarta. " +
                "Balas HANYA JSON valid tanpa markdown. Jika foto bukan sampah, set is_waste=false. " +
                "PANDUAN MEMBEDAKAN BAHAN (penting): " +
                "(1) Botol minuman transparan yang ringan, dinding tipis, bisa penyok, bertutup ulir plastik, " +
                "berlabel pembungkus, ada ulir/lekukan di badan botol (contoh: air mineral, teh botol, minuman kemasan) " +
                "= PLASTIK (PET). Ini jenis botol paling umum di kampus. " +
                "(2) KACA hanya jika jelas tebal, kaku, sangat bening/berkilau tajam, berat, bentuk khas botol sirup/kecap/" +
                "bir/selai, atau tutupnya logam (kaleng crown cap/ulir logam), atau ada pantulan tajam dan tepi tebal. " +
                "(3) Jika ragu antara plastik dan kaca pada botol minuman, pilih PLASTIK dan turunkan confidence. " +
                "(4) Kaleng minuman = logam. Kertas/kardus = kertas. Sisa makanan = organik. Baterai/kabel/gadget = elektronik. " +
                "Isi field \"visual_evidence\" LEBIH DULU (ciri fisik yang terlihat), baru tentukan kategori." +
                "(5) Masker bekas, tisu bekas, puntung rokok, popok, dan sampah campuran yang tidak bisa didaur ulang = " +
                "category \"lainnya\", recyclable=false, bin_type \"anorganik\". Untuk masker, sarankan dilipat dan dibungkus " +
                "dulu sebelum dibuang, dan jangan sebut gelas atau botol. "

        const val WASTE_USER =
            "Identifikasi sampah di foto ini. Format JSON (urutkan field persis seperti ini): " +
                "{\"visual_evidence\": string (1 kalimat: ciri bahan yang terlihat, mis. tutup, ketebalan, label), " +
                "\"is_waste\": boolean, \"item_name\": string, " +
                "\"category\": \"plastik|kertas|organik|kaca|logam|elektronik|lainnya\", " +
                "\"material\": string, \"confidence\": number 0-1, \"recyclable\": boolean, " +
                "\"disposal_instruction\": string, \"bin_type\": \"anorganik|organik|kertas|b3\", " +
                "\"tips\": string}. Gunakan Bahasa Indonesia yang singkat."

        const val BIN_SYSTEM =
            "Kamu memverifikasi foto tempat sampah. Balas HANYA JSON valid tanpa markdown."

        const val BIN_USER =
            "Apakah foto ini menampilkan tong atau tempat sampah? " +
                "Format JSON: {\"is_bin\": boolean, \"confidence\": number 0-1}."
    }
}
