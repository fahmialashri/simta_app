package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.project.upload.UploadBerkasViewModel

@Composable
fun TuDashboardScreen(
    navController: NavHostController,
    uploadBerkasViewModel: UploadBerkasViewModel,
    onLogout: () -> Unit = {}
) {
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    var isLoggingOut by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        uploadBerkasViewModel.loadPendingSubmissions()
    }

    val submissions = uploadState.submissions

    val seminarProposalCount = submissions.count {
        it.stage == "seminar_proposal"
    }

    val kolokiumCount = submissions.count {
        it.stage == "kolokium" || it.stage == "pendaftaran_kolokium"
    }

    val yudisiumCount = submissions.count {
        it.stage == "yudisium"
    }

    val revisiSeminarProposalCount = submissions.count {
        it.stage == "revisi_seminar_proposal"
    }

    val revisiKolokiumCount = submissions.count {
        it.stage == "revisi_kolokium"
    }

    val totalPending = submissions.size
    val hasNewSubmission = totalPending > 0

    Scaffold(
        containerColor = Color(0xFFF6F6F6)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            TuHeader(
                notificationCount = totalPending,
                isLoggingOut = isLoggingOut,
                onNotificationClick = {
                    showNotifications = !showNotifications
                },
                onLogoutClick = {
                    if (!isLoggingOut) {
                        isLoggingOut = true
                        onLogout()
                    }
                }
            )

            if (showNotifications) {
                Spacer(modifier = Modifier.height(14.dp))

                TuNotificationPanel(
                    totalPending = totalPending,
                    seminarProposalCount = seminarProposalCount,
                    kolokiumCount = kolokiumCount,
                    yudisiumCount = yudisiumCount,
                    revisiSeminarProposalCount = revisiSeminarProposalCount,
                    revisiKolokiumCount = revisiKolokiumCount,
                    onOpenSempro = {
                        showNotifications = false
                        navController.navigate(
                            Screen.TuDocumentReview.createRoute("seminar_proposal")
                        )
                    },
                    onOpenKolokium = {
                        showNotifications = false
                        navController.navigate(
                            Screen.TuDocumentReview.createRoute("kolokium")
                        )
                    },
                    onOpenYudisium = {
                        showNotifications = false
                        navController.navigate(
                            Screen.TuDocumentReview.createRoute("yudisium")
                        )
                    },
                    onOpenRevisiSempro = {
                        showNotifications = false
                        navController.navigate(
                            Screen.TuDocumentReview.createRoute("revisi_seminar_proposal")
                        )
                    },
                    onOpenRevisiKolokium = {
                        showNotifications = false
                        navController.navigate(
                            Screen.TuDocumentReview.createRoute("revisi_kolokium")
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            TuSummaryCard(
                totalPending = totalPending,
                isLoading = uploadState.isLoading
            )

            if (hasNewSubmission) {
                Spacer(modifier = Modifier.height(14.dp))

                NewSubmissionNotificationCard(
                    totalPending = totalPending
                )
            }

            if (uploadState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))

                ErrorInfoCard(
                    message = uploadState.errorMessage.orEmpty()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Menu Administrasi",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            TuMenuCard(
                title = "Berkas Seminar Proposal",
                subtitle = "$seminarProposalCount pengajuan menunggu review",
                icon = Icons.Default.UploadFile,
                badgeCount = seminarProposalCount,
                iconBackground = Color(0xFFE3F2FD),
                iconTint = Color(0xFF1976D2),
                onClick = {
                    navController.navigate(
                        Screen.TuDocumentReview.createRoute("seminar_proposal")
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TuMenuCard(
                title = "Berkas Kolokium",
                subtitle = "$kolokiumCount pengajuan menunggu review",
                icon = Icons.Default.AssignmentTurnedIn,
                badgeCount = kolokiumCount,
                iconBackground = Color(0xFFE8F5E9),
                iconTint = Color(0xFF2E7D32),
                onClick = {
                    navController.navigate(
                        Screen.TuDocumentReview.createRoute("kolokium")
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TuMenuCard(
                title = "Berkas Yudisium",
                subtitle = "$yudisiumCount pengajuan menunggu review",
                icon = Icons.Default.WorkspacePremium,
                badgeCount = yudisiumCount,
                iconBackground = Color(0xFFFFF3E0),
                iconTint = Color(0xFFF57C00),
                onClick = {
                    navController.navigate(
                        Screen.TuDocumentReview.createRoute("yudisium")
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TuMenuCard(
                title = "Revisi Seminar Proposal",
                subtitle = "$revisiSeminarProposalCount revisi menunggu review",
                icon = Icons.Default.UploadFile,
                badgeCount = revisiSeminarProposalCount,
                iconBackground = Color(0xFFF3E5F5),
                iconTint = Color(0xFF8E24AA),
                onClick = {
                    navController.navigate(
                        Screen.TuDocumentReview.createRoute("revisi_seminar_proposal")
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            TuMenuCard(
                title = "Revisi Kolokium",
                subtitle = "$revisiKolokiumCount revisi menunggu review",
                icon = Icons.Default.AssignmentTurnedIn,
                badgeCount = revisiKolokiumCount,
                iconBackground = Color(0xFFE0F7FA),
                iconTint = Color(0xFF00838F),
                onClick = {
                    navController.navigate(
                        Screen.TuDocumentReview.createRoute("revisi_kolokium")
                    )
                }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun TuHeader(
    notificationCount: Int,
    isLoggingOut: Boolean,
    onNotificationClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Dashboard TU",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Validasi berkas dan administrasi pengajuan",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onNotificationClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifikasi",
                tint = Color.Black,
                modifier = Modifier.size(25.dp)
            )

            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .size(19.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp)
                        .clip(CircleShape)
                        .background(SimtaRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = notificationCount.coerceAtMost(99).toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(10.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isLoggingOut) Color.LightGray else SimtaRed
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            onClick = onLogoutClick
        ) {
            if (isLoggingOut) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun TuNotificationPanel(
    totalPending: Int,
    seminarProposalCount: Int,
    kolokiumCount: Int,
    yudisiumCount: Int,
    revisiSeminarProposalCount: Int,
    revisiKolokiumCount: Int,
    onOpenSempro: () -> Unit,
    onOpenKolokium: () -> Unit,
    onOpenYudisium: () -> Unit,
    onOpenRevisiSempro: () -> Unit,
    onOpenRevisiKolokium: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Notifikasi",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (totalPending == 0) {
                Text(
                    text = "Belum ada pengajuan baru yang perlu direview.",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            } else {
                Text(
                    text = "$totalPending pengajuan mahasiswa perlu dicek oleh TU.",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (seminarProposalCount > 0) {
                    TuNotificationItem(
                        title = "Seminar Proposal",
                        count = seminarProposalCount,
                        onClick = onOpenSempro
                    )
                }

                if (kolokiumCount > 0) {
                    TuNotificationItem(
                        title = "Kolokium",
                        count = kolokiumCount,
                        onClick = onOpenKolokium
                    )
                }

                if (yudisiumCount > 0) {
                    TuNotificationItem(
                        title = "Yudisium",
                        count = yudisiumCount,
                        onClick = onOpenYudisium
                    )
                }

                if (revisiSeminarProposalCount > 0) {
                    TuNotificationItem(
                        title = "Revisi Seminar Proposal",
                        count = revisiSeminarProposalCount,
                        onClick = onOpenRevisiSempro
                    )
                }

                if (revisiKolokiumCount > 0) {
                    TuNotificationItem(
                        title = "Revisi Kolokium",
                        count = revisiKolokiumCount,
                        onClick = onOpenRevisiKolokium
                    )
                }
            }
        }
    }
}

@Composable
private fun TuNotificationItem(
    title: String,
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8F8F8))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEBEE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = SimtaRed,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$count pengajuan menunggu review",
                color = Color.DarkGray,
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SimtaRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.coerceAtMost(99).toString(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun TuSummaryCard(
    totalPending: Int,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = SimtaRed
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = if (isLoading) {
                        "Memuat Data..."
                    } else {
                        "$totalPending Pengajuan Baru"
                    },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pengajuan mahasiswa yang masih menunggu review TU.",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun NewSubmissionNotificationCard(
    totalPending: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = SimtaRed,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ada Pengajuan Baru",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$totalPending pengajuan mahasiswa perlu dicek oleh TU.",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun ErrorInfoCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Text(
            text = message,
            color = SimtaRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun TuMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeCount: Int,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = Color.DarkGray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(SimtaRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.coerceAtMost(99).toString(),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}