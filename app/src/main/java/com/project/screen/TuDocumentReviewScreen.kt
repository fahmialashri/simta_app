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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.core.SimtaRed
import com.project.navigation.Screen

data class TuDocumentReviewUi(
    val stageId: String,
    val studentName: String,
    val nim: String,
    val stage: String,
    val documentCount: Int,
    val status: String
)

@Composable
fun TuDocumentReviewScreen(
    navController: NavHostController,
    stage: String
) {
    val title = when (stage) {
        "seminar_proposal" -> "Review Berkas Seminar Proposal"
        "kolokium" -> "Review Berkas Kolokium"
        "yudisium" -> "Review Berkas Yudisium"
        else -> "Review Berkas"
    }

    val items = listOf(
        TuDocumentReviewUi(
            stageId = "stage_1",
            studentName = "Ahmad Fauzi",
            nim = "202101001",
            stage = stage,
            documentCount = 3,
            status = "menunggu_review"
        ),
        TuDocumentReviewUi(
            stageId = "stage_2",
            studentName = "Siti Nurhaliza",
            nim = "202101002",
            stage = stage,
            documentCount = 3,
            status = "menunggu_review"
        )
    )

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
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.Black
                    )
                }

                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Validasi kelengkapan berkas mahasiswa",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            items.forEach { item ->
                TuDocumentCard(
                    item = item,
                    onViewDocument = {
                        // Nanti buka file dari Supabase Storage
                    },
                    onApprove = {
                        // Nanti update status stage_documents jadi valid
                    },
                    onPlotting = {
                        navController.navigate(Screen.TuPlottingPenguji.createRoute(item.stageId))
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun TuDocumentCard(
    item: TuDocumentReviewUi,
    onViewDocument: () -> Unit,
    onApprove: () -> Unit,
    onPlotting: () -> Unit
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
                        .background(SimtaRed.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
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
                        text = "${item.nim} • ${item.documentCount} berkas",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Status: Menunggu Review TU",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA000)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDocument,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = SimtaRed,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = "Lihat",
                        color = SimtaRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SimtaRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.size(6.dp))

                    Text(
                        text = "Valid",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onPlotting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.GroupAdd,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Plotting Dosen Penguji",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}