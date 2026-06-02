package com.project.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.project.core.SupabaseClientProvider
import com.project.data.model.ThesisSubmission
import com.project.data.model.ThesisSubmissionDocument
import com.project.data.model.ThesisSubmissionDocumentInsert
import com.project.data.model.ThesisSubmissionInsert
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ThesisDocumentRepository {

    private val supabase = SupabaseClientProvider.client
    private val bucketName = "thesis-files"

    suspend fun submitThesisRegistration(
        context: Context,
        userId: String,
        stage: String,
        studentName: String?,
        nim: String?,
        phone: String?,
        title: String?,
        titleEnglish: String?,
        supervisor1: String?,
        supervisor2: String?,
        examiner1: String?,
        examiner2: String?,
        files: Map<String, Uri>
    ): Result<ThesisSubmission> {
        return withContext(Dispatchers.IO) {
            try {
                if (userId.isBlank()) {
                    return@withContext Result.failure(
                        Exception("User belum terbaca. Silakan login ulang.")
                    )
                }

                if (stage.isBlank()) {
                    return@withContext Result.failure(
                        Exception("Tahap pendaftaran tidak valid.")
                    )
                }

                if (files.isEmpty()) {
                    return@withContext Result.failure(
                        Exception("Belum ada file yang dipilih.")
                    )
                }

                val submissionInsert = ThesisSubmissionInsert(
                    studentId = userId,
                    stage = stage,
                    studentName = studentName,
                    nim = nim,
                    phone = phone,
                    title = title,
                    titleEnglish = titleEnglish,
                    supervisor1 = supervisor1,
                    supervisor2 = supervisor2,
                    examiner1 = examiner1,
                    examiner2 = examiner2,
                    status = "menunggu_review"
                )

                val submission = supabase
                    .from("thesis_submissions")
                    .insert(submissionInsert) {
                        select()
                    }
                    .decodeSingle<ThesisSubmission>()

                val uploadedDocuments = uploadMultipleDocuments(
                    context = context,
                    userId = userId,
                    stage = stage,
                    files = files
                ).getOrThrow()

                val documentInserts = uploadedDocuments.map { uploaded ->
                    ThesisSubmissionDocumentInsert(
                        submissionId = submission.id,
                        documentKey = uploaded.documentKey,
                        documentName = uploaded.documentName,
                        fileUrl = uploaded.fileUrl,
                        status = "menunggu_review"
                    )
                }

                supabase
                    .from("thesis_submission_documents")
                    .insert(documentInserts)

                Result.success(submission)
            } catch (e: Exception) {
                Result.failure(
                    Exception(e.message ?: "Gagal mengirim pendaftaran ke TU.")
                )
            }
        }
    }

    suspend fun uploadDocument(
        context: Context,
        userId: String,
        stage: String,
        documentKey: String,
        uri: Uri
    ): Result<UploadedThesisDocument> {
        return withContext(Dispatchers.IO) {
            try {
                val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.readBytes()
                } ?: return@withContext Result.failure(
                    Exception("File tidak bisa dibaca.")
                )

                if (bytes.isEmpty()) {
                    return@withContext Result.failure(
                        Exception("File kosong atau tidak terbaca.")
                    )
                }

                val fileName = getFileName(context, uri)
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

                Result.success(
                    UploadedThesisDocument(
                        documentKey = cleanDocumentKey,
                        documentName = fileName,
                        fileUrl = publicUrl
                    )
                )
            } catch (e: Exception) {
                Result.failure(
                    Exception(e.message ?: "Gagal upload dokumen.")
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
                        Exception("Belum ada file yang dipilih.")
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
                                ?: Exception("Gagal upload file $documentKey.")
                        )
                    }

                    uploadedFiles.add(uploadResult.getOrThrow())
                }

                Result.success(uploadedFiles)
            } catch (e: Exception) {
                Result.failure(
                    Exception(e.message ?: "Gagal upload beberapa dokumen.")
                )
            }
        }
    }

    suspend fun getSubmissionsByStage(
        stage: String
    ): List<ThesisSubmission> {
        return supabase
            .from("thesis_submissions")
            .select {
                filter {
                    eq("stage", stage)
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<ThesisSubmission>()
    }

    suspend fun getPendingSubmissions(): List<ThesisSubmission> {
        return supabase
            .from("thesis_submissions")
            .select {
                filter {
                    eq("status", "menunggu_review")
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<ThesisSubmission>()
    }

    suspend fun getDocumentsBySubmissionId(
        submissionId: String
    ): List<ThesisSubmissionDocument> {
        return supabase
            .from("thesis_submission_documents")
            .select {
                filter {
                    eq("submission_id", submissionId)
                }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<ThesisSubmissionDocument>()
    }

    suspend fun updateSubmissionStatus(
        submissionId: String,
        status: String
    ) {
        supabase
            .from("thesis_submissions")
            .update(
                buildJsonObject {
                    put("status", status)
                    put("updated_at", "now()")
                }
            ) {
                filter {
                    eq("id", submissionId)
                }
            }
    }

    suspend fun approveSubmission(
        submissionId: String
    ) {
        updateSubmissionStatus(
            submissionId = submissionId,
            status = "disetujui_tu"
        )
    }

    suspend fun rejectSubmission(
        submissionId: String
    ) {
        updateSubmissionStatus(
            submissionId = submissionId,
            status = "ditolak_tu"
        )
    }

    private fun buildFilePath(
        userId: String,
        stage: String,
        documentKey: String,
        extension: String
    ): String {
        val timestamp = System.currentTimeMillis()

        val safeUserId = userId
            .lowercase()
            .replace(Regex("[^a-z0-9-]"), "_")

        val safeStage = stage
            .lowercase()
            .replace(Regex("[^a-z0-9_-]"), "_")

        val safeDocumentKey = documentKey
            .lowercase()
            .replace(Regex("[^a-z0-9_-]"), "_")

        val safeExtension = extension
            .lowercase()
            .replace(".", "")
            .ifBlank { "pdf" }

        return "$safeUserId/$safeStage/${safeDocumentKey}_$timestamp.$safeExtension"
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
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
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

        if (fileName.isBlank()) {
            fileName = "dokumen_${System.currentTimeMillis()}"
        }

        return fileName
    }
}

data class UploadedThesisDocument(
    val documentKey: String,
    val documentName: String,
    val fileUrl: String
)