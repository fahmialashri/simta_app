package com.project.data.repository

import com.project.core.SupabaseClientProvider
import com.project.data.model.Lecturer
import com.project.data.model.SupervisorRequest
import com.project.data.model.SupervisorRequestInsert
import com.project.data.model.ThesisChapter
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.net.URLEncoder

class SupervisorRequestRepository {

    private val supabase = SupabaseClientProvider.client

    private val httpClient = HttpClient(Android)

    suspend fun uploadBuktiKrs(
        studentId: String,
        fileName: String,
        mimeType: String,
        fileBytes: ByteArray
    ): String {
        val session = supabase.auth.currentSessionOrNull()
            ?: throw Exception("Session tidak ditemukan. Silakan login ulang.")

        if (fileBytes.isEmpty()) {
            throw Exception("File bukti KRS kosong atau tidak terbaca.")
        }

        val safeFileName = sanitizeFileName(fileName)
        val filePath = "bukti-krs/$studentId/${System.currentTimeMillis()}_$safeFileName"
        val encodedPath = encodeStoragePath(filePath)

        val response: HttpResponse = httpClient.post(
            "${SupabaseClientProvider.SUPABASE_URL}/storage/v1/object/thesis-files/$encodedPath"
        ) {
            header("apikey", SupabaseClientProvider.SUPABASE_KEY)
            header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
            header("x-upsert", "true")
            contentType(ContentType.parse(mimeType))
            setBody(fileBytes)
        }

        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.Created) {
            val errorBody = runCatching { response.bodyAsText() }.getOrNull()
            throw Exception(
                if (errorBody.isNullOrBlank()) {
                    "Gagal upload bukti KRS ke storage."
                } else {
                    "Gagal upload bukti KRS: $errorBody"
                }
            )
        }

        return "${SupabaseClientProvider.SUPABASE_URL}/storage/v1/object/public/thesis-files/$encodedPath"
    }

    suspend fun createRequest(
        studentId: String,
        lecturerId: Long,
        title: String,
        topic: String,
        message: String?
    ) {
        val request = SupervisorRequestInsert(
            studentId = studentId,
            lecturerId = lecturerId,
            title = title,
            topic = topic,
            message = message,
            status = "pending"
        )

        supabase
            .from("supervisor_requests")
            .insert(request)
    }

    suspend fun getMyLatestRequest(studentId: String): SupervisorRequest? {
        return supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("student_id", studentId)
                }
            }
            .decodeList<SupervisorRequest>()
            .sortedByDescending { it.id }
            .firstOrNull()
    }

    suspend fun getLecturerById(lecturerId: Long): Lecturer? {
        return supabase
            .from("lecturers")
            .select {
                filter {
                    eq("id", lecturerId)
                }
            }
            .decodeList<Lecturer>()
            .firstOrNull()
    }

    suspend fun getChaptersByRequest(
        studentId: String,
        requestId: Long
    ): List<ThesisChapter> {
        return supabase
            .from("thesis_chapters")
            .select {
                filter {
                    eq("student_id", studentId)
                    eq("supervisor_request_id", requestId)
                }
            }
            .decodeList<ThesisChapter>()
            .sortedBy { it.chapterNumber }
    }

    private fun sanitizeFileName(fileName: String): String {
        return fileName
            .trim()
            .ifBlank { "bukti_krs.pdf" }
            .replace(Regex("[^A-Za-z0-9._-]"), "_")
    }

    private fun encodeStoragePath(path: String): String {
        return path
            .split("/")
            .joinToString("/") { segment ->
                URLEncoder.encode(segment, "UTF-8")
                    .replace("+", "%20")
            }
    }
}