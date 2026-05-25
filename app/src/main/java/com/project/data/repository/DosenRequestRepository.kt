package com.project.data.repository

import com.project.core.SupabaseClientProvider
import com.project.data.model.ChapterSubmission
import com.project.data.model.DosenReviewItem
import com.project.data.model.DosenSupervisedStudentItem
import com.project.data.model.Profile
import com.project.data.model.SupervisorRequest
import com.project.data.model.ThesisChapter
import com.project.data.model.ThesisChapterUpdate
import io.github.jan.supabase.postgrest.from

class DosenRequestRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getSupervisedStudents(
        lecturerId: Long
    ): List<DosenSupervisedStudentItem> {
        val requests = supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("lecturer_id", lecturerId)
                    eq("status", "accepted")
                }
            }
            .decodeList<SupervisorRequest>()
            .sortedByDescending { it.id }

        return requests.map { request ->
            DosenSupervisedStudentItem(
                request = request,
                student = getStudentProfile(request.studentId)
            )
        }
    }

    suspend fun getReviewItems(
        lecturerId: Long
    ): List<DosenReviewItem> {
        val acceptedRequests = supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("lecturer_id", lecturerId)
                    eq("status", "accepted")
                }
            }
            .decodeList<SupervisorRequest>()

        val result = mutableListOf<DosenReviewItem>()

        acceptedRequests.forEach { request ->
            val student = getStudentProfile(request.studentId)

            val chapters = supabase
                .from("thesis_chapters")
                .select {
                    filter {
                        eq("supervisor_request_id", request.id)
                    }
                }
                .decodeList<ThesisChapter>()
                .sortedBy { it.chapterNumber }

            chapters.forEach { chapter ->
                val submissions = getSubmissions(chapter.id)

                if (chapter.status == "process" || submissions.isNotEmpty()) {
                    result.add(
                        DosenReviewItem(
                            request = request,
                            student = student,
                            chapter = chapter,
                            submissions = submissions
                        )
                    )
                }
            }
        }

        return result.sortedByDescending { item ->
            item.submissions.firstOrNull()?.id ?: 0L
        }
    }

    private suspend fun getStudentProfile(
        studentId: String
    ): Profile? {
        return supabase
            .from("profiles")
            .select {
                filter {
                    eq("id", studentId)
                }
            }
            .decodeList<Profile>()
            .firstOrNull()
    }

    private suspend fun getSubmissions(
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

    suspend fun approveChapter(
        chapterId: Long,
        note: String?
    ) {
        supabase
            .from("thesis_chapters")
            .update(
                ThesisChapterUpdate(
                    status = "approved",
                    lecturerNote = note
                )
            ) {
                filter {
                    eq("id", chapterId)
                }
            }
    }

    suspend fun requestRevision(
        chapterId: Long,
        note: String?
    ) {
        supabase
            .from("thesis_chapters")
            .update(
                ThesisChapterUpdate(
                    status = "revision",
                    lecturerNote = note
                )
            ) {
                filter {
                    eq("id", chapterId)
                }
            }
    }
}