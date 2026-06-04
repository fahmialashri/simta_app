package com.project.screen

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.lecturer.LecturerViewModel
import com.project.upload.UploadBerkasViewModel

@Composable
fun UploadBerkasScreen(
    navController: NavHostController,
    stage: String,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel = viewModel(),
    lecturerViewModel: LecturerViewModel = viewModel()
) {
    when (stage) {
        "seminar_proposal" -> PendaftaranSeminarProposalFormScreen(
            navController = navController,
            authViewModel = authViewModel,
            uploadBerkasViewModel = uploadBerkasViewModel,
            lecturerViewModel = lecturerViewModel
        )

        "revisi_seminar_proposal" -> UploadRevisiSeminarProposalScreen(
            navController = navController,
            authViewModel = authViewModel,
            uploadBerkasViewModel = uploadBerkasViewModel,
            lecturerViewModel = lecturerViewModel
        )

        "pendaftaran_kolokium" -> PendaftaranKolokiumFormScreen(
            navController = navController,
            authViewModel = authViewModel,
            uploadBerkasViewModel = uploadBerkasViewModel,
            lecturerViewModel = lecturerViewModel
        )

        "revisi_kolokium" -> UploadRevisiKolokiumScreen(
            navController = navController,
            authViewModel = authViewModel,
            uploadBerkasViewModel = uploadBerkasViewModel,
            lecturerViewModel = lecturerViewModel
        )

        "yudisium" -> PendaftaranYudisiumFormScreen(
            navController = navController,
            authViewModel = authViewModel,
            uploadBerkasViewModel = uploadBerkasViewModel,
            lecturerViewModel = lecturerViewModel
        )

        else -> GenericUploadBerkasScreen(
            navController = navController,
            stage = stage,
            authViewModel = authViewModel,
            uploadBerkasViewModel = uploadBerkasViewModel
        )
    }
}