package com.example.laum.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

data class FirebaseProfile(
    val uid: String,
    val username: String,
    val email: String,
    val major: String?,
)

object AuthRepository {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUid(): String? = auth.currentUser?.uid

    fun currentEmail(): String? = auth.currentUser?.email

    fun logout() = auth.signOut()

    /**
     * Mengambil profil pengguna aktif dari Firestore dengan fallback ke metadata FirebaseAuth
     * saat cache atau koneksi sedang tidak tersedia.
     */
    suspend fun fetchCurrentProfile(): FirebaseProfile? {
        val user = auth.currentUser ?: return null
        val fallbackName = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
            ?: "Mahasiswa PNJ"
        val fallbackEmail = user.email?.trim().orEmpty()
        return suspendCancellableCoroutine { cont ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { doc ->
                    val username = doc.getString("username")?.takeIf { it.isNotBlank() } ?: fallbackName
                    val email = doc.getString("email")?.trim()?.takeIf { it.isNotBlank() } ?: fallbackEmail
                    val major = doc.getString("major")?.takeIf { it.isNotBlank() }
                    if (cont.isActive) {
                        cont.resume(FirebaseProfile(user.uid, username, email, major))
                    }
                }
                .addOnFailureListener {
                    if (cont.isActive) {
                        cont.resume(FirebaseProfile(user.uid, fallbackName, fallbackEmail, null))
                    }
                }
        }
    }

    fun login(username: String, password: String, onResult: (String?) -> Unit) {
        db.collection("usernames").document(username.trim().lowercase()).get()
            .addOnSuccessListener { doc ->
                val email = doc.getString("email")
                if (!doc.exists() || email == null) {
                    onResult("Username tidak ditemukan")
                    return@addOnSuccessListener
                }
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        onResult(if (task.isSuccessful) null else "Username atau password salah")
                    }
            }
            .addOnFailureListener { onResult("Gagal terhubung, cek koneksi internet") }
    }

    fun register(
        username: String,
        password: String,
        campus: String,
        major: String,
        email: String,
        onResult: (String?) -> Unit
    ) {
        val name = username.trim().lowercase()
        val mail = email.trim()
        db.collection("usernames").document(name).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    onResult("Username sudah dipakai")
                    return@addOnSuccessListener
                }
                auth.createUserWithEmailAndPassword(mail, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user!!.uid
                        val batch = db.batch()
                        batch.set(
                            db.collection("users").document(uid),
                            mapOf(
                                "username" to name,
                                "email" to mail,
                                "campus" to campus,
                                "major" to major
                            )
                        )
                        batch.set(
                            db.collection("usernames").document(name),
                            mapOf("uid" to uid, "email" to mail)
                        )
                        batch.commit()
                            .addOnSuccessListener { onResult(null) }
                            .addOnFailureListener { onResult("Gagal menyimpan data akun") }
                    }
                    .addOnFailureListener { e ->
                        onResult(e.message ?: "Gagal membuat akun")
                    }
            }
            .addOnFailureListener { onResult("Gagal terhubung, cek koneksi internet") }
    }
}