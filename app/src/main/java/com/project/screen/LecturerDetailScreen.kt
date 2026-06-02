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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.auth.AuthViewModel
import com.project.core.SimtaGreen
import com.project.core.SimtaRed
import com.project.data.model.Lecturer
import com.project.lecturer.LecturerViewModel
import com.project.supervisor.SupervisorRequestViewModel

@Composable
fun LecturerDetailScreen(
    lecturerId: Long,
    lecturerViewModel: LecturerViewModel,
    supervisorRequestViewModel: SupervisorRequestViewModel,
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    onAjukanClick: () -> Unit
) {
    val lecturerState by lecturerViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (lecturerState.lecturers.isEmpty()) {
            lecturerViewModel.loadLecturers()
        }
        supervisorRequestViewModel.resetState()
    }

    val lecturer: Lecturer? = lecturerState.lecturers.find { it.id == lecturerId }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
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
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 20.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Profil Dosen",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                lecturerState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                }

                lecturer == null -> {
                    Text(
                        text = "Dosen tidak ditemukan.",
                        color = Color.White,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        LecturerInfoCardModern(lecturer = lecturer)

                        Spacer(modifier = Modifier.height(24.dp))

                        AjukanJudulProposalCard(
                            lecturer = lecturer,
                            onAjukanClick = onAjukanClick
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AjukanJudulProposalCard(
    lecturer: Lecturer,
    onAjukanClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Form Pengajuan Judul Proposal",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ajukan judul proposal skripsi melalui form pengajuan. Pastikan data dan judul yang diajukan sudah sesuai.",
                color = Color(0xFF6F6F6F),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAjukanClick,
                enabled = lecturer.isAvailable,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SimtaRed,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    disabledContentColor = Color.Gray
                )
            ) {
                Text(
                    text = "Ajukan Sekarang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            if (!lecturer.isAvailable) {
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = SimtaRed.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = SimtaRed,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Kuota dosen penuh. Coba pilih dosen lain.",
                            color = SimtaRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LecturerInfoCardModern(
    lecturer: Lecturer
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F3F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = lecturer.fullName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )

                    Surface(
                        color = SimtaRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = lecturer.expertise ?: "General",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            ),
                            color = SimtaRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Divider(
                color = Color(0xFFF1F3F4),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoItem(
                    label = "Kuota Max",
                    value = "${lecturer.quota}"
                )

                InfoItem(
                    label = "Bimbingan",
                    value = "${lecturer.currentStudents}"
                )

                InfoItem(
                    label = "Sisa Kuota",
                    value = "${lecturer.remainingQuota}",
                    valueColor = if (lecturer.isAvailable) {
                        SimtaGreen
                    } else {
                        SimtaRed
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            fontSize = 16.sp,
            color = valueColor,
            fontWeight = FontWeight.ExtraBold
        )
    }
}