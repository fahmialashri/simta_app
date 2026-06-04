package com.project.data.repository

import com.project.core.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class KaprodiRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getSupervisorRequestsByDepartment(
        departmentId: Long
    ): List<KaprodiSubmissionData> {
        val requests = supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("is_archived", false)
                }
            }
            .decodeList<SupervisorRequestRow>()

        return requests.mapNotNull { request ->
            val student = getProfileById(request.studentId)
            val lecturer = getLecturerById(request.lecturerId)

            if (lecturer == null) {
                null
            } else if (lecturer.departmentId != departmentId) {
                null
            } else {
                val documents = getStudentDocuments(request.studentId)

                val krsFileUrl = documents
                    .firstOrNull {
                        it.documentKey == "krs_pengambilan_skripsi"
                    }
                    ?.fileUrl

                val proposalFileUrl = documents
                    .firstOrNull {
                        it.documentKey == "file_proposal_skripsi"
                    }
                    ?.fileUrl

                val approvalFileUrl = documents
                    .firstOrNull {
                        it.documentKey == "bukti_persetujuan_pembimbing"
                    }
                    ?.fileUrl

                val recommendations = getRecommendedLecturers(
                    departmentId = lecturer.departmentId,
                    expertise = request.topic,
                    excludeLecturerId = lecturer.id
                )

                val selectedRecommendation = request.recommendedLecturerId
                    ?.let { recommendationId ->
                        getLecturerById(recommendationId)
                    }

                KaprodiSubmissionData(
                    id = request.id,
                    studentId = request.studentId,
                    studentName = student?.fullName ?: "Mahasiswa tidak ditemukan",
                    nim = student?.nim ?: "-",
                    title = request.title,
                    topic = request.topic,
                    lecturerId = request.lecturerId,
                    lecturerName = lecturer.fullName,
                    lecturerQuota = lecturer.quota,
                    lecturerCurrentStudents = lecturer.currentStudents,
                    lecturerRemainingQuota = lecturer.remainingQuota.coerceAtLeast(0),
                    lecturerIsFull = lecturer.isFull,
                    status = request.status,
                    note = request.lecturerNote,
                    recommendedLecturerId = request.recommendedLecturerId,
                    recommendedLecturerName = selectedRecommendation?.fullName,
                    recommendationNote = request.recommendationNote,
                    krsFileUrl = krsFileUrl,
                    proposalFileUrl = proposalFileUrl,
                    approvalFileUrl = approvalFileUrl,
                    recommendedLecturers = recommendations
                )
            }
        }.sortedWith(
            compareBy<KaprodiSubmissionData> {
                when (it.status.lowercase()) {
                    "pending" -> 0
                    "accepted" -> 1
                    "rejected" -> 2
                    else -> 3
                }
            }.thenByDescending { it.id }
        )
    }

    suspend fun getStudentTrackingByDepartment(
        departmentId: Long
    ): List<KaprodiStudentTrackingData> {
        val students = supabase
            .from("profiles")
            .select {
                filter {
                    eq("role", "mahasiswa")
                    eq("department_id", departmentId)
                }
            }
            .decodeList<ProfileTrackingRow>()

        val acceptedRequests = supabase
            .from("supervisor_requests")
            .select()
            .decodeList<SupervisorRequestRow>()
            .filter { request ->
                request.status.equals("accepted", ignoreCase = true) &&
                        !request.isArchived &&
                        students.any { student ->
                            student.id == request.studentId
                        }
            }
            .groupBy { request ->
                request.studentId
            }
            .mapValues { entry ->
                entry.value.maxByOrNull { request ->
                    request.id
                }
            }

        val lecturers = supabase
            .from("lecturers")
            .select()
            .decodeList<LecturerMiniRow>()
            .associateBy { lecturer ->
                lecturer.id
            }

        val acceptedRequestIds = acceptedRequests.values
            .mapNotNull { request ->
                request?.id
            }
            .toSet()

        val chapters = runCatching {
            supabase
                .from("thesis_chapters")
                .select()
                .decodeList<ThesisChapterTrackingRow>()
                .filter { chapter ->
                    chapter.supervisorRequestId != null &&
                            acceptedRequestIds.contains(chapter.supervisorRequestId)
                }
        }.getOrDefault(emptyList())

        val submissions = runCatching {
            supabase
                .from("thesis_submissions")
                .select()
                .decodeList<ThesisSubmissionTrackingRow>()
                .filter { submission ->
                    students.any { student ->
                        student.id == submission.studentId
                    }
                }
        }.getOrDefault(emptyList())

        return students.map { student ->
            val request = acceptedRequests[student.id]
            val lecturer = request?.lecturerId?.let { lecturerId ->
                lecturers[lecturerId]
            }

            val latestChapter = if (request != null) {
                chapters
                    .filter { chapter ->
                        chapter.supervisorRequestId == request.id
                    }
                    .maxOfOrNull { chapter ->
                        chapter.chapterNumber ?: 0
                    }
                    ?: 0
            } else {
                0
            }

            val studentSubmissions = submissions.filter { submission ->
                submission.studentId == student.id
            }

            KaprodiStudentTrackingData(
                studentId = student.id,
                studentName = student.fullName ?: "Nama tidak tersedia",
                nim = student.nim ?: "-",
                departmentId = student.departmentId,
                thesisTitle = request?.title,
                lecturerName = lecturer?.fullName,
                latestChapter = latestChapter,
                totalSubmissions = studentSubmissions.size,
                seminarProposalStatus = getLatestStageStatus(
                    submissions = studentSubmissions,
                    stage = "seminar_proposal"
                ),
                revisiSeminarProposalStatus = getLatestStageStatus(
                    submissions = studentSubmissions,
                    stage = "revisi_seminar_proposal"
                ),
                kolokiumStatus = getLatestStageStatus(
                    submissions = studentSubmissions,
                    stage = "kolokium"
                ),
                revisiKolokiumStatus = getLatestStageStatus(
                    submissions = studentSubmissions,
                    stage = "revisi_kolokium"
                ),
                yudisiumStatus = getLatestStageStatus(
                    submissions = studentSubmissions,
                    stage = "yudisium"
                )
            )
        }.sortedBy { student ->
            student.studentName
        }
    }

    suspend fun saveLecturerRecommendation(
        requestId: Long,
        lecturerId: Long,
        note: String?
    ) {
        val lecturer = getLecturerById(lecturerId)
            ?: throw Exception("Dosen rekomendasi tidak ditemukan.")

        if (lecturer.isFull) {
            throw Exception("Dosen rekomendasi sudah penuh. Pilih dosen lain.")
        }

        supabase
            .from("supervisor_requests")
            .update(
                SupervisorRequestRecommendationUpdate(
                    recommendedLecturerId = lecturerId,
                    recommendationNote = note
                )
            ) {
                filter {
                    eq("id", requestId)
                }
            }
    }

    suspend fun approveRequest(requestId: Long) {
        val request = getRequestById(requestId)
            ?: throw Exception("Data pengajuan tidak ditemukan.")

        val selectedLecturerId = request.recommendedLecturerId ?: request.lecturerId

        val lecturer = getLecturerById(selectedLecturerId)
            ?: throw Exception("Data dosen tidak ditemukan.")

        if (lecturer.isFull) {
            val recommendations = getRecommendedLecturers(
                departmentId = lecturer.departmentId,
                expertise = request.topic,
                excludeLecturerId = lecturer.id
            )

            val recommendationText = if (recommendations.isEmpty()) {
                "Belum ada dosen rekomendasi yang kuotanya tersedia."
            } else {
                recommendations.joinToString(separator = "\n") { recommendation ->
                    "- ${recommendation.fullName} | Sisa kuota: ${recommendation.remainingQuota} (${recommendation.currentStudents}/${recommendation.quota})"
                }
            }

            throw Exception(
                "Kuota ${lecturer.fullName} sudah penuh (${lecturer.currentStudents}/${lecturer.quota}).\n\nRekomendasi dosen:\n$recommendationText"
            )
        }

        val finalNote = if (request.recommendedLecturerId != null) {
            "Disetujui oleh Kaprodi dengan dosen rekomendasi: ${lecturer.fullName}"
        } else {
            "Disetujui oleh Kaprodi"
        }

        supabase
            .from("supervisor_requests")
            .update(
                SupervisorRequestApproveUpdate(
                    status = "accepted",
                    lecturerId = selectedLecturerId,
                    lecturerNote = finalNote
                )
            ) {
                filter {
                    eq("id", requestId)
                }
            }

        supabase
            .from("lecturers")
            .update(
                LecturerCurrentStudentsUpdate(
                    currentStudents = lecturer.currentStudents + 1
                )
            ) {
                filter {
                    eq("id", lecturer.id)
                }
            }
    }

    suspend fun rejectRequest(
        requestId: Long,
        note: String?
    ) {
        val finalNote = note
            ?.trim()
            ?.ifBlank {
                null
            }
            ?: "Pengajuan ditolak oleh Kaprodi."

        supabase
            .from("supervisor_requests")
            .update(
                SupervisorRequestRejectUpdate(
                    status = "rejected",
                    lecturerNote = finalNote
                )
            ) {
                filter {
                    eq("id", requestId)
                }
            }
    }

    suspend fun archiveRequest(requestId: Long) {
        supabase
            .from("supervisor_requests")
            .update(
                SupervisorRequestArchiveUpdate(
                    isArchived = true
                )
            ) {
                filter {
                    eq("id", requestId)
                }
            }
    }

    private suspend fun getRequestById(requestId: Long): SupervisorRequestRow? {
        return try {
            supabase
                .from("supervisor_requests")
                .select {
                    filter {
                        eq("id", requestId)
                    }
                }
                .decodeSingle<SupervisorRequestRow>()
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getProfileById(profileId: String): ProfileMiniRow? {
        return try {
            supabase
                .from("profiles")
                .select {
                    filter {
                        eq("id", profileId)
                    }
                }
                .decodeSingle<ProfileMiniRow>()
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getLecturerById(lecturerId: Long): LecturerMiniRow? {
        return try {
            supabase
                .from("lecturers")
                .select {
                    filter {
                        eq("id", lecturerId)
                    }
                }
                .decodeSingle<LecturerMiniRow>()
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun getRecommendedLecturers(
        departmentId: Long,
        expertise: String?,
        excludeLecturerId: Long
    ): List<KaprodiLecturerRecommendation> {
        val lecturers = supabase
            .from("lecturers")
            .select {
                filter {
                    eq("department_id", departmentId)
                    eq("is_active", true)
                }
            }
            .decodeList<LecturerMiniRow>()

        return lecturers
            .filter { lecturer ->
                lecturer.id != excludeLecturerId &&
                        lecturer.currentStudents < lecturer.quota
            }
            .sortedWith(
                compareByDescending<LecturerMiniRow> { lecturer ->
                    if (expertise.isNullOrBlank()) {
                        false
                    } else {
                        lecturer.expertise?.contains(expertise, ignoreCase = true) == true ||
                                expertise.contains(lecturer.expertise.orEmpty(), ignoreCase = true)
                    }
                }.thenBy { lecturer ->
                    lecturer.currentStudents
                }.thenByDescending { lecturer ->
                    lecturer.remainingQuota
                }.thenBy { lecturer ->
                    lecturer.name
                }
            )
            .map { lecturer ->
                KaprodiLecturerRecommendation(
                    id = lecturer.id,
                    fullName = lecturer.fullName,
                    expertise = lecturer.expertise,
                    quota = lecturer.quota,
                    currentStudents = lecturer.currentStudents,
                    remainingQuota = lecturer.remainingQuota.coerceAtLeast(0)
                )
            }
    }

    private suspend fun getStudentDocuments(
        studentId: String
    ): List<ThesisSubmissionDocumentRow> {
        return try {
            val submissions = supabase
                .from("thesis_submissions")
                .select {
                    filter {
                        eq("student_id", studentId)
                    }
                }
                .decodeList<ThesisSubmissionTrackingRow>()

            val submissionIds = submissions
                .map { submission ->
                    submission.id
                }
                .toSet()

            if (submissionIds.isEmpty()) {
                emptyList()
            } else {
                supabase
                    .from("thesis_submission_documents")
                    .select()
                    .decodeList<ThesisSubmissionDocumentRow>()
                    .filter { document ->
                        submissionIds.contains(document.submissionId)
                    }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun getLatestStageStatus(
        submissions: List<ThesisSubmissionTrackingRow>,
        stage: String
    ): String? {
        return submissions
            .filter { submission ->
                submission.stage == stage
            }
            .maxByOrNull { submission ->
                submission.createdAt.orEmpty()
            }
            ?.status
    }
}

data class KaprodiSubmissionData(
    val id: Long,
    val studentId: String,
    val studentName: String,
    val nim: String,
    val title: String?,
    val topic: String?,
    val lecturerId: Long,
    val lecturerName: String,
    val lecturerQuota: Int,
    val lecturerCurrentStudents: Int,
    val lecturerRemainingQuota: Int,
    val lecturerIsFull: Boolean,
    val status: String,
    val note: String?,
    val recommendedLecturerId: Long? = null,
    val recommendedLecturerName: String? = null,
    val recommendationNote: String? = null,
    val krsFileUrl: String? = null,
    val proposalFileUrl: String? = null,
    val approvalFileUrl: String? = null,
    val recommendedLecturers: List<KaprodiLecturerRecommendation> = emptyList()
)

data class KaprodiLecturerRecommendation(
    val id: Long,
    val fullName: String,
    val expertise: String?,
    val quota: Int,
    val currentStudents: Int,
    val remainingQuota: Int
)

data class KaprodiStudentTrackingData(
    val studentId: String,
    val studentName: String,
    val nim: String,
    val departmentId: Long?,
    val thesisTitle: String?,
    val lecturerName: String?,
    val latestChapter: Int,
    val totalSubmissions: Int,
    val seminarProposalStatus: String?,
    val revisiSeminarProposalStatus: String?,
    val kolokiumStatus: String?,
    val revisiKolokiumStatus: String?,
    val yudisiumStatus: String?
) {
    val progressText: String
        get() = if (latestChapter <= 0) {
            "Belum ada progress BAB"
        } else {
            "Sudah sampai BAB $latestChapter"
        }
}

@Serializable
private data class SupervisorRequestRow(
    val id: Long,

    @SerialName("student_id")
    val studentId: String,

    @SerialName("lecturer_id")
    val lecturerId: Long,

    val title: String? = null,

    val topic: String? = null,

    val status: String,

    @SerialName("lecturer_note")
    val lecturerNote: String? = null,

    @SerialName("recommended_lecturer_id")
    val recommendedLecturerId: Long? = null,

    @SerialName("recommendation_note")
    val recommendationNote: String? = null,

    @SerialName("is_archived")
    val isArchived: Boolean = false
)

@Serializable
private data class ProfileMiniRow(
    val id: String,

    @SerialName("full_name")
    val fullName: String,

    val nim: String? = null
)

@Serializable
private data class ProfileTrackingRow(
    val id: String,

    @SerialName("full_name")
    val fullName: String? = null,

    val nim: String? = null,

    @SerialName("department_id")
    val departmentId: Long? = null
)

@Serializable
private data class LecturerMiniRow(
    val id: Long,

    @SerialName("department_id")
    val departmentId: Long,

    val name: String,

    val title: String? = null,

    val expertise: String? = null,

    val quota: Int = 5,

    @SerialName("current_students")
    val currentStudents: Int = 0,

    @SerialName("is_active")
    val isActive: Boolean = true
) {
    val fullName: String
        get() = if (title.isNullOrBlank()) {
            name
        } else {
            "$name, $title"
        }

    val remainingQuota: Int
        get() = quota - currentStudents

    val isFull: Boolean
        get() = currentStudents >= quota
}

@Serializable
private data class ThesisChapterTrackingRow(
    val id: Long? = null,

    @SerialName("supervisor_request_id")
    val supervisorRequestId: Long? = null,

    @SerialName("chapter_number")
    val chapterNumber: Int? = null
)

@Serializable
private data class ThesisSubmissionTrackingRow(
    val id: String,

    @SerialName("student_id")
    val studentId: String,

    val stage: String,

    val status: String,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
private data class ThesisSubmissionDocumentRow(
    val id: String,

    @SerialName("submission_id")
    val submissionId: String,

    @SerialName("document_key")
    val documentKey: String,

    @SerialName("document_name")
    val documentName: String? = null,

    @SerialName("file_url")
    val fileUrl: String,

    val status: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
private data class SupervisorRequestRecommendationUpdate(
    @SerialName("recommended_lecturer_id")
    val recommendedLecturerId: Long,

    @SerialName("recommendation_note")
    val recommendationNote: String? = null
)

@Serializable
private data class SupervisorRequestApproveUpdate(
    val status: String,

    @SerialName("lecturer_id")
    val lecturerId: Long,

    @SerialName("lecturer_note")
    val lecturerNote: String
)

@Serializable
private data class SupervisorRequestRejectUpdate(
    val status: String,

    @SerialName("lecturer_note")
    val lecturerNote: String
)

@Serializable
private data class SupervisorRequestArchiveUpdate(
    @SerialName("is_archived")
    val isArchived: Boolean
)

@Serializable
private data class LecturerCurrentStudentsUpdate(
    @SerialName("current_students")
    val currentStudents: Int
)