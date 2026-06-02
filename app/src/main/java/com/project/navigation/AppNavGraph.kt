package com.project.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.project.auth.AuthViewModel
import com.project.bimbingan.BimbinganViewModel
import com.project.lecturer.LecturerViewModel
import com.project.screen.AjukanJudulProposalScreen
import com.project.screen.BimbinganDetailScreen
import com.project.screen.BimbinganScreen
import com.project.screen.DosenDashboardScreen
import com.project.screen.ForgotPasswordScreen
import com.project.screen.KaprodiDashboardScreen
import com.project.screen.LecturerDetailScreen
import com.project.screen.LecturerListScreen
import com.project.screen.LoginScreen
import com.project.screen.MahasiswaDashboardScreen
import com.project.screen.OnboardingScreen
import com.project.screen.PendaftaranKolokiumFormScreen
import com.project.screen.PendaftaranKolokiumScreen
import com.project.screen.PendaftaranSeminarProposalFormScreen
import com.project.screen.PendaftaranSeminarProposalScreen
import com.project.screen.PendaftaranYudisiumFormScreen
import com.project.screen.PengajuanScreen
import com.project.screen.ProfileScreen
import com.project.screen.RegisterScreen
import com.project.screen.ResetPasswordScreen
import com.project.screen.SplashScreen
import com.project.screen.TuDashboardScreen
import com.project.screen.TuDocumentReviewScreen
import com.project.screen.TuPlottingPengujiScreen
import com.project.screen.UploadBerkasScreen
import com.project.screen.UploadRevisiKolokiumScreen
import com.project.screen.UploadRevisiSeminarProposalScreen
import com.project.supervisor.SupervisorRequestViewModel
import com.project.upload.UploadBerkasViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val lecturerViewModel: LecturerViewModel = viewModel()
    val supervisorRequestViewModel: SupervisorRequestViewModel = viewModel()
    val bimbinganViewModel: BimbinganViewModel = viewModel()
    val uploadBerkasViewModel: UploadBerkasViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onStartClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(
            route = Screen.ResetPassword.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "simtaapp://reset-password"
                }
            )
        ) {
            ResetPasswordScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.MahasiswaDashboard.route) {
            MahasiswaDashboardScreen(
                navController = navController,
                authViewModel = authViewModel,
                supervisorRequestViewModel = supervisorRequestViewModel
            )
        }

        composable(Screen.Pengajuan.route) {
            PengajuanScreen(
                navController = navController
            )
        }

        composable(Screen.PendaftaranSeminarProposal.route) {
            PendaftaranSeminarProposalScreen(
                navController = navController
            )
        }

        composable(Screen.PendaftaranSeminarProposalForm.route) {
            PendaftaranSeminarProposalFormScreen(
                navController = navController,
                authViewModel = authViewModel,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(Screen.UploadRevisiSeminarProposal.route) {
            UploadRevisiSeminarProposalScreen(
                navController = navController,
                authViewModel = authViewModel,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(Screen.PendaftaranKolokium.route) {
            PendaftaranKolokiumScreen(
                navController = navController,
                authViewModel = authViewModel,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(Screen.PendaftaranKolokiumForm.route) {
            PendaftaranKolokiumFormScreen(
                navController = navController,
                authViewModel = authViewModel,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(Screen.UploadRevisiKolokium.route) {
            UploadRevisiKolokiumScreen(
                navController = navController,
                authViewModel = authViewModel,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(Screen.PendaftaranYudisiumForm.route) {
            PendaftaranYudisiumFormScreen(
                navController = navController,
                authViewModel = authViewModel,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(Screen.AjukanJudulProposal.route) {
            AjukanJudulProposalScreen(
                navController = navController,
                authViewModel = authViewModel,
                lecturerViewModel = lecturerViewModel,
                supervisorRequestViewModel = supervisorRequestViewModel
            )
        }

        composable(
            route = Screen.UploadBerkas.route,
            arguments = listOf(
                navArgument("stage") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val stage = backStackEntry.arguments?.getString("stage") ?: "seminar_proposal"

            UploadBerkasScreen(
                navController = navController,
                stage = stage,
                authViewModel = authViewModel,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(Screen.Bimbingan.route) {
            BimbinganScreen(
                navController = navController,
                authViewModel = authViewModel,
                supervisorRequestViewModel = supervisorRequestViewModel,
                bimbinganViewModel = bimbinganViewModel
            )
        }

        composable(
            route = Screen.BimbinganDetail.route,
            arguments = listOf(
                navArgument("chapterId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getLong("chapterId") ?: 0L

            BimbinganDetailScreen(
                chapterId = chapterId,
                navController = navController,
                authViewModel = authViewModel,
                supervisorRequestViewModel = supervisorRequestViewModel,
                bimbinganViewModel = bimbinganViewModel
            )
        }

        composable(Screen.DosenDashboard.route) {
            DosenDashboardScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(Screen.KaprodiDashboard.route) {
            KaprodiDashboardScreen(
                navController = navController,
                onLogout = {
                    authViewModel.logout()

                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.TuDashboard.route) {
            TuDashboardScreen(
                navController = navController,
                uploadBerkasViewModel = uploadBerkasViewModel,
                onLogout = {
                    authViewModel.logout()

                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.TuDocumentReview.route,
            arguments = listOf(
                navArgument("stage") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val stage = backStackEntry.arguments?.getString("stage") ?: "seminar_proposal"

            TuDocumentReviewScreen(
                navController = navController,
                stage = stage,
                uploadBerkasViewModel = uploadBerkasViewModel
            )
        }

        composable(
            route = Screen.TuPlottingPenguji.route,
            arguments = listOf(
                navArgument("stageId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val stageId = backStackEntry.arguments?.getString("stageId") ?: ""

            TuPlottingPengujiScreen(
                navController = navController,
                stageId = stageId
            )
        }

        composable(Screen.LecturerList.route) {
            LecturerListScreen(
                navController = navController,
                lecturerViewModel = lecturerViewModel,
                authViewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onLecturerClick = { lecturerId ->
                    navController.navigate(
                        Screen.LecturerDetail.createRoute(lecturerId)
                    )
                }
            )
        }

        composable(
            route = Screen.LecturerListByExpertise.route,
            arguments = listOf(
                navArgument("expertise") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val expertise = backStackEntry.arguments
                ?.getString("expertise")
                .orEmpty()

            LecturerListScreen(
                navController = navController,
                lecturerViewModel = lecturerViewModel,
                authViewModel = authViewModel,
                initialExpertise = expertise,
                onBackClick = {
                    navController.popBackStack()
                },
                onLecturerClick = { lecturerId ->
                    navController.navigate(
                        Screen.LecturerDetail.createRoute(lecturerId)
                    )
                }
            )
        }

        composable(
            route = Screen.LecturerDetail.route,
            arguments = listOf(
                navArgument("lecturerId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val lecturerId = backStackEntry.arguments?.getLong("lecturerId") ?: 0L

            LecturerDetailScreen(
                lecturerId = lecturerId,
                lecturerViewModel = lecturerViewModel,
                supervisorRequestViewModel = supervisorRequestViewModel,
                authViewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.MahasiswaDashboard.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onAjukanClick = {
                    navController.navigate(Screen.AjukanJudulProposal.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
    }
}