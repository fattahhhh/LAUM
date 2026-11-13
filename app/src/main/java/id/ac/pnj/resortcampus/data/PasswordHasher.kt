package id.ac.pnj.resortcampus.data

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hash password dengan PBKDF2-HMAC-SHA256 + salt acak.
 * Format tersimpan: `pbkdf2$iterasi$saltBase64$hashBase64`.
 */
object PasswordHasher {
    private const val ITERATIONS = 60_000
    private const val KEY_LENGTH = 256
    private const val SALT_BYTES = 16

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val derived = derive(password, salt, ITERATIONS)
        val enc = Base64.getEncoder()
        return "pbkdf2\$$ITERATIONS\$${enc.encodeToString(salt)}\$${enc.encodeToString(derived)}"
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split('$')
        if (parts.size != 4 || parts[0] != "pbkdf2") return false
        val iterations = parts[1].toIntOrNull() ?: return false
        val dec = Base64.getDecoder()
        val salt = dec.decode(parts[2])
        val expected = dec.decode(parts[3])
        val actual = derive(password, salt, iterations)
        return MessageDigest.isEqual(expected, actual)
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}
