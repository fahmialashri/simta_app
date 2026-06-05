package com.project.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.data.model.DosenReviewItem
import com.project.data.model.DosenSupervisedStudentItem
import com.project.dosen.DosenDashboardViewModel
import com.project.navigation.Screen

@Composable
fun DosenDashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    dosenDashboardViewModel: DosenDashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()
    val dashboardState by dosenDashboardViewModel.uiState.collectAsState()

    val currentRole = authState.role?.trim()?.lowercase()

    val waitingReviews: List<DosenReviewItem> = dashboardState.reviews.filter { review ->
        review.chapter.status == "process"
    }

    val historyReviews: List<DosenReviewItem> = dashboardState.reviews.filter { review ->
        review.chapter.status != "process" && review.submissions.isNotEmpty()
    }

    LaunchedEffect(currentRole) {
        when (currentRole) {
            "kaprodi" -> {
                navController.navigate(Screen.KaprodiDashboard.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            "tu" -> {
                navController.navigate(Screen.TuDashboard.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

            "mahasiswa" -> {
                navController.navigate(Screen.MahasiswaDashboard.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    LaunchedEffect(currentRole, authState.lecturerId) {
        if (currentRole == "dosen") {
            dosenDashboardViewModel.loadDashboard(authState.lecturerId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SimtaRed,
                            SimtaRed.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            HeaderSection(
                name = authState.name ?: "Dosen Pembimbing",
                nidn = authState.nidn ?: "-",
                onRefreshClick = {
                    if (currentRole == "dosen") {
                        dosenDashboardViewModel.loadDashboard(authState.lecturerId)
                    }
                },
                onLogoutClick = {
                    authViewModel.logout(
                        onSuccess = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallInfoCard(
                    title = "Mahasiswa Bimbingan",
                    value = dashboardState.supervisedStudents.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                SmallInfoCard(
                    title = "Review BAB",
                    value = waitingReviews.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                when {
                    dashboardState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SimtaRed)
                        }
                    }

                    dashboardState.errorMessage != null -> {
                        ErrorCard(
                            message = dashboardState.errorMessage ?: "Terjadi kesalahan"
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                dashboardState.successMessage?.let { message ->
                    SuccessCard(message = message)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "Mahasiswa Bimbingan",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!dashboardState.isLoading && dashboardState.errorMessage == null) {
                    if (dashboardState.supervisedStudents.isEmpty()) {
                        EmptyCard(
                            text = "Belum ada mahasiswa bimbingan yang sudah disetujui Kaprodi."
                        )
                    } else {
                        dashboardState.supervisedStudents.forEach { studentItem ->
                            SupervisedStudentCard(item = studentItem)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Review Menunggu",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!dashboardState.isLoading && dashboardState.errorMessage == null) {
                    if (waitingReviews.isEmpty()) {
                        EmptyCard(
                            text = "Belum ada dokumen BAB yang menunggu diperiksa."
                        )
                    } else {
                        waitingReviews.forEach { reviewItem ->
                            ReviewCard(
                                item = reviewItem,
                                onOpenUrl = { url ->
                                    context.openUrl(url)
                                },
                                onApproveClick = { note ->
                                    dosenDashboardViewModel.approveChapter(
                                        chapterId = reviewItem.chapter.id,
                                        lecturerId = authState.lecturerId,
                                        note = note
                                    )
                                },
                                onRevisionClick = { note ->
                                    dosenDashboardViewModel.requestRevision(
                                        chapterId = reviewItem.chapter.id,
                                        lecturerId = authState.lecturerId,
                                        note = note
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "History Bimbingan",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!dashboardState.isLoading && dashboardState.errorMessage == null) {
                    if (historyReviews.isEmpty()) {
                        EmptyCard(
                            text = "Belum ada history bimbingan mahasiswa."
                        )
                    } else {
                        historyReviews.forEach { historyItem ->
                            HistoryCard(
                                item = historyItem,
                                onOpenUrl = { url ->
                                    context.openUrl(url)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SupervisedStudentCard(
    item: DosenSupervisedStudentItem
) {
    val studentName = item.student?.fullName ?: "Mahasiswa tidak ditemukan"
    val nim = item.student?.nim ?: "-"
    val title = item.request.title ?: "Judul belum diisi"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = SimtaRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SupervisorAccount,
                        contentDescription = null,
                        tint = SimtaRed
                    )
                }

                Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = studentName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "NIM: $nim",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.supervisorRole,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SimtaRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Judul Skripsi",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                Text(
                    text = "Status: Mahasiswa Bimbingan ${item.supervisorRole}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

private fun Context.openUrl(url: String) {
    try {
        val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else {
            url
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl))
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}