package com.project.navigation

import android.net.Uri

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object ResetPassword : Screen("reset_password")

    data object MahasiswaDashboard : Screen("mahasiswa_dashboard")
    data object DosenDashboard : Screen("dosen_dashboard")
    data object KaprodiDashboard : Screen("kaprodi_dashboard")
    data object TuDashboard : Screen("tu_dashboard")

    data object Pengajuan : Screen("pengajuan")

    data object PendaftaranSeminarProposal : Screen("pendaftaran_seminar_proposal")
    data object PendaftaranSeminarProposalForm : Screen("pendaftaran_seminar_proposal_form")
    data object UploadRevisiSeminarProposal : Screen("upload_revisi_seminar_proposal")

    data object PendaftaranKolokium : Screen("pendaftaran_kolokium")
    data object PendaftaranKolokiumForm : Screen("pendaftaran_kolokium_form")
    data object UploadRevisiKolokium : Screen("upload_revisi_kolokium")

    data object PendaftaranYudisiumForm : Screen("pendaftaran_yudisium_form")

    data object AjukanJudulProposal : Screen("ajukan_judul_proposal")

    data object LecturerList : Screen("lecturer_list")

    data object LecturerListByExpertise : Screen("lecturer_list_by_expertise/{expertise}") {
        fun createRoute(expertise: String): String {
            return "lecturer_list_by_expertise/${Uri.encode(expertise)}"
        }
    }

    data object Bimbingan : Screen("bimbingan")
    data object Profile : Screen("profile")

    data object BimbinganDetail : Screen("bimbingan_detail/{chapterId}") {
        fun createRoute(chapterId: Long): String {
            return "bimbingan_detail/$chapterId"
        }
    }

    data object LecturerDetail : Screen("lecturer_detail/{lecturerId}") {
        fun createRoute(lecturerId: Long): String {
            return "lecturer_detail/$lecturerId"
        }
    }

    data object UploadBerkas : Screen("upload_berkas/{stage}") {
        fun createRoute(stage: String): String {
            return "upload_berkas/$stage"
        }
    }

    data object TuDocumentReview : Screen("tu_document_review/{stage}") {
        fun createRoute(stage: String): String {
            return "tu_document_review/$stage"
        }
    }

    data object TuPlottingPenguji : Screen("tu_plotting_penguji/{stageId}") {
        fun createRoute(stageId: String): String {
            return "tu_plotting_penguji/$stageId"
        }
    }

    data object KaprodiSubmissionDetail : Screen("kaprodi_submission_detail/{submissionId}") {
        fun createRoute(submissionId: String): String {
            return "kaprodi_submission_detail/$submissionId"
        }
    }
}