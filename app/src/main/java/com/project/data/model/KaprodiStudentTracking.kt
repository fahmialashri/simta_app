package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KaprodiStudentTracking(
    @SerialName("student_id")
    val studentId: String,

    @SerialName("student_name")
    val studentName: String? = null,

    val nim: String? = null,

    @SerialName("department_id")
    val departmentId: Long? = null,

    @SerialName("supervisor_request_id")
    val supervisorRequestId: Long? = null,

    @SerialName("thesis_title")
    val thesisTitle: String? = null,

    @SerialName("supervisor_status")
    val supervisorStatus: String? = null,

    @SerialName("lecturer_name")
    val lecturerName: String? = null,

    @SerialName("lecturer_title")
    val lecturerTitle: String? = null,

    @SerialName("latest_chapter")
    val latestChapter: Int = 0,

    @SerialName("total_submissions")
    val totalSubmissions: Int = 0,

    @SerialName("seminar_proposal_status")
    val seminarProposalStatus: String? = null,

    @SerialName("revisi_seminar_proposal_status")
    val revisiSeminarProposalStatus: String? = null,

    @SerialName("kolokium_status")
    val kolokiumStatus: String? = null,

    @SerialName("revisi_kolokium_status")
    val revisiKolokiumStatus: String? = null,

    @SerialName("yudisium_status")
    val yudisiumStatus: String? = null,

    @SerialName("latest_submission_at")
    val latestSubmissionAt: String? = null
) {
    val lecturerFullName: String
        get() = if (lecturerTitle.isNullOrBlank()) {
            lecturerName.orEmpty()
        } else {
            "${lecturerName.orEmpty()}, $lecturerTitle"
        }

    val progressText: String
        get() = if (latestChapter <= 0) {
            "Belum mulai bimbingan BAB"
        } else {
            "Sudah sampai BAB $latestChapter"
        }
}