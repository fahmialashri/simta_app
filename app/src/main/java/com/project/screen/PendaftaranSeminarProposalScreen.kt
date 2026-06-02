package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.core.SimtaRed
import com.project.navigation.Screen

@Composable
fun PendaftaranSeminarProposalScreen(
    navController: NavHostController
) {
    Scaffold(
        containerColor = Color(0xFFF4F4F4),
        bottomBar = {
            PendaftaranSemproBottomBar(
                onHomeClick = {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.MahasiswaDashboard.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onPengajuanClick = {
                    navController.navigate(Screen.Pengajuan.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                        launchSingleTop = true
                    }
                },
                onBimbinganClick = {
                    navController.navigate(Screen.Bimbingan.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                        launchSingleTop = true
                    }
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4))
                .padding(paddingValues)
        ) {
            HeaderSection(
                onBackClick = {
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SeminarProposalMenuCard(
                    icon = Icons.Default.Assignment,
                    title = "Pengajuan Judul Proposal",
                    subtitle = "Ajukan judul untuk disetujui dosen",
                    onClick = {
                        navController.navigate(Screen.AjukanJudulProposal.route)
                    }
                )

                SeminarProposalMenuCard(
                    icon = Icons.Default.Article,
                    title = "Pendaftaran Seminar Proposal",
                    subtitle = "Daftar jadwal seminar proposal",
                    onClick = {
                        navController.navigate(Screen.PendaftaranSeminarProposalForm.route)
                    }
                )

                SeminarProposalMenuCard(
                    icon = Icons.Default.UploadFile,
                    title = "Upload Revisi Seminar Proposal",
                    subtitle = "Unggah berkas hasil revisi",
                    onClick = {
                        navController.navigate(Screen.UploadRevisiSeminarProposal.route)
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(SimtaRed)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Kembali",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onBackClick()
                    }
            )

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = "Pendaftaran Seminar Proposal",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Pilih layanan yang ingin diajukan",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun SeminarProposalMenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable {
                onClick()
            }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(SimtaRed.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SimtaRed,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color(0xFF222222),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = Color(0xFF8A8A8A),
                fontSize = 10.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun PendaftaranSemproBottomBar(
    onHomeClick: () -> Unit,
    onPengajuanClick: () -> Unit,
    onBimbinganClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar(
        containerColor = SimtaRed,
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            unselectedIconColor = Color.White.copy(alpha = 0.55f),
            unselectedTextColor = Color.White.copy(alpha = 0.55f),
            indicatorColor = Color.Transparent
        )

        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 9.sp
                )
            },
            colors = colors
        )

        NavigationBarItem(
            selected = true,
            onClick = onPengajuanClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = "Pengajuan",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Pengajuan",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = colors
        )

        NavigationBarItem(
            selected = false,
            onClick = onBimbinganClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.AssignmentTurnedIn,
                    contentDescription = "Bimbingan",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Bimbingan",
                    fontSize = 9.sp
                )
            },
            colors = colors
        )

        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Profil",
                    fontSize = 9.sp
                )
            },
            colors = colors
        )
    }
}