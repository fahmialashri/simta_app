package com.project.data.repository

import com.project.core.SupabaseClientProvider
import com.project.data.model.Lecturer
import com.project.data.model.SupervisorRequest
import com.project.data.model.SupervisorRequestInsert
import com.project.data.model.ThesisChapter
import io.github.jan.supabase.postgrest.from

class SupervisorRequestRepository {

    private val supabase = SupabaseClientProvider.client

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
}