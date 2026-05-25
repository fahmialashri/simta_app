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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun TuDashboardScreen(
    navController: NavHostController,
    onLogout: () -> Unit = {}
) {
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
                        text = "Dashboard TU",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Validasi berkas dan plotting dosen penguji",
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

            Spacer(modifier = Modifier.height(24.dp))

            TuSummaryCard()

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Menu Administrasi",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            TuMenuCard(
                title = "Berkas Seminar Proposal",
                subtitle = "Cek kelengkapan berkas sempro mahasiswa",
                icon = Icons.Default.UploadFile,
                colors = listOf(Color(0xFFE3F2FD), Color(0xFF64B5F6)),
                onClick = {
                    navController.navigate(Screen.TuDocumentReview.createRoute("seminar_proposal"))
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            TuMenuCard(
                title = "Berkas Kolokium",
                subtitle = "Cek berkas sidang hasil / kolokium",
                icon = Icons.Default.AssignmentTurnedIn,
                colors = listOf(Color(0xFFE8F5E9), Color(0xFF81C784)),
                onClick = {
                    navController.navigate(Screen.TuDocumentReview.createRoute("kolokium"))
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            TuMenuCard(
                title = "Berkas Yudisium",
                subtitle = "Validasi berkas akhir mahasiswa",
                icon = Icons.Default.WorkspacePremium,
                colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFB74D)),
                onClick = {
                    navController.navigate(Screen.TuDocumentReview.createRoute("yudisium"))
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            TuMenuCard(
                title = "Plotting Dosen Penguji",
                subtitle = "Atur dosen penguji dan pembimbing tambahan",
                icon = Icons.Default.ManageAccounts,
                colors = listOf(Color(0xFFFCE4EC), Color(0xFFF06292)),
                onClick = {
                    navController.navigate(Screen.TuPlottingPenguji.createRoute("default_stage_id"))
                }
            )
        }
    }
}

@Composable
private fun TuSummaryCard() {
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
                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = "Administrasi Skripsi",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pantau pengajuan, validasi berkas, dan tentukan dosen penguji.",
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun TuMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .size(54.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 11.sp
                )
            }
        }
    }
}