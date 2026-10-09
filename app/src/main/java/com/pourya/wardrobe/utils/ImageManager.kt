package com.pourya.wardrobe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageManager {

    private const val IMAGES_DIR = "wardrobe_images"
    private const val IMAGE_QUALITY = 85

    suspend fun saveImage(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.filesDir, IMAGES_DIR).also { it.mkdirs() }
            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(dir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                val bitmap = BitmapFactory.decodeStream(input)
                val scaled = scaleBitmap(bitmap, 1024)
                FileOutputStream(file).use { out ->
                    scaled.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, out)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun deleteImage(imagePath: String?) {
        imagePath?.let { File(it).delete() }
    }

    fun getImageFile(imagePath: String?): File? {
        return imagePath?.let { File(it).takeIf { f -> f.exists() } }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxSize && h <= maxSize) return bitmap
        val ratio = w.toFloat() / h.toFloat()
        return if (ratio > 1) {
            Bitmap.createScaledBitmap(bitmap, maxSize, (maxSize / ratio).toInt(), true)
        } else {
            Bitmap.createScaledBitmap(bitmap, (maxSize * ratio).toInt(), maxSize, true)
        }
    }
}
