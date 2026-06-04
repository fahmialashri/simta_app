package com.project.data.repository

import com.project.BuildConfig
import com.project.core.SupabaseClientProvider
import com.project.data.model.BimbinganTargetLecturer
import com.project.data.model.ChapterSubmission
import com.project.data.model.ChapterSubmissionInsert
import com.project.data.model.Lecturer
import com.project.data.model.SupervisorRequest
import com.project.data.model.ThesisChapter
import com.project.data.model.ThesisChapterUpdate
import com.project.data.model.ThesisSubmission
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

    suspend fun getGuidanceTargets(
        studentId: String,
        supervisorRequestId: Long
    ): List<BimbinganTargetLecturer> {
        val result = mutableListOf<BimbinganTargetLecturer>()

        val activeRequest = supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("id", supervisorRequestId)
                    eq("student_id", studentId)
                }
            }
            .decodeList<SupervisorRequest>()
            .firstOrNull()

        if (activeRequest != null) {
            val lecturer = getLecturerById(activeRequest.lecturerId)

            if (lecturer != null) {
                result.add(
                    BimbinganTargetLecturer(
                        lecturerId = lecturer.id,
                        lecturerName = lecturer.fullName,
                        supervisorRole = "Pembimbing 1"
                    )
                )
            }
        }

        val latestSubmission = supabase
            .from("thesis_submissions")
            .select {
                filter {
                    eq("student_id", studentId)
                }
            }
            .decodeList<ThesisSubmission>()
            .sortedByDescending { it.createdAt.orEmpty() }
            .firstOrNull { !it.supervisor2.isNullOrBlank() }

        val supervisor2Name = latestSubmission?.supervisor2.orEmpty()

        if (supervisor2Name.isNotBlank()) {
            val supervisor2Lecturer = findLecturerByName(supervisor2Name)

            if (supervisor2Lecturer != null) {
                result.add(
                    BimbinganTargetLecturer(
                        lecturerId = supervisor2Lecturer.id,
                        lecturerName = supervisor2Lecturer.fullName,
                        supervisorRole = "Pembimbing 2"
                    )
                )
            }
        }

        return result.distinctBy { it.lecturerId }
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
        note: String?,
        targetLecturerId: Long?,
        targetSupervisorRole: String?
    ) {
        val payload = ChapterSubmissionInsert(
            chapterId = chapterId,
            studentId = studentId,
            supervisorRequestId = supervisorRequestId,
            fileName = fileName,
            filePath = filePath,
            fileUrl = fileUrl,
            driveUrl = driveUrl,
            note = note,
            targetLecturerId = targetLecturerId,
            targetSupervisorRole = targetSupervisorRole
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

    private suspend fun getLecturerById(
        lecturerId: Long
    ): Lecturer? {
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

    private suspend fun findLecturerByName(
        storedName: String
    ): Lecturer? {
        val normalizedStoredName = storedName.normalizeLecturerName()

        if (normalizedStoredName.isBlank()) {
            return null
        }

        return supabase
            .from("lecturers")
            .select()
            .decodeList<Lecturer>()
            .firstOrNull { lecturer ->
                val lecturerNames = listOf(
                    lecturer.name,
                    lecturer.fullName,
                    "${lecturer.name}, ${lecturer.title.orEmpty()}",
                    "${lecturer.name} ${lecturer.title.orEmpty()}"
                ).map { it.normalizeLecturerName() }

                lecturerNames.any { normalizedLecturerName ->
                    normalizedLecturerName == normalizedStoredName ||
                            normalizedLecturerName.contains(normalizedStoredName) ||
                            normalizedStoredName.contains(normalizedLecturerName)
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

private fun String.normalizeLecturerName(): String {
    return trim()
        .lowercase()
        .replace(".", "")
        .replace(",", "")
        .replace("dr ", "")
        .replace("drs ", "")
        .replace("dra ", "")
        .replace("ir ", "")
        .replace("prof ", "")
        .replace("s t", "")
        .replace("st", "")
        .replace("s si", "")
        .replace("ssi", "")
        .replace("s kom", "")
        .replace("skom", "")
        .replace("m kom", "")
        .replace("mkom", "")
        .replace("m si", "")
        .replace("msi", "")
        .replace("m t", "")
        .replace("mt", "")
        .replace("mpd", "")
        .replace("phd", "")
        .replace(Regex("\\s+"), " ")
        .trim()
}