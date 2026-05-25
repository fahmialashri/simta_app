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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.core.SimtaRed
import com.project.data.repository.KaprodiSubmissionData
import com.project.kaprodi.KaprodiViewModel

@Composable
fun KaprodiDashboardScreen(
    navController: NavHostController,
    onLogout: () -> Unit = {},
    viewModel: KaprodiViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPendingRequests()
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Dashboard Kaprodi",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Persetujuan pengajuan dosen pembimbing",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SimtaRed),
                    onClick = onLogout
                ) {
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

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KaprodiStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Menunggu",
                    value = state.submissions.size.toString(),
                    icon = Icons.Default.Timer,
                    color = Color(0xFFFFA000)
                )

                KaprodiStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Status",
                    value = if (state.isLoading) "..." else "Aktif",
                    icon = Icons.Default.CheckCircle,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Daftar Pengajuan",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SimtaRed)
                    }
                }

                state.errorMessage != null -> {
                    KaprodiMessageCard(
                        message = state.errorMessage ?: "Terjadi kesalahan",
                        isError = true
                    )
                }

                state.submissions.isEmpty() -> {
                    EmptyKaprodiCard()
                }

                else -> {
                    state.submissions.forEach { item ->
                        KaprodiSubmissionCard(
                            item = item,
                            onApprove = {
                                viewModel.approveRequest(item.id)
                            },
                            onReject = {
                                viewModel.rejectRequest(item.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }

    if (state.successMessage != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearMessage()
            },
            title = {
                Text(
                    text = "Berhasil",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = state.successMessage ?: "",
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearMessage()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaRed
                    )
                ) {
                    Text(
                        text = "Tutup",
                        color = Color.White
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun KaprodiStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = color.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun KaprodiSubmissionCard(
    item: KaprodiSubmissionData,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = SimtaRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = SimtaRed
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.studentName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = item.nim,
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }

                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFFA000)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Judul Skripsi",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Dosen Pembimbing yang Dipilih",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = SimtaRed,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = item.lecturerName,
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Status: ${item.status}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA000)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tolak",
                        color = SimtaRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaRed
                    )
                ) {
                    Text(
                        text = "Setujui",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyKaprodiCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Belum Ada Pengajuan",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pengajuan dosen pembimbing dari mahasiswa akan muncul di sini.",
                fontSize = 12.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun KaprodiMessageCard(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
        )
    ) {
        Text(
            text = message,
            color = if (isError) SimtaRed else Color(0xFF2E7D32),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}