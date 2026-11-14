package id.ac.pnj.resortcampus.util

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File
import java.io.FileOutputStream

enum class AppLanguage(val label: String) {
    ID("Bahasa Indonesia"),
    EN("English"),
}

enum class NotifType { REMINDER, COMPETITION, ACHIEVEMENT }

object SettingsStore {
    private lateinit var prefs: SharedPreferences

    var language by mutableStateOf(AppLanguage.ID)
        private set
    var reminder by mutableStateOf(false)
        private set
    var competition by mutableStateOf(false)
        private set
    var achievement by mutableStateOf(false)
        private set
    var profilePhotoVersion by mutableIntStateOf(0)
        private set
    var funFactIndex by mutableIntStateOf(0)
        private set

    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences("resort_settings", Context.MODE_PRIVATE)
        language = runCatching { AppLanguage.valueOf(prefs.getString("lang", AppLanguage.ID.name).orEmpty()) }
            .getOrDefault(AppLanguage.ID)
        reminder = prefs.getBoolean("notif_reminder", false)
        competition = prefs.getBoolean("notif_competition", false)
        achievement = prefs.getBoolean("notif_achievement", false)
        val nextFact = (prefs.getInt("fun_fact_index", -1) + 1).coerceAtLeast(0)
        funFactIndex = nextFact
        prefs.edit().putInt("fun_fact_index", nextFact).apply()
    }

    fun advanceFunFact(): Int {
        val next = (funFactIndex + 1).coerceAtLeast(0)
        funFactIndex = next
        if (::prefs.isInitialized) {
            prefs.edit().putInt("fun_fact_index", next).apply()
        }
        return next
    }

    fun setFunFact(index: Int) {
        val safe = index.coerceAtLeast(0)
        funFactIndex = safe
        if (::prefs.isInitialized) {
            prefs.edit().putInt("fun_fact_index", safe).apply()
        }
    }

    fun changeLanguage(value: AppLanguage) {
        language = value
        if (::prefs.isInitialized) {
            prefs.edit().putString("lang", value.name).apply()
        }
    }

    fun getProfilePhotoPath(userId: String?): String? {
        if (userId.isNullOrBlank() || !::prefs.isInitialized) return null
        val path = prefs.getString("avatar_$userId", null) ?: return null
        return if (File(path).exists()) path else null
    }

    fun saveProfilePhoto(context: Context, userId: String, sourceUri: Uri): Boolean {
        init(context)
        val bmp = ImageUtils.decodeOrientedBitmap(context, sourceUri) ?: return false
        val outFile = File(context.filesDir, "avatar_${userId.replace(Regex("[^a-zA-Z0-9_-]"), "_")}.jpg")
        val ok = runCatching {
            FileOutputStream(outFile).use { out ->
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            true
        }.getOrDefault(false)
        if (ok) {
            prefs.edit().putString("avatar_$userId", outFile.absolutePath).apply()
            profilePhotoVersion++
        }
        return ok
    }

    fun enabled(type: NotifType): Boolean = when (type) {
        NotifType.REMINDER -> reminder
        NotifType.COMPETITION -> competition
        NotifType.ACHIEVEMENT -> achievement
    }

    fun setEnabled(type: NotifType, value: Boolean) {
        when (type) {
            NotifType.REMINDER -> reminder = value
            NotifType.COMPETITION -> competition = value
            NotifType.ACHIEVEMENT -> achievement = value
        }
        prefs.edit().putBoolean("notif_" + type.name.lowercase(), value).apply()
    }
}