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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.bimbingan.BimbinganViewModel
import com.project.core.SimtaGreen
import com.project.core.SimtaRed
import com.project.data.model.ThesisChapter
import com.project.navigation.Screen
import com.project.supervisor.SupervisorRequestViewModel

@Composable
fun BimbinganScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    supervisorRequestViewModel: SupervisorRequestViewModel,
    bimbinganViewModel: BimbinganViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val supervisorState by supervisorRequestViewModel.uiState.collectAsState()
    val bimbinganState by bimbinganViewModel.uiState.collectAsState()

    val request = supervisorState.activeRequest

    LaunchedEffect(authState.userId) {
        supervisorRequestViewModel.loadMyRequestProgress(authState.userId)
    }

    LaunchedEffect(request?.id, request?.status) {
        if (request?.status == "accepted") {
            bimbinganViewModel.loadChapters(
                studentId = request.studentId,
                supervisorRequestId = request.id
            )
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA), // Background abu-abu muda senada dengan Dashboard
        bottomBar = {
            BimbinganBottomNavigation(
                onHomeClick = {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.MahasiswaDashboard.route) { inclusive = true }
                    }
                },
                onPengajuanClick = {
                    navController.navigate(Screen.Pengajuan.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                    }
                },
                onBimbinganClick = {
                    // Sudah di Bimbingan
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
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
                text = "Logbook Bimbingan",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pantau progress bimbingan tugas akhir anda",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                supervisorState.isLoading || bimbinganState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SimtaRed)
                    }
                }

                supervisorState.errorMessage != null -> {
                    InfoCard(
                        text = supervisorState.errorMessage ?: "Gagal mengambil data pengajuan.",
                        isError = true
                    )
                }

                request == null -> {
                    InfoCard(
                        text = "Belum ada pengajuan dosen pembimbing. Silakan lakukan pengajuan terlebih dahulu."
                    )
                }

                request.status != "accepted" -> {
                    InfoCard(
                        text = "Bimbingan belum tersedia. Pengajuan dosen pembimbing anda masih menunggu persetujuan."
                    )
                }

                bimbinganState.errorMessage != null -> {
                    InfoCard(
                        text = bimbinganState.errorMessage ?: "Terjadi kesalahan",
                        isError = true
                    )
                }

                bimbinganState.chapters.isEmpty() -> {
                    InfoCard(
                        text = "Data BAB belum tersedia."
                    )
                }

                else -> {
                    bimbinganState.chapters.forEach { chapter ->
                        ChapterCard(
                            chapter = chapter,
                            onClick = {
                                navController.navigate(
                                    Screen.BimbinganDetail.createRoute(chapter.id)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: ThesisChapter,
    onClick: () -> Unit
) {
    val badgeLabel = when (chapter.status) {
        "approved" -> "Disetujui"
        "revision" -> "Revisi"
        "process" -> "Diproses"
        else -> "Belum"
    }

    val badgeColor = when (chapter.status) {
        "approved" -> SimtaGreen
        "revision" -> Color(0xFFF39C12) // Orange modern
        "process" -> Color(0xFF3498DB) // Biru modern
        else -> Color(0xFFBDBDBD)
    }

    val icon = when (chapter.chapterNumber) {
        1 -> Icons.Default.MenuBook
        2 -> Icons.Default.WbSunny
        3 -> Icons.Default.Science
        4 -> Icons.Default.Article
        else -> Icons.Default.Folder
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp), // Sudut lebih membulat kekinian
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box Modern
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F3F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SimtaRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "BAB ${chapter.chapterNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SimtaRed
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = cleanChapterTitle(chapter.title),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }

                // Badge Status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeLabel,
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!chapter.lecturerNote.isNullOrBlank()) {
                Divider(
                    color = Color(0xFFF1F3F4),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA)) // Background sedikit beda buat area komentar
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Komentar Dosen:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = chapter.lecturerNote,
                            fontSize = 11.sp,
                            color = Color.Black,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp).align(Alignment.CenterVertically)
                    )
                }
            } else {
                Divider(
                    color = Color(0xFFF1F3F4),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Lihat Detail",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoCard(text: String, isError: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEBEE) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isError) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Article, // Bisa diganti icon info/warning
                contentDescription = null,
                tint = if (isError) SimtaRed else Color.LightGray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                fontSize = 13.sp,
                color = if (isError) SimtaRed else Color.DarkGray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun BimbinganBottomNavigation(
    onHomeClick: () -> Unit,
    onPengajuanClick: () -> Unit,
    onBimbinganClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = SimtaRed, // Icon merah saat aktif
        unselectedIconColor = Color.White.copy(alpha = 0.7f),
        selectedTextColor = Color.White,
        unselectedTextColor = Color.White.copy(alpha = 0.7f),
        indicatorColor = Color.White // Kapsul putih di belakang icon aktif
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
            selected = false,
            onClick = onPengajuanClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Article,
                    contentDescription = "Pengajuan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Pengajuan",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )

        NavigationBarItem(
            selected = true, // Aktif di menu Bimbingan
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
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
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

private fun cleanChapterTitle(title: String): String {
    return title
        .replace("BAB I -", "")
        .replace("BAB II -", "")
        .replace("BAB III -", "")
        .replace("BAB IV -", "")
        .replace("BAB V -", "")
        .trim()
}