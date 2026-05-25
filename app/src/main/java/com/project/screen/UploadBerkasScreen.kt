package com.project.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.upload.UploadBerkasViewModel

private data class UploadRequiredDocument(
    val key: String,
    val title: String,
    val description: String
)

@Composable
fun UploadBerkasScreen(
    navController: NavHostController,
    stage: String,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    val selectedFiles = remember {
        mutableStateMapOf<String, Uri>()
    }

    val documents = remember(stage) {
        getUploadRequiredDocuments(stage)
    }

    val stageTitle = when (stage) {
        "seminar_proposal" -> "Seminar Proposal"
        "kolokium" -> "Kolokium"
        "yudisium" -> "Yudisium"
        else -> "Pengajuan"
    }

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            uploadBerkasViewModel.resetState()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            Button(
                onClick = {
                    uploadBerkasViewModel.uploadDocuments(
                        context = context,
                        userId = authState.userId,
                        stage = stage,
                        files = selectedFiles.toMap()
                    )
                },
                enabled = selectedFiles.size == documents.size && !uploadState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SimtaRed,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                if (uploadState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Kirim Berkas ke TU",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
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
                        text = "Upload Berkas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = stageTitle,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            UploadInfoCard(
                title = "Catatan",
                message = "File akan diupload ke Supabase Storage bucket thesis-files. Setelah dikirim, TU dapat mengecek kelengkapan berkas."
            )

            if (uploadState.errorMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))

                UploadErrorCard(
                    message = uploadState.errorMessage ?: "Terjadi kesalahan"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            documents.forEachIndexed { index, document ->
                UploadDocumentItemCard(
                    number = index + 1,
                    document = document,
                    selectedUri = selectedFiles[document.key],
                    onFileSelected = { uri ->
                        selectedFiles[document.key] = uri
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun UploadDocumentItemCard(
    number: Int,
    document: UploadRequiredDocument,
    selectedUri: Uri?,
    onFileSelected: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onFileSelected(uri)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
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
                        .size(38.dp)
                        .background(
                            color = SimtaRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        fontWeight = FontWeight.ExtraBold,
                        color = SimtaRed
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = document.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = document.description,
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }

                Icon(
                    imageVector = if (selectedUri == null) {
                        Icons.Default.Description
                    } else {
                        Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = if (selectedUri == null) {
                        Color.Gray
                    } else {
                        Color(0xFF4CAF50)
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = Color(0xFFE0E0E0))

            Spacer(modifier = Modifier.height(14.dp))

            if (selectedUri != null) {
                Text(
                    text = "File sudah dipilih",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedButton(
                onClick = {
                    launcher.launch(
                        arrayOf(
                            "application/pdf",
                            "image/*"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.UploadFile,
                    contentDescription = null,
                    tint = SimtaRed
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = if (selectedUri == null) "Pilih File" else "Ganti File",
                    color = SimtaRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UploadInfoCard(
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SimtaRed
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message,
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun UploadErrorCard(
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

private fun getUploadRequiredDocuments(stage: String): List<UploadRequiredDocument> {
    return when (stage) {
        "seminar_proposal" -> listOf(
            UploadRequiredDocument(
                key = "proposal",
                title = "File Proposal Skripsi",
                description = "Upload proposal skripsi dalam format PDF."
            ),
            UploadRequiredDocument(
                key = "kartu_bimbingan",
                title = "Kartu Bimbingan",
                description = "Upload bukti bimbingan dengan dosen pembimbing."
            ),
            UploadRequiredDocument(
                key = "krs",
                title = "KRS Terbaru",
                description = "Upload KRS semester berjalan."
            )
        )

        "kolokium" -> listOf(
            UploadRequiredDocument(
                key = "laporan_hasil",
                title = "Laporan Hasil Penelitian",
                description = "Upload laporan hasil atau draft skripsi."
            ),
            UploadRequiredDocument(
                key = "kartu_bimbingan",
                title = "Kartu Bimbingan",
                description = "Upload kartu bimbingan terbaru."
            ),
            UploadRequiredDocument(
                key = "bukti_sempro",
                title = "Bukti Lulus Seminar Proposal",
                description = "Upload bukti telah mengikuti seminar proposal."
            )
        )

        "yudisium" -> listOf(
            UploadRequiredDocument(
                key = "skripsi_final",
                title = "Skripsi Final",
                description = "Upload skripsi final yang sudah disetujui."
            ),
            UploadRequiredDocument(
                key = "lembar_pengesahan",
                title = "Lembar Pengesahan",
                description = "Upload lembar pengesahan yang sudah ditandatangani."
            ),
            UploadRequiredDocument(
                key = "bebas_perpus",
                title = "Surat Bebas Perpustakaan",
                description = "Upload surat bebas perpustakaan."
            ),
            UploadRequiredDocument(
                key = "transkrip",
                title = "Transkrip Nilai",
                description = "Upload transkrip nilai terbaru."
            )
        )

        else -> emptyList()
    }
}