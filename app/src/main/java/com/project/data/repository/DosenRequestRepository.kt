package com.project.data.repository

import com.project.core.SupabaseClientProvider
import com.project.data.model.ChapterSubmission
import com.project.data.model.DosenReviewItem
import com.project.data.model.DosenSupervisedStudentItem
import com.project.data.model.Lecturer
import com.project.data.model.Profile
import com.project.data.model.SupervisorRequest
import com.project.data.model.ThesisChapter
import com.project.data.model.ThesisChapterUpdate
import com.project.data.model.ThesisSubmission
import io.github.jan.supabase.postgrest.from

class DosenRequestRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getSupervisedStudents(
        lecturerId: Long
    ): List<DosenSupervisedStudentItem> {
        val lecturer = getLecturerById(lecturerId)

        val primaryRequests = getAcceptedRequestsByLecturerId(lecturerId)
            .map { request ->
                DosenSupervisedStudentItem(
                    request = request,
                    student = getStudentProfile(request.studentId),
                    supervisorRole = "Pembimbing 1"
                )
            }

        val secondaryRequestsFromTarget = getTargetedSubmissionsByLecturerId(lecturerId)
            .mapNotNull { submission ->
                getSupervisorRequestById(submission.supervisorRequestId)
            }
            .filter { request ->
                request.status == "accepted"
            }
            .map { request ->
                DosenSupervisedStudentItem(
                    request = request,
                    student = getStudentProfile(request.studentId),
                    supervisorRole = "Pembimbing 2"
                )
            }

        val secondaryRequestsFromSubmission = getAcceptedRequestsAsSecondSupervisor(
            lecturer = lecturer,
            primaryRequestIds = primaryRequests.map { it.request.id }.toSet()
        ).map { request ->
            DosenSupervisedStudentItem(
                request = request,
                student = getStudentProfile(request.studentId),
                supervisorRole = "Pembimbing 2"
            )
        }

        return (primaryRequests + secondaryRequestsFromTarget + secondaryRequestsFromSubmission)
            .distinctBy { item ->
                "${item.request.id}_${item.supervisorRole}"
            }
            .sortedWith(
                compareByDescending<DosenSupervisedStudentItem> {
                    it.supervisorRole == "Pembimbing 1"
                }.thenByDescending {
                    it.request.id
                }
            )
    }

    suspend fun getReviewItems(
        lecturerId: Long
    ): List<DosenReviewItem> {
        val lecturer = getLecturerById(lecturerId)

        val primaryRequests = getAcceptedRequestsByLecturerId(lecturerId)
            .map { request ->
                request to "Pembimbing 1"
            }

        val primaryRequestIds = primaryRequests.map { it.first.id }.toSet()

        val secondaryRequestsFromSubmission = getAcceptedRequestsAsSecondSupervisor(
            lecturer = lecturer,
            primaryRequestIds = primaryRequestIds
        ).map { request ->
            request to "Pembimbing 2"
        }

        val acceptedRequests = (primaryRequests + secondaryRequestsFromSubmission)
            .distinctBy { pair ->
                "${pair.first.id}_${pair.second}"
            }

        val result = mutableListOf<DosenReviewItem>()

        acceptedRequests.forEach { pair ->
            val request = pair.first
            val supervisorRole = pair.second
            val student = getStudentProfile(request.studentId)
            val chapters = getChaptersBySupervisorRequestId(request.id)

            chapters.forEach { chapter ->
                val submissions = getSubmissionsForLecturer(
                    chapterId = chapter.id,
                    lecturerId = lecturerId,
                    includeLegacyWithoutTarget = supervisorRole == "Pembimbing 1"
                )

                if (submissions.isNotEmpty()) {
                    result.add(
                        DosenReviewItem(
                            request = request,
                            student = student,
                            chapter = chapter,
                            submissions = submissions,
                            supervisorRole = supervisorRole
                        )
                    )
                }
            }
        }

        val targetedSubmissions = getTargetedSubmissionsByLecturerId(lecturerId)

        targetedSubmissions.forEach { submission ->
            val chapter = getChapterById(submission.chapterId) ?: return@forEach
            val request = getSupervisorRequestById(submission.supervisorRequestId) ?: return@forEach
            val student = getStudentProfile(request.studentId)

            val submissions = getSubmissionsForLecturer(
                chapterId = chapter.id,
                lecturerId = lecturerId,
                includeLegacyWithoutTarget = false
            )

            if (submissions.isNotEmpty()) {
                result.add(
                    DosenReviewItem(
                        request = request,
                        student = student,
                        chapter = chapter,
                        submissions = submissions,
                        supervisorRole = submission.targetSupervisorRole ?: "Pembimbing 2"
                    )
                )
            }
        }

        return result
            .distinctBy { item ->
                "${item.chapter.id}_${item.supervisorRole}"
            }
            .sortedByDescending { item ->
                item.submissions.firstOrNull()?.id ?: item.chapter.id
            }
    }

    private suspend fun getAcceptedRequestsByLecturerId(
        lecturerId: Long
    ): List<SupervisorRequest> {
        return supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("lecturer_id", lecturerId)
                    eq("status", "accepted")
                }
            }
            .decodeList<SupervisorRequest>()
    }

    private suspend fun getAcceptedRequestsAsSecondSupervisor(
        lecturer: Lecturer?,
        primaryRequestIds: Set<Long>
    ): List<SupervisorRequest> {
        if (lecturer == null) {
            return emptyList()
        }

        val lecturerKeys = buildLecturerMatchingKeys(lecturer)

        if (lecturerKeys.isEmpty()) {
            return emptyList()
        }

        val thesisSubmissions = supabase
            .from("thesis_submissions")
            .select()
            .decodeList<ThesisSubmission>()
            .sortedByDescending { submission ->
                submission.createdAt.orEmpty()
            }

        val matchedStudentIds = thesisSubmissions
            .filter { submission ->
                val submissionNames = listOfNotNull(
                    submission.supervisor2,
                    submission.examiner1,
                    submission.examiner2
                )

                submissionNames.any { storedName ->
                    isSameLecturerName(
                        lecturerKeys = lecturerKeys,
                        storedName = storedName
                    )
                }
            }
            .map { submission ->
                submission.studentId
            }
            .distinct()

        if (matchedStudentIds.isEmpty()) {
            return emptyList()
        }

        val requests = mutableListOf<SupervisorRequest>()

        matchedStudentIds.forEach { studentId ->
            val acceptedRequest = getLatestAcceptedRequestByStudentId(studentId)

            if (acceptedRequest != null && acceptedRequest.id !in primaryRequestIds) {
                requests.add(acceptedRequest)
            }
        }

        return requests.distinctBy { request ->
            request.id
        }
    }

    private suspend fun getTargetedSubmissionsByLecturerId(
        lecturerId: Long
    ): List<ChapterSubmission> {
        return supabase
            .from("chapter_submissions")
            .select {
                filter {
                    eq("target_lecturer_id", lecturerId)
                }
            }
            .decodeList<ChapterSubmission>()
            .sortedByDescending { submission ->
                submission.id
            }
    }

    private suspend fun getSubmissionsForLecturer(
        chapterId: Long,
        lecturerId: Long,
        includeLegacyWithoutTarget: Boolean
    ): List<ChapterSubmission> {
        val submissions = supabase
            .from("chapter_submissions")
            .select {
                filter {
                    eq("chapter_id", chapterId)
                }
            }
            .decodeList<ChapterSubmission>()

        return submissions
            .filter { submission ->
                submission.targetLecturerId == lecturerId ||
                        (includeLegacyWithoutTarget && submission.targetLecturerId == null)
            }
            .sortedByDescending { submission ->
                submission.id
            }
    }

    private fun buildLecturerMatchingKeys(
        lecturer: Lecturer
    ): Set<String> {
        return listOf(
            lecturer.name,
            lecturer.fullName,
            "${lecturer.name}, ${lecturer.title.orEmpty()}",
            "${lecturer.name} ${lecturer.title.orEmpty()}"
        )
            .map { value ->
                value.normalizeLecturerName()
            }
            .filter { value ->
                value.isNotBlank()
            }
            .toSet()
    }

    private fun isSameLecturerName(
        lecturerKeys: Set<String>,
        storedName: String
    ): Boolean {
        val normalizedStoredName = storedName.normalizeLecturerName()

        if (normalizedStoredName.isBlank()) {
            return false
        }

        if (normalizedStoredName in lecturerKeys) {
            return true
        }

        return lecturerKeys.any { lecturerName ->
            lecturerName.isNotBlank() &&
                    (
                            normalizedStoredName.contains(lecturerName) ||
                                    lecturerName.contains(normalizedStoredName)
                            )
        }
    }

    private suspend fun getLatestAcceptedRequestByStudentId(
        studentId: String
    ): SupervisorRequest? {
        return supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("student_id", studentId)
                    eq("status", "accepted")
                }
            }
            .decodeList<SupervisorRequest>()
            .maxByOrNull { request ->
                request.id
            }
    }

    private suspend fun getSupervisorRequestById(
        supervisorRequestId: Long
    ): SupervisorRequest? {
        return supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("id", supervisorRequestId)
                }
            }
            .decodeList<SupervisorRequest>()
            .firstOrNull()
    }

    private suspend fun getChaptersBySupervisorRequestId(
        supervisorRequestId: Long
    ): List<ThesisChapter> {
        return supabase
            .from("thesis_chapters")
            .select {
                filter {
                    eq("supervisor_request_id", supervisorRequestId)
                }
            }
            .decodeList<ThesisChapter>()
            .sortedBy { chapter ->
                chapter.chapterNumber
            }
    }

    private suspend fun getChapterById(
        chapterId: Long
    ): ThesisChapter? {
        return supabase
            .from("thesis_chapters")
            .select {
                filter {
                    eq("id", chapterId)
                }
            }
            .decodeList<ThesisChapter>()
            .firstOrNull()
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