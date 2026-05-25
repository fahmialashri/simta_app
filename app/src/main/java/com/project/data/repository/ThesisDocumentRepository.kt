package com.project.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.project.core.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThesisDocumentRepository {

    private val supabase = SupabaseClientProvider.client
    private val bucketName = "thesis-files"

    suspend fun uploadDocument(
        context: Context,
        userId: String,
        stage: String,
        documentKey: String,
        uri: Uri
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.readBytes()
                } ?: return@withContext Result.failure(
                    Exception("File tidak bisa dibaca")
                )

                val fileExtension = getFileExtension(context, uri)
                val safeExtension = fileExtension.ifBlank { "pdf" }

                val cleanUserId = userId.trim()
                val cleanStage = stage.trim()
                val cleanDocumentKey = documentKey.trim()

                if (cleanUserId.isBlank()) {
                    return@withContext Result.failure(
                        Exception("User ID kosong. Silakan login ulang.")
                    )
                }

                if (cleanStage.isBlank()) {
                    return@withContext Result.failure(
                        Exception("Tahap pengajuan kosong.")
                    )
                }

                if (cleanDocumentKey.isBlank()) {
                    return@withContext Result.failure(
                        Exception("Nama dokumen kosong.")
                    )
                }

                val filePath = buildFilePath(
                    userId = cleanUserId,
                    stage = cleanStage,
                    documentKey = cleanDocumentKey,
                    extension = safeExtension
                )

                supabase.storage
                    .from(bucketName)
                    .upload(
                        path = filePath,
                        data = bytes
                    ) {
                        upsert = true
                    }

                val publicUrl = supabase.storage
                    .from(bucketName)
                    .publicUrl(filePath)

                Result.success(publicUrl)
            } catch (e: Exception) {
                Result.failure(
                    Exception(e.message ?: "Gagal upload dokumen")
                )
            }
        }
    }

    suspend fun uploadMultipleDocuments(
        context: Context,
        userId: String,
        stage: String,
        files: Map<String, Uri>
    ): Result<List<UploadedThesisDocument>> {
        return withContext(Dispatchers.IO) {
            try {
                if (files.isEmpty()) {
                    return@withContext Result.failure(
                        Exception("Belum ada file yang dipilih")
                    )
                }

                val uploadedFiles = mutableListOf<UploadedThesisDocument>()

                files.forEach { entry ->
                    val documentKey = entry.key
                    val uri = entry.value

                    val uploadResult = uploadDocument(
                        context = context,
                        userId = userId,
                        stage = stage,
                        documentKey = documentKey,
                        uri = uri
                    )

                    if (uploadResult.isFailure) {
                        return@withContext Result.failure(
                            uploadResult.exceptionOrNull()
                                ?: Exception("Gagal upload file $documentKey")
                        )
                    }

                    uploadedFiles.add(
                        UploadedThesisDocument(
                            documentKey = documentKey,
                            fileUrl = uploadResult.getOrThrow()
                        )
                    )
                }

                Result.success(uploadedFiles)
            } catch (e: Exception) {
                Result.failure(
                    Exception(e.message ?: "Gagal upload beberapa dokumen")
                )
            }
        }
    }

    private fun buildFilePath(
        userId: String,
        stage: String,
        documentKey: String,
        extension: String
    ): String {
        val timestamp = System.currentTimeMillis()

        val safeStage = stage
            .lowercase()
            .replace(" ", "_")

        val safeDocumentKey = documentKey
            .lowercase()
            .replace(" ", "_")

        val safeExtension = extension
            .lowercase()
            .replace(".", "")

        return "$userId/$safeStage/${safeDocumentKey}_$timestamp.$safeExtension"
    }

    private fun getFileExtension(
        context: Context,
        uri: Uri
    ): String {
        val mimeType = context.contentResolver.getType(uri)

        return when (mimeType) {
            "application/pdf" -> "pdf"
            "image/jpeg" -> "jpg"
            "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> {
                val fileName = getFileName(context, uri)
                fileName.substringAfterLast('.', "")
            }
        }
    }

    private fun getFileName(
        context: Context,
        uri: Uri
    ): String {
        var fileName = ""

        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (nameIndex >= 0 && it.moveToFirst()) {
                fileName = it.getString(nameIndex).orEmpty()
            }
        }

        if (fileName.isBlank()) {
            fileName = uri.lastPathSegment.orEmpty()
        }

        return fileName
    }
}

data class UploadedThesisDocument(
    val documentKey: String,
    val fileUrl: String
)