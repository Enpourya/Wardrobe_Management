package com.pourya.wardrobe.utils

import android.content.Context
import android.net.Uri
import com.pourya.wardrobe.data.db.WardrobeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object BackupManager {

    private const val DB_NAME = WardrobeDatabase.DATABASE_NAME
    private const val IMAGES_DIR = "wardrobe_images"

    suspend fun createBackup(context: Context, outputUri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Close DB before backup
            WardrobeDatabase.getInstance(context).close()

            context.contentResolver.openOutputStream(outputUri)?.use { output ->
                ZipOutputStream(output).use { zip ->
                    // Add DB file
                    val dbFile = context.getDatabasePath(DB_NAME)
                    if (dbFile.exists()) {
                        addFileToZip(zip, dbFile, "db/$DB_NAME")
                    }
                    // Add DB WAL
                    val walFile = File(dbFile.path + "-wal")
                    if (walFile.exists()) addFileToZip(zip, walFile, "db/$DB_NAME-wal")
                    val shmFile = File(dbFile.path + "-shm")
                    if (shmFile.exists()) addFileToZip(zip, shmFile, "db/$DB_NAME-shm")

                    // Add images
                    val imagesDir = File(context.filesDir, IMAGES_DIR)
                    if (imagesDir.exists()) {
                        imagesDir.walkTopDown().forEach { file ->
                            if (file.isFile) {
                                val relative = file.relativeTo(context.filesDir).path
                                addFileToZip(zip, file, "files/$relative")
                            }
                        }
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreBackup(context: Context, inputUri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            WardrobeDatabase.getInstance(context).close()

            context.contentResolver.openInputStream(inputUri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        when {
                            entry.name.startsWith("db/") -> {
                                val fileName = entry.name.removePrefix("db/")
                                val outFile = if (fileName == DB_NAME) {
                                    context.getDatabasePath(DB_NAME)
                                } else {
                                    File(context.getDatabasePath(DB_NAME).parent, fileName)
                                }
                                outFile.parentFile?.mkdirs()
                                FileOutputStream(outFile).use { out -> zip.copyTo(out) }
                            }
                            entry.name.startsWith("files/") -> {
                                val relative = entry.name.removePrefix("files/")
                                val outFile = File(context.filesDir, relative)
                                outFile.parentFile?.mkdirs()
                                FileOutputStream(outFile).use { out -> zip.copyTo(out) }
                            }
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBackupFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        return "wardrobe_backup_${sdf.format(Date())}.wbk"
    }

    private fun addFileToZip(zip: ZipOutputStream, file: File, entryName: String) {
        zip.putNextEntry(ZipEntry(entryName))
        FileInputStream(file).use { input -> input.copyTo(zip) }
        zip.closeEntry()
    }
}
