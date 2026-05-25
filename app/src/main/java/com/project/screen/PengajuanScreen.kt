package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.core.SimtaRed
import com.project.navigation.Screen

@Composable
fun PengajuanScreen(
    navController: NavHostController
) {
    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            PengajuanBottomNavigation(
                onHomeClick = {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.MahasiswaDashboard.route) {
                            inclusive = true
                        }
                    }
                },
                onPengajuanClick = {},
                onBimbinganClick = {
                    navController.navigate(Screen.Bimbingan.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                    }
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Formulir Pengajuan",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pilih tahapan tugas akhir yang ingin diajukan.",
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            PengajuanMenuCard(
                title = "Pengajuan Dosen Pembimbing",
                subtitle = "Pilih dosen pembimbing utama untuk disetujui Kaprodi",
                icon = Icons.Default.School,
                variant = PengajuanCardVariant.DOSBING,
                onClick = {
                    navController.navigate(Screen.LecturerList.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PengajuanMenuCard(
                title = "Pendaftaran Seminar Proposal",
                subtitle = "Upload berkas persyaratan seminar proposal",
                icon = Icons.Default.Article,
                variant = PengajuanCardVariant.SEMPRO,
                onClick = {
                    navController.navigate(Screen.UploadBerkas.createRoute("seminar_proposal"))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PengajuanMenuCard(
                title = "Pendaftaran Kolokium",
                subtitle = "Upload berkas seminar hasil / kolokium",
                icon = Icons.Default.Verified,
                variant = PengajuanCardVariant.KOLOKIUM,
                onClick = {
                    navController.navigate(Screen.UploadBerkas.createRoute("kolokium"))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PengajuanMenuCard(
                title = "Pendaftaran Yudisium",
                subtitle = "Upload berkas akhir untuk proses yudisium",
                icon = Icons.Default.WorkspacePremium,
                variant = PengajuanCardVariant.YUDISIUM,
                onClick = {
                    navController.navigate(Screen.UploadBerkas.createRoute("yudisium"))
                }
            )
        }
    }
}

private enum class PengajuanCardVariant {
    DOSBING,
    SEMPRO,
    KOLOKIUM,
    YUDISIUM
}

@Composable
private fun PengajuanMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    variant: PengajuanCardVariant,
    onClick: () -> Unit
) {
    val backgroundBrush = when (variant) {
        PengajuanCardVariant.DOSBING -> Brush.linearGradient(
            listOf(Color(0xFFFFCDD2), Color(0xFFE57373))
        )

        PengajuanCardVariant.SEMPRO -> Brush.linearGradient(
            listOf(Color(0xFFE3F2FD), Color(0xFF64B5F6))
        )

        PengajuanCardVariant.KOLOKIUM -> Brush.linearGradient(
            listOf(Color(0xFFE8F5E9), Color(0xFF81C784))
        )

        PengajuanCardVariant.YUDISIUM -> Brush.linearGradient(
            listOf(Color(0xFFFFF3E0), Color(0xFFFFB74D))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.45f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .size(56.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .background(SimtaRed)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, end = 54.dp, bottom = 12.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 10.sp,
                    maxLines = 2
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 20.dp)
                    .size(26.dp)
            )
        }
    }
}

@Composable
private fun PengajuanBottomNavigation(
    onHomeClick: () -> Unit,
    onPengajuanClick: () -> Unit,
    onBimbinganClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = SimtaRed,
        unselectedIconColor = Color.White.copy(alpha = 0.7f),
        selectedTextColor = Color.White,
        unselectedTextColor = Color.White.copy(alpha = 0.7f),
        indicatorColor = Color.White
    )

    NavigationBar(
        containerColor = SimtaRed,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )

        NavigationBarItem(
            selected = true,
            onClick = onPengajuanClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = "Pengajuan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Pengajuan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onBimbinganClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Bimbingan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Bimbingan",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Profil",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )
    }
}