package com.example.laum.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() = auth.signOut()

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