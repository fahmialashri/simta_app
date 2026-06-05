package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.component.MahasiswaBottomNavItem
import com.project.component.MahasiswaBottomNavigation
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Suppress("UNUSED_PARAMETER")
@Composable
fun PendaftaranKolokiumScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            MahasiswaBottomNavigation(
                navController = navController,
                selectedItem = MahasiswaBottomNavItem.PENGAJUAN
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            PengajuanHeader(
                title = "Pendaftaran Kolokium",
                subtitle = "Pilih layanan yang ingin diajukan",
                onBackClick = {
                    navController.popBackStack()
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 24.dp)
            ) {
                PengajuanMenuCard(
                    title = "Pendaftaran Kolokium",
                    subtitle = "Daftar jadwal sidang kolokium",
                    icon = Icons.Default.Assignment,
                    onClick = {
                        navController.navigate(
                            Screen.UploadBerkas.createRoute("pendaftaran_kolokium")
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PengajuanMenuCard(
                    title = "Upload Revisi Kolokium",
                    subtitle = "Unggah revisi setelah sidang kolokium",
                    icon = Icons.Default.UploadFile,
                    onClick = {
                        navController.navigate(Screen.UploadRevisiKolokium.route)
                    }
                )
            }
        }
    }
}