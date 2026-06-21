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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.core.SimtaRed
import com.project.data.model.ThesisSubmission
import com.project.data.model.ThesisSubmissionDocument
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Composable
fun TuDocumentReviewScreen(
    navController: NavHostController,
    stage: String,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    var openedSubmissionId by remember {
        mutableStateOf<String?>(null)
    }

    val title = when (stage) {
        "seminar_proposal" -> "Review Berkas Seminar Proposal"
        "kolokium" -> "Review Berkas Kolokium"
        "pendaftaran_kolokium" -> "Review Berkas Kolokium"
        "yudisium" -> "Review Berkas Yudisium"
        "revisi_seminar_proposal" -> "Review Revisi Seminar Proposal"
        "revisi_kolokium" -> "Review Revisi Kolokium"
        else -> "Review Berkas"
    }

    LaunchedEffect(stage) {
        uploadBerkasViewModel.loadSubmissionsByStage(stage)
    }

    LaunchedEffect(uploadState.errorMessage) {
        uploadState.errorMessage?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
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

            if (uploadState.isLoading && uploadState.submissions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = SimtaRed
                    )
                }
            } else if (uploadState.submissions.isEmpty()) {
                EmptyReviewCard(
                    message = "Belum ada pengajuan untuk tahap ini."
                )
            } else {
                uploadState.submissions.forEach { submission ->
                    TuSubmissionCard(
                        submission = submission,
                        documents = if (openedSubmissionId == submission.id) {
                            uploadState.selectedDocuments
                        } else {
                            emptyList()
                        },
                        isDocumentsOpen = openedSubmissionId == submission.id,
                        onViewDocument = {
                            openedSubmissionId = submission.id
                            uploadBerkasViewModel.loadDocumentsBySubmissionId(submission.id)
                        },
                        onOpenFile = { fileUrl ->
                            runCatching {
                                uriHandler.openUri(fileUrl)
                            }.onFailure {
                                Toast.makeText(
                                    context,
                                    "Gagal membuka file.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onApprove = {
                            uploadBerkasViewModel.approveSubmission(
                                submissionId = submission.id,
                                reloadStage = stage
                            )
                        },
                        onPlotting = {
                            navController.navigate(
                                Screen.TuPlottingPenguji.createRoute(submission.id)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyReviewCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 13.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TuSubmissionCard(
    submission: ThesisSubmission,
    documents: List<ThesisSubmissionDocument>,
    isDocumentsOpen: Boolean,
    onViewDocument: () -> Unit,
    onOpenFile: (String) -> Unit,
    onApprove: () -> Unit,
    onPlotting: () -> Unit
) {
    val statusText = when (submission.status) {
        "menunggu_review" -> "Menunggu Review TU"
        "disetujui_tu" -> "Disetujui TU"
        "ditolak_tu" -> "Ditolak TU"
        else -> submission.status
    }

    val statusColor = when (submission.status) {
        "menunggu_review" -> Color(0xFFFFA000)
        "disetujui_tu" -> Color(0xFF2E7D32)
        "ditolak_tu" -> SimtaRed
        else -> Color.DarkGray
    }

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
                        text = submission.studentName ?: "Nama tidak tersedia",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "${submission.nim ?: "-"} • ${submission.title ?: "Tanpa judul"}",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Status: $statusText",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )

            if (!submission.supervisor1.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Pembimbing: ${submission.supervisor1}",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            if (
                !submission.examiner1.isNullOrBlank() ||
                !submission.examiner2.isNullOrBlank()
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                ExaminerPlottingInfoCard(
                    examiner1 = submission.examiner1,
                    examiner2 = submission.examiner2
                )
            }

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
                        text = if (isDocumentsOpen) "Muat Ulang" else "Lihat",
                        color = SimtaRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onApprove,
                    enabled = submission.status == "menunggu_review",
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaRed,
                        disabledContainerColor = Color.LightGray
                    )
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

            if (isDocumentsOpen) {
                Spacer(modifier = Modifier.height(12.dp))

                if (documents.isEmpty()) {
                    Text(
                        text = "Dokumen belum termuat atau tidak ada dokumen.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                } else {
                    documents.forEach { document ->
                        DocumentFileRow(
                            document = document,
                            onOpenFile = {
                                onOpenFile(document.fileUrl)
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }
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

@Composable
private fun ExaminerPlottingInfoCard(
    examiner1: String?,
    examiner2: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF8F9FA),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = "Dosen Penguji",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExaminerTextRow(
            label = "Penguji 1",
            value = examiner1
        )

        if (!examiner2.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(5.dp))

            ExaminerTextRow(
                label = "Penguji 2",
                value = examiner2
            )
        }
    }
}

@Composable
private fun ExaminerTextRow(
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$label:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.weight(0.35f)
        )

        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "-",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.weight(0.65f)
        )
    }
}

@Composable
private fun DocumentFileRow(
    document: ThesisSubmissionDocument,
    onOpenFile: () -> Unit
) {
    OutlinedButton(
        onClick = onOpenFile,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = SimtaRed,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = document.documentName,
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = document.documentKey,
                color = Color.Gray,
                fontSize = 10.sp
            )
        }
    }
}