package com.project.screen

import android.widget.Toast
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.component.MahasiswaBottomNavItem
import com.project.component.MahasiswaBottomNavigation
import com.project.core.SimtaRed
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Composable
fun PengajuanScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    LaunchedEffect(authState.userId) {
        uploadBerkasViewModel.loadMySubmissions(authState.userId)
    }

    val seminarProposalSubmission = uploadState.submissions.firstOrNull {
        it.stage == "seminar_proposal"
    }

    val kolokiumSubmission = uploadState.submissions.firstOrNull {
        it.stage == "pendaftaran_kolokium" || it.stage == "kolokium"
    }

    val yudisiumSubmission = uploadState.submissions.firstOrNull {
        it.stage == "yudisium"
    }

    val kolokiumApproved = uploadState.submissions.any {
        (it.stage == "pendaftaran_kolokium" || it.stage == "kolokium") &&
                it.status.isApprovedStatus()
    }

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
                    title = "Pilih Dosen Pembimbing",
                    subtitle = "Lihat dan pilih dosen pembimbing sesuai program studi",
                    icon = Icons.Default.School,
                    onClick = {
                        navController.navigate(Screen.LecturerList.route)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (seminarProposalSubmission != null) {
                    RegistrationStatusCard(
                        title = "Pendaftaran Seminar Proposal",
                        status = seminarProposalSubmission.status,
                        date = seminarProposalSubmission.createdAt,
                        onDetailClick = {
                            Toast.makeText(
                                context,
                                "Status Seminar Proposal: ${seminarProposalSubmission.status.toReadableSubmissionStatus()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                PengajuanMenuCard(
                    title = "Pendaftaran Seminar Proposal",
                    subtitle = "Daftar dan lengkapi layanan seminar proposal",
                    icon = Icons.Default.Article,
                    onClick = {
                        navController.navigate(Screen.PendaftaranSeminarProposal.route)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (kolokiumSubmission != null) {
                    RegistrationStatusCard(
                        title = "Pendaftaran Kolokium",
                        status = kolokiumSubmission.status,
                        date = kolokiumSubmission.createdAt,
                        onDetailClick = {
                            Toast.makeText(
                                context,
                                "Status Kolokium: ${kolokiumSubmission.status.toReadableSubmissionStatus()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                PengajuanMenuCard(
                    title = "Pendaftaran Kolokium",
                    subtitle = "Daftar dan lengkapi layanan kolokium",
                    icon = Icons.Default.Assignment,
                    onClick = {
                        navController.navigate(Screen.PendaftaranKolokium.route)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (yudisiumSubmission != null) {
                    RegistrationStatusCard(
                        title = "Pendaftaran Yudisium",
                        status = yudisiumSubmission.status,
                        date = yudisiumSubmission.createdAt,
                        onDetailClick = {
                            Toast.makeText(
                                context,
                                "Status Yudisium: ${yudisiumSubmission.status.toReadableSubmissionStatus()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                PengajuanMenuCard(
                    title = "Pendaftaran Yudisium",
                    subtitle = "Daftar dan lengkapi berkas yudisium",
                    icon = Icons.Default.Person,
                    onClick = {
                        if (kolokiumApproved) {
                            navController.navigate(
                                Screen.UploadBerkas.createRoute("yudisium")
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Anda harus menyelesaikan Kolokium terlebih dahulu.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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
            .height(92.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SimtaRed
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    lineHeight = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun RegistrationStatusCard(
    title: String,
    status: String,
    date: String?,
    onDetailClick: () -> Unit
) {
    val approved = status.isApprovedStatus()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
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
            Text(
                text = if (approved) "✓ $title" else title,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (approved) Color(0xFF2E7D32) else Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Status: ${status.toReadableSubmissionStatus()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )

            if (!date.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tanggal daftar: ${date.take(10)}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDetailClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SimtaRed
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Lihat Detail",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

fun String.isApprovedStatus(): Boolean {
    return this == "disetujui_tu" ||
            this == "disetujui_kaprodi" ||
            this == "approved" ||
            this == "accepted"
}

fun String.toReadableSubmissionStatus(): String {
    return when (this) {
        "menunggu_review" -> "Menunggu Review TU"
        "disetujui_tu" -> "Disetujui TU"
        "ditolak_tu" -> "Ditolak TU"
        "disetujui_kaprodi" -> "Disetujui Kaprodi"
        "ditolak_kaprodi" -> "Ditolak Kaprodi"
        "approved" -> "Disetujui"
        "accepted" -> "Diterima"
        "rejected" -> "Ditolak"
        "pending" -> "Menunggu"
        else -> this
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
    }
}