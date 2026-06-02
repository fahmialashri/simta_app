package com.project.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.component.FormTopBar
import com.project.component.SectionTitle
import com.project.component.UploadFileCard
import com.project.component.UploadFileItem
import com.project.component.UploadPageAlert
import com.project.core.SimtaYellow
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Composable
fun GenericUploadBerkasScreen(
    navController: NavHostController,
    stage: String,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    val selectedFileNames = remember {
        mutableStateMapOf<String, String>()
    }

    val selectedFileUris = remember {
        mutableStateMapOf<String, Uri>()
    }

    val title = when (stage) {
        "kolokium" -> "Upload Berkas Kolokium"
        "yudisium" -> "Upload Berkas Yudisium"
        "seminar_proposal" -> "Upload Berkas Seminar Proposal"
        else -> "Upload Berkas"
    }

    val successMessage = when (stage) {
        "kolokium" -> "Berkas kolokium berhasil dikirim ke TU"
        "yudisium" -> "Berkas yudisium berhasil dikirim ke TU"
        "seminar_proposal" -> "Berkas seminar proposal berhasil dikirim ke TU"
        else -> "Berkas berhasil dikirim ke TU"
    }

    val documents = remember(stage) {
        when (stage) {
            "kolokium" -> listOf(
                UploadFileItem(
                    key = "draft_skripsi",
                    title = "Draft Skripsi",
                    description = "Upload draft skripsi dalam format PDF atau DOCX",
                    mimeTypes = listOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )
                ),
                UploadFileItem(
                    key = "kartu_bimbingan",
                    title = "Kartu Bimbingan",
                    description = "Upload kartu bimbingan yang sudah ditandatangani"
                ),
                UploadFileItem(
                    key = "bukti_persetujuan",
                    title = "Bukti Persetujuan Pembimbing",
                    description = "Upload bukti persetujuan dari dosen pembimbing"
                )
            )

            "yudisium" -> listOf(
                UploadFileItem(
                    key = "skripsi_final",
                    title = "Skripsi Final",
                    description = "Upload skripsi final yang sudah disahkan",
                    mimeTypes = listOf("application/pdf")
                ),
                UploadFileItem(
                    key = "lembar_pengesahan",
                    title = "Lembar Pengesahan",
                    description = "Upload lembar pengesahan yang sudah ditandatangani",
                    mimeTypes = listOf(
                        "application/pdf",
                        "image/jpeg",
                        "image/png"
                    )
                ),
                UploadFileItem(
                    key = "bebas_perpustakaan",
                    title = "Surat Bebas Perpustakaan",
                    description = "Upload surat bebas perpustakaan"
                )
            )

            "seminar_proposal" -> listOf(
                UploadFileItem(
                    key = "bukti_lunas_ukt",
                    title = "Bukti Lunas Administrasi UKT",
                    description = "Upload bukti lunas administrasi UKT"
                ),
                UploadFileItem(
                    key = "krs_pengambilan_skripsi",
                    title = "KRS Pengambilan Skripsi",
                    description = "Upload KRS pengambilan skripsi"
                ),
                UploadFileItem(
                    key = "file_proposal_skripsi",
                    title = "File Proposal Skripsi",
                    description = "Upload file proposal skripsi",
                    mimeTypes = listOf(
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    )
                )
            )

            else -> listOf(
                UploadFileItem(
                    key = "dokumen_utama",
                    title = "Dokumen Utama",
                    description = "Upload dokumen utama yang dibutuhkan"
                ),
                UploadFileItem(
                    key = "dokumen_pendukung",
                    title = "Dokumen Pendukung",
                    description = "Upload dokumen pendukung jika diperlukan"
                )
            )
        }
    }

    val isValid = documents.all { document ->
        selectedFileUris.containsKey(document.key)
    } && !uploadState.isLoading

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            Toast.makeText(
                context,
                successMessage,
                Toast.LENGTH_SHORT
            ).show()

            uploadBerkasViewModel.resetState()

            navController.navigate(Screen.MahasiswaDashboard.route) {
                popUpTo(Screen.Pengajuan.route) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
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
        topBar = {
            FormTopBar(
                title = title,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 14.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                UploadPageAlert(
                    text = "Lengkapi berkas yang dibutuhkan sebelum dikirim."
                )
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Berkas"
                )
            }

            documents.forEach { document ->
                item {
                    UploadFileCard(
                        item = document,
                        selectedFileName = selectedFileNames[document.key],
                        onFilePicked = { uri, fileName ->
                            selectedFileUris[document.key] = uri
                            selectedFileNames[document.key] = fileName
                        },
                        onRemoveFile = {
                            selectedFileUris.remove(document.key)
                            selectedFileNames.remove(document.key)
                        }
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        uploadBerkasViewModel.submitRegistration(
                            context = context,
                            userId = authState.userId,
                            stage = stage,
                            studentName = authState.name,
                            nim = authState.nim,
                            phone = null,
                            title = null,
                            titleEnglish = null,
                            supervisor1 = null,
                            supervisor2 = null,
                            examiner1 = null,
                            examiner2 = null,
                            files = selectedFileUris.toMap()
                        )
                    },
                    enabled = isValid,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaYellow,
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFFE0E0E0),
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Text(
                        text = if (uploadState.isLoading) {
                            "Mengirim..."
                        } else {
                            "Kirim Berkas"
                        }
                    )
                }
            }
        }
    }
}