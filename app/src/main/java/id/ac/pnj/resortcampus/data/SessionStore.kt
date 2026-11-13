package id.ac.pnj.resortcampus.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

/** Menyimpan id user yang sedang login (pengganti session cookie di versi web). */
class SessionStore(private val context: Context) {
    private val keyUserId = stringPreferencesKey("user_id")

    val userIdFlow: Flow<String?> = context.dataStore.data.map { it[keyUserId] }

    suspend fun currentUserId(): String? = userIdFlow.first()

    suspend fun save(userId: String) {
        context.dataStore.edit { it[keyUserId] = userId }
    }

    suspend fun clear() {
        context.dataStore.edit { it.remove(keyUserId) }
    }
}
