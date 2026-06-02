package com.project.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.component.AppDropdownField
import com.project.component.AppTextField
import com.project.component.FormCard
import com.project.component.FormTopBar
import com.project.component.InfoBox
import com.project.component.SectionTitle
import com.project.component.UploadFileCard
import com.project.component.UploadFileItem
import com.project.component.UploadPageAlert
import com.project.core.SimtaYellow
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Composable
fun UploadRevisiSeminarProposalScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    var nama by remember { mutableStateOf(authState.name.orEmpty()) }
    var npm by remember { mutableStateOf(authState.nim.orEmpty()) }
    var pembimbing by remember { mutableStateOf("") }
    var penguji by remember { mutableStateOf("") }
    var tanggalProposal by remember { mutableStateOf("") }
    var judulRevisi by remember { mutableStateOf("") }
    var isDraftSaved by remember { mutableStateOf(false) }

    val selectedFileNames = remember {
        mutableStateMapOf<String, String>()
    }

    val selectedFileUris = remember {
        mutableStateMapOf<String, Uri>()
    }

    val documents = remember {
        listOf(
            UploadFileItem(
                key = "soft_file_proposal_revisi",
                title = "Soft File Proposal Revisi",
                description = "Format Word atau PDF (.pdf / .docx), maks. 10 MB",
                mimeTypes = listOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            ),
            UploadFileItem(
                key = "bukti_persetujuan_pembimbing",
                title = "Bukti Persetujuan Pembimbing",
                description = "Screenshot persetujuan pembimbing"
            ),
            UploadFileItem(
                key = "bukti_persetujuan_penguji",
                title = "Bukti Persetujuan Penguji",
                description = "Screenshot persetujuan penguji"
            )
        )
    }

    val isValid =
        nama.isNotBlank() &&
                npm.isNotBlank() &&
                pembimbing.isNotBlank() &&
                penguji.isNotBlank() &&
                tanggalProposal.isNotBlank() &&
                judulRevisi.isNotBlank() &&
                documents.all { selectedFileUris.containsKey(it.key) } &&
                !uploadState.isLoading

    LaunchedEffect(authState.name, authState.nim) {
        if (nama.isBlank()) {
            nama = authState.name.orEmpty()
        }

        if (npm.isBlank()) {
            npm = authState.nim.orEmpty()
        }
    }

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            Toast.makeText(
                context,
                "Revisi seminar proposal berhasil dikirim ke TU",
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
                title = "Revisi Proposal Skripsi",
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
                    text = "Form revisi setelah seminar proposal. Pastikan judul revisi sudah disetujui pembimbing dan penguji."
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Nama",
                        value = nama,
                        onValueChange = { nama = it }
                    )

                    AppTextField(
                        label = "NPM",
                        value = npm,
                        onValueChange = { npm = it }
                    )

                    AppDropdownField(
                        label = "Pembimbing Proposal",
                        placeholder = "-- Pilih dosen pembimbing --",
                        value = pembimbing,
                        options = sampleLecturersRevisiSempro(),
                        onSelect = { pembimbing = it }
                    )

                    AppDropdownField(
                        label = "Penguji Proposal",
                        placeholder = "-- Pilih dosen penguji --",
                        value = penguji,
                        options = sampleExaminersRevisiSempro(),
                        onSelect = { penguji = it }
                    )

                    AppTextField(
                        label = "Tanggal Pelaksanaan Proposal",
                        placeholder = "Contoh: Rabu, 30 Oktober 2019",
                        value = tanggalProposal,
                        onValueChange = { tanggalProposal = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Berkas Revisi"
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
                SectionTitle(
                    icon = Icons.Rounded.Edit,
                    title = "Judul Revisi"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Judul Proposal Setelah Direvisi",
                        placeholder = "Tulis judul yang sudah direvisi...",
                        value = judulRevisi,
                        minLines = 4,
                        onValueChange = { judulRevisi = it }
                    )
                }
            }

            item {
                InfoBox(
                    title = if (isDraftSaved) {
                        "Draft sudah disimpan"
                    } else {
                        "Catatan penting"
                    },
                    description = if (isDraftSaved) {
                        "Draft revisi seminar proposal tersimpan sementara di halaman ini."
                    } else {
                        "Pastikan judul revisi sudah disetujui oleh pembimbing dan penguji sebelum dikirim."
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isDraftSaved = true

                            Toast.makeText(
                                context,
                                "Draft revisi seminar proposal berhasil disimpan",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        enabled = !uploadState.isLoading,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text(text = "Simpan Draft")
                    }

                    Button(
                        onClick = {
                            uploadBerkasViewModel.submitRegistration(
                                context = context,
                                userId = authState.userId,
                                stage = "revisi_seminar_proposal",
                                studentName = nama.trim(),
                                nim = npm.trim(),
                                phone = null,
                                title = judulRevisi.trim(),
                                titleEnglish = null,
                                supervisor1 = pembimbing.trim(),
                                supervisor2 = null,
                                examiner1 = penguji.trim(),
                                examiner2 = null,
                                files = selectedFileUris.toMap()
                            )
                        },
                        enabled = isValid,
                        modifier = Modifier.weight(1f),
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
                                "Kirim Revisi"
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun sampleLecturersRevisiSempro(): List<String> {
    return listOf(
        "Dr. Dosen Pembimbing 1",
        "Dr. Dosen Pembimbing 2",
        "Dr. Dosen Pembimbing 3",
        "Dr. Dosen Pembimbing 4"
    )
}

private fun sampleExaminersRevisiSempro(): List<String> {
    return listOf(
        "Dr. Dosen Penguji 1",
        "Dr. Dosen Penguji 2",
        "Dr. Dosen Penguji 3",
        "Dr. Dosen Penguji 4"
    )
}