package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.data.model.ThesisChapter
import com.project.navigation.Screen
import com.project.supervisor.SupervisorRequestViewModel

@Composable
fun MahasiswaDashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    supervisorRequestViewModel: SupervisorRequestViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val requestState by supervisorRequestViewModel.uiState.collectAsState()

    LaunchedEffect(authState.userId) {
        supervisorRequestViewModel.loadMyRequestProgress(authState.userId)
    }

    val request = requestState.activeRequest
    val isPending = request?.status == "pending"
    val isAccepted = request?.status == "accepted"
    val isRejected = request?.status == "rejected"

    val approvedCount = requestState.chapters.count { it.status == "approved" }
    val totalChapters = requestState.chapters.size

    val progress = if (isAccepted && totalChapters > 0) {
        approvedCount.toFloat() / totalChapters.toFloat()
    } else {
        0f
    }

    val progressLabel = if (isAccepted && totalChapters > 0) {
        "${(progress * 100).toInt()} % Selesai"
    } else {
        "0 % Selesai"
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            SimtaBottomNavigation(
                onHomeClick = {
                    // Sudah di dashboard
                },
                onPengajuanClick = {
                    navController.navigate(Screen.Pengajuan.route)
                },
                onBimbinganClick = {
                    navController.navigate(Screen.Bimbingan.route)
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
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            DashboardHeader(
                name = authState.name ?: "MAHASISWA"
            )

            Spacer(modifier = Modifier.height(24.dp))

            QuoteCard()

            Spacer(modifier = Modifier.height(20.dp))

            SupervisorCard(
                lecturerName = requestState.activeLecturer?.fullName ?: "Belum memilih dosen",
                status = when {
                    isPending -> "(Menunggu Persetujuan)"
                    isAccepted -> "(Pembimbing 1)"
                    isRejected -> "(Pengajuan Ditolak)"
                    else -> "(Klik untuk pengajuan)"
                },
                onClick = {
                    navController.navigate(Screen.Pengajuan.route)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProgressCard(
                progress = progress,
                label = progressLabel
            )

            Spacer(modifier = Modifier.height(20.dp))

            when {
                requestState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SimtaRed)
                    }
                }

                requestState.errorMessage != null -> {
                    MahasiswaErrorCard(
                        message = requestState.errorMessage ?: "Terjadi kesalahan"
                    )
                }

                isAccepted -> {
                    ComponentStatusCard(
                        chapters = requestState.chapters
                    )
                }

                isPending -> {
                    WaitingApprovalCard(
                        lecturerName = requestState.activeLecturer?.fullName ?: "-",
                        title = request?.title ?: "-"
                    )
                }

                isRejected -> {
                    RejectedCard(
                        note = request?.lecturerNote
                    )
                }

                else -> {
                    EmptySubmissionCard(
                        onClick = {
                            navController.navigate(Screen.Pengajuan.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    name: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8EAED)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Selamat Datang!",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = name.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                letterSpacing = 0.5.sp
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun QuoteCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SimtaRed
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Masa depan adalah milik mereka yang percaya\ndengan impiannya.",
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "- Tan Malaka -",
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SupervisorCard(
    lecturerName: String,
    status: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Dosen Pembimbing",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8EAED)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = lecturerName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressCard(
    progress: Float,
    label: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Status Tugas Akhir Anda",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(50))
                        .background(SimtaRed),
                    contentAlignment = Alignment.Center
                ) {
                    if (progress > 0.2f) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun ComponentStatusCard(
    chapters: List<ThesisChapter>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SimtaRed)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Komponen",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Status",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (chapters.isEmpty()) {
                    Text(
                        text = "Data BAB belum tersedia.",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    chapters.forEach { chapter ->
                        BabStatusRow(
                            title = chapter.title.uppercase(),
                            status = chapter.status
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BabStatusRow(
    title: String,
    status: String
) {
    val (label, bgColor, textColor) = when (status) {
        "approved" -> Triple("Disetujui", Color(0xFF8BC34A), Color.White)
        "revision" -> Triple("Revisi", Color(0xFFFFCA28), Color.White)
        "process" -> Triple("Proses", Color(0xFFD0D0D0), Color.White)
        else -> Triple("Belum", Color(0xFFE0E0E0), Color.DarkGray)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .width(80.dp)
                .clip(RoundedCornerShape(50))
                .background(bgColor)
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun EmptySubmissionCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pengajuan Pembimbing",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Belum ada pengajuan dosen pembimbing.",
                fontSize = 12.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Klik di sini untuk membuka formulir pengajuan.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SimtaRed
            )
        }
    }
}

@Composable
private fun WaitingApprovalCard(
    lecturerName: String,
    title: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Status Pengajuan",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Menunggu Persetujuan",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA000)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dosen: $lecturerName",
                fontSize = 12.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Judul: $title",
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun RejectedCard(
    note: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pengajuan Ditolak",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SimtaRed
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note ?: "Silakan ajukan dosen pembimbing lain.",
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun MahasiswaErrorCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Text(
            text = message,
            color = SimtaRed,
            modifier = Modifier.padding(16.dp),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SimtaBottomNavigation(
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
            selected = true,
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
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
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