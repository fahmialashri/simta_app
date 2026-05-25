package com.project.data.repository

import com.project.BuildConfig
import com.project.core.SupabaseClientProvider
import com.project.data.model.ChapterSubmission
import com.project.data.model.ChapterSubmissionInsert
import com.project.data.model.ThesisChapter
import com.project.data.model.ThesisChapterUpdate
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.ktor.http.ContentType

class BimbinganRepository {

    private val supabase = SupabaseClientProvider.client
    private val bucketName = "chapter-files"

    suspend fun getChapters(
        studentId: String,
        supervisorRequestId: Long
    ): List<ThesisChapter> {
        return supabase
            .from("thesis_chapters")
            .select {
                filter {
                    eq("student_id", studentId)
                    eq("supervisor_request_id", supervisorRequestId)
                }
            }
            .decodeList<ThesisChapter>()
            .sortedBy { it.chapterNumber }
    }

    suspend fun getSubmissions(
        chapterId: Long
    ): List<ChapterSubmission> {
        return supabase
            .from("chapter_submissions")
            .select {
                filter {
                    eq("chapter_id", chapterId)
                }
            }
            .decodeList<ChapterSubmission>()
            .sortedByDescending { it.id }
    }

    suspend fun uploadChapterFile(
        studentId: String,
        chapterId: Long,
        fileName: String,
        bytes: ByteArray,
        mimeType: String?
    ): Pair<String, String> {
        val safeFileName = fileName
            .replace(" ", "_")
            .replace("/", "_")
            .replace("\\", "_")

        val path = "$studentId/$chapterId/${System.currentTimeMillis()}_$safeFileName"

        val finalContentType = try {
            ContentType.parse(mimeType ?: "application/octet-stream")
        } catch (e: Exception) {
            ContentType.Application.OctetStream
        }

        supabase.storage
            .from(bucketName)
            .upload(path, bytes) {
                upsert = false
                contentType = finalContentType
            }

        val publicUrl = buildPublicStorageUrl(path)

        return path to publicUrl
    }

    suspend fun createSubmission(
        chapterId: Long,
        studentId: String,
        supervisorRequestId: Long,
        fileName: String?,
        filePath: String?,
        fileUrl: String?,
        driveUrl: String?,
        note: String?
    ) {
        val payload = ChapterSubmissionInsert(
            chapterId = chapterId,
            studentId = studentId,
            supervisorRequestId = supervisorRequestId,
            fileName = fileName,
            filePath = filePath,
            fileUrl = fileUrl,
            driveUrl = driveUrl,
            note = note
        )

        supabase
            .from("chapter_submissions")
            .insert(payload)

        supabase
            .from("thesis_chapters")
            .update(
                ThesisChapterUpdate(
                    status = "process",
                    lecturerNote = null
                )
            ) {
                filter {
                    eq("id", chapterId)
                    eq("student_id", studentId)
                    eq("supervisor_request_id", supervisorRequestId)
                }
            }
    }

    private fun buildPublicStorageUrl(path: String): String {
        val encodedPath = path
            .split("/")
            .joinToString("/") { segment ->
                java.net.URLEncoder
                    .encode(segment, "UTF-8")
                    .replace("+", "%20")
            }

        return "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/$bucketName/$encodedPath"
    }
}