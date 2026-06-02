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
fun PendaftaranYudisiumFormScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    var nama by remember { mutableStateOf(authState.name.orEmpty()) }
    var npm by remember { mutableStateOf(authState.nim.orEmpty()) }
    var nomorHp by remember { mutableStateOf("") }
    var judulIndonesia by remember { mutableStateOf("") }
    var judulEnglish by remember { mutableStateOf("") }
    var pembimbing1 by remember { mutableStateOf("") }
    var pembimbing2 by remember { mutableStateOf("") }
    var penguji1 by remember { mutableStateOf("") }
    var penguji2 by remember { mutableStateOf("") }

    var isDraftSaved by remember { mutableStateOf(false) }

    val selectedFileNames = remember { mutableStateMapOf<String, String>() }
    val selectedFileUris = remember { mutableStateMapOf<String, Uri>() }

    val documents = remember {
        listOf(
            UploadFileItem(
                key = "bukti_setuju_revisi_kolokium",
                title = "Bukti Setuju Revisi Pasca Kolokium",
                description = "File PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "softfile_skripsi_word",
                title = "Softfile Skripsi (Word)",
                description = "Lengkap: cover, lampiran, daftar pustaka. Format .doc atau .docx",
                mimeTypes = listOf(
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            ),
            UploadFileItem(
                key = "skripsi_pdf_ttd",
                title = "Skripsi PDF (Bertanda Tangan)",
                description = "Wajib termasuk lembar pengesahan. Format PDF, maks. 10 MB",
                mimeTypes = listOf("application/pdf")
            ),
            UploadFileItem(
                key = "lembar_pengesahan_pdf",
                title = "Lembar Pengesahan (PDF)",
                description = "Lembar pengesahan terpisah yang sudah ditandatangani. Maks. 10 MB",
                mimeTypes = listOf("application/pdf")
            ),
            UploadFileItem(
                key = "bukti_loa_artikel",
                title = "Bukti LOA Artikel Ilmiah",
                description = "Capture form submit atau bukti konfirmasi artikel ilmiah"
            )
        )
    }

    val isValid =
        nama.isNotBlank() &&
                npm.isNotBlank() &&
                nomorHp.isNotBlank() &&
                judulIndonesia.isNotBlank() &&
                judulEnglish.isNotBlank() &&
                pembimbing1.isNotBlank() &&
                pembimbing2.isNotBlank() &&
                penguji1.isNotBlank() &&
                penguji2.isNotBlank() &&
                documents.all { selectedFileUris.containsKey(it.key) } &&
                !uploadState.isLoading

    LaunchedEffect(authState.name, authState.nim) {
        if (nama.isBlank()) nama = authState.name.orEmpty()
        if (npm.isBlank()) npm = authState.nim.orEmpty()
    }

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            Toast.makeText(
                context,
                "Pendaftaran yudisium berhasil dikirim ke TU",
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
                title = "Pendaftaran Yudisium",
                onBackClick = { navController.popBackStack() }
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
                    text = "Tahap Akhir Tugas Akhir. Lengkapi seluruh berkas final sebelum daftar yudisium."
                )
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.Edit,
                    title = "Data Diri"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Nama Lengkap",
                        placeholder = "Sesuai PUEBI. Contoh: Ring Karanuci",
                        value = nama,
                        onValueChange = { nama = it }
                    )

                    AppTextField(
                        label = "NPM",
                        placeholder = "NPM lengkap anda",
                        value = npm,
                        onValueChange = { npm = it }
                    )

                    AppTextField(
                        label = "Nomor Handphone",
                        placeholder = "Gunakan 62 sebagai pengganti 0. Contoh: 628123456789",
                        value = nomorHp,
                        onValueChange = { nomorHp = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.Edit,
                    title = "Judul Skripsi Final"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Judul Skripsi (Bahasa Indonesia)",
                        placeholder = "Tulis judul final dalam Bahasa Indonesia...",
                        value = judulIndonesia,
                        minLines = 3,
                        onValueChange = { judulIndonesia = it }
                    )

                    AppTextField(
                        label = "Judul Skripsi (English)",
                        placeholder = "Write the final title in English...",
                        value = judulEnglish,
                        minLines = 3,
                        onValueChange = { judulEnglish = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.Edit,
                    title = "Pembimbing & Penguji"
                )
            }

            item {
                FormCard {
                    AppDropdownField(
                        label = "Pembimbing 1",
                        placeholder = "-- Pilih pembimbing 1 --",
                        value = pembimbing1,
                        options = sampleLecturers(),
                        onSelect = { pembimbing1 = it }
                    )

                    AppDropdownField(
                        label = "Pembimbing 2",
                        placeholder = "-- Pilih pembimbing 2 --",
                        value = pembimbing2,
                        options = sampleLecturers(),
                        onSelect = { pembimbing2 = it }
                    )

                    AppDropdownField(
                        label = "Penguji 1",
                        placeholder = "-- Pilih penguji kolokium 1 --",
                        value = penguji1,
                        options = sampleExaminers(),
                        onSelect = { penguji1 = it }
                    )

                    AppDropdownField(
                        label = "Penguji 2",
                        placeholder = "-- Pilih penguji kolokium 2 --",
                        value = penguji2,
                        options = sampleExaminers(),
                        onSelect = { penguji2 = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Berkas Persetujuan & Revisi"
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
                InfoBox(
                    title = if (isDraftSaved) {
                        "Draft sudah disimpan"
                    } else {
                        "Sebelum mengirim — periksa ulang"
                    },
                    description = if (isDraftSaved) {
                        "Draft pendaftaran yudisium tersimpan sementara di halaman ini. Data belum dikirim ke TU."
                    } else {
                        "Judul Indonesia & English final, dosen pembimbing dan penguji, lembar pengesahan, serta bukti LOA artikel sudah sesuai."
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
                                "Draft yudisium berhasil disimpan",
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
                                stage = "yudisium",
                                studentName = nama.trim(),
                                nim = npm.trim(),
                                phone = nomorHp.trim(),
                                title = judulIndonesia.trim(),
                                titleEnglish = judulEnglish.trim(),
                                supervisor1 = pembimbing1.trim(),
                                supervisor2 = pembimbing2.trim(),
                                examiner1 = penguji1.trim(),
                                examiner2 = penguji2.trim(),
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
                                "Daftar Yudisium"
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun sampleLecturers(): List<String> {
    return listOf(
        "Dr. Dosen Pembimbing 1",
        "Dr. Dosen Pembimbing 2",
        "Dr. Dosen Pembimbing 3",
        "Dr. Dosen Pembimbing 4"
    )
}

private fun sampleExaminers(): List<String> {
    return listOf(
        "Dr. Dosen Penguji 1",
        "Dr. Dosen Penguji 2",
        "Dr. Dosen Penguji 3",
        "Dr. Dosen Penguji 4"
    )
}