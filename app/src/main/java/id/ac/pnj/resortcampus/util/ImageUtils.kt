package id.ac.pnj.resortcampus.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest

/**
 * Kompres foto sebelum dikirim ke AI (Green IT):
 * sisi terpanjang maks 1024px, JPEG kualitas 70, orientasi EXIF diperbaiki.
 *
 * Perbaikan: decodeStream dengan inJustDecodeBounds=true SELALU mengembalikan null,
 * jadi hasilnya tidak boleh dipakai untuk menilai "foto bisa dibuka atau tidak".
 * Yang dicek adalah bounds.outWidth / outHeight.
 */
object ImageUtils {
    private const val MAX_SIDE = 768
    private const val JPEG_QUALITY = 70

    private fun openStream(context: Context, uri: Uri): InputStream? {
        if (uri.scheme == "file") {
            val path = uri.path ?: return null
            val f = File(path)
            if (f.exists()) return FileInputStream(f)
        }
        return context.contentResolver.openInputStream(uri)
    }

    /** Membaca ukuran gambar saja. Return null kalau file tidak bisa dibuka / bukan gambar. */
    private fun readBounds(context: Context, uri: Uri): BitmapFactory.Options? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val stream = openStream(context, uri) ?: return null
        stream.use { BitmapFactory.decodeStream(it, null, bounds) }
        return if (bounds.outWidth > 0 && bounds.outHeight > 0) bounds else null
    }

    private fun readRotation(context: Context, uri: Uri): Float {
        val stream = openStream(context, uri) ?: return 0f
        return runCatching {
            stream.use {
                when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            }
        }.getOrDefault(0f)
    }

    suspend fun compress(context: Context, uri: Uri): ByteArray = withContext(Dispatchers.Default) {
        val bounds = readBounds(context, uri) ?: throw IOException("Foto tidak bisa dibuka")

        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = openStream(context, uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: throw IOException("Gagal membaca foto")

        val rotation = readRotation(context, uri)

        val scale = MAX_SIDE.toFloat() / maxOf(decoded.width, decoded.height)
        val matrix = Matrix().apply {
            if (scale < 1f) postScale(scale, scale)
            if (rotation != 0f) postRotate(rotation)
        }
        val finalBitmap = if (matrix.isIdentity) decoded
        else Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)

        val out = ByteArrayOutputStream()
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        if (finalBitmap !== decoded) decoded.recycle()
        out.toByteArray()
    }

    /** Membaca Bitmap dari URI hasil CameraX ImageCapture / Galeri dengan koreksi rotasi EXIF. */
    fun decodeOrientedBitmap(context: Context, uri: Uri): Bitmap? = runCatching {
        val bounds = readBounds(context, uri) ?: return@runCatching null

        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = openStream(context, uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: return@runCatching null

        val rotation = readRotation(context, uri)

        if (rotation == 0f) decoded
        else {
            val matrix = Matrix().apply { postRotate(rotation) }
            val rotated = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
            if (rotated !== decoded) decoded.recycle()
            rotated
        }
    }.getOrNull()

    fun sha256(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
}