package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun PengajuanScreen(
    navController: NavHostController
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            PengajuanBottomNavigation(
                onHomeClick = {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.MahasiswaDashboard.route) {
                            inclusive = true
                        }
                    }
                },
                onPengajuanClick = {
                    // Tetap di halaman Pengajuan
                },
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
                .background(Color(0xFFF5F5F5))
        ) {
            PengajuanHeader(
                title = "Pengajuan",
                subtitle = "Pilih layanan yang ingin dilakukan",
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
                    title = "Pendaftaran Seminar Proposal",
                    subtitle = "Daftar dan lengkapi layanan seminar proposal",
                    icon = Icons.Default.Article,
                    onClick = {
                        navController.navigate(Screen.PendaftaranSeminarProposal.route)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PengajuanMenuCard(
                    title = "Pendaftaran Kolokium",
                    subtitle = "Daftar dan lengkapi layanan kolokium",
                    icon = Icons.Default.Assignment,
                    onClick = {
                        navController.navigate(Screen.PendaftaranKolokium.route)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                PengajuanMenuCard(
                    title = "Pendaftaran Yudisium",
                    subtitle = "Daftar dan lengkapi berkas yudisium",
                    icon = Icons.Default.Person,
                    onClick = {
                        navController.navigate(
                            Screen.UploadBerkas.createRoute("yudisium")
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun PengajuanHeader(
    title: String,
    subtitle: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .background(SimtaRed)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun PengajuanMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF8E3E3)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SimtaRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color(0xFF2D2D2D),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF8A8A8A),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFBDBDBD),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PengajuanBottomNavigation(
    onHomeClick: () -> Unit,
    onPengajuanClick: () -> Unit,
    onBimbinganClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        unselectedIconColor = Color.White.copy(alpha = 0.68f),
        selectedTextColor = Color.White,
        unselectedTextColor = Color.White.copy(alpha = 0.68f),
        indicatorColor = Color.Transparent
    )

    NavigationBar(
        containerColor = SimtaRed,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            colors = navItemColors,
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
                    fontSize = 9.sp,
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
                    imageVector = Icons.Default.Article,
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
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Bimbingan",
                    fontSize = 9.sp,
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
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Profil",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )
    }
}