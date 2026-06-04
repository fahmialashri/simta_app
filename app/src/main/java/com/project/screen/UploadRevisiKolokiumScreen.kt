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
import com.project.lecturer.LecturerViewModel
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Composable
fun UploadRevisiKolokiumScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel,
    lecturerViewModel: LecturerViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()
    val lecturerState by lecturerViewModel.uiState.collectAsState()

    var nama by remember { mutableStateOf(authState.name.orEmpty()) }
    var npm by remember { mutableStateOf(authState.nim.orEmpty()) }
    var pembimbing1 by remember { mutableStateOf("") }
    var pembimbing2 by remember { mutableStateOf("") }
    var penguji1 by remember { mutableStateOf("") }
    var penguji2 by remember { mutableStateOf("") }
    var tanggalKolokium by remember { mutableStateOf("") }
    var judulIndonesia by remember { mutableStateOf("") }
    var judulEnglish by remember { mutableStateOf("") }
    var isDraftSaved by remember { mutableStateOf(false) }

    val selectedFileNames = remember { mutableStateMapOf<String, String>() }
    val selectedFileUris = remember { mutableStateMapOf<String, Uri>() }

    val lecturerOptions = remember(lecturerState.lecturers) {
        lecturerState.lecturers
            .filter { it.isActive }
            .map { it.fullName }
            .distinct()
    }

    val pembimbing1Options = remember(lecturerOptions, pembimbing2, penguji1, penguji2) {
        lecturerOptions.filter {
            it != pembimbing2 &&
                    it != penguji1 &&
                    it != penguji2
        }
    }

    val pembimbing2Options = remember(lecturerOptions, pembimbing1, penguji1, penguji2) {
        lecturerOptions.filter {
            it != pembimbing1 &&
                    it != penguji1 &&
                    it != penguji2
        }
    }

    val penguji1Options = remember(lecturerOptions, pembimbing1, pembimbing2, penguji2) {
        lecturerOptions.filter {
            it != pembimbing1 &&
                    it != pembimbing2 &&
                    it != penguji2
        }
    }

    val penguji2Options = remember(lecturerOptions, pembimbing1, pembimbing2, penguji1) {
        lecturerOptions.filter {
            it != pembimbing1 &&
                    it != pembimbing2 &&
                    it != penguji1
        }
    }

    val dropdownEmptyText = if (authState.departmentId == null) {
        "Program studi belum ditemukan"
    } else if (lecturerState.isLoading) {
        "Memuat data dosen..."
    } else {
        "Data dosen belum tersedia"
    }

    val documents = remember {
        listOf(
            UploadFileItem(
                key = "soft_file_skripsi_revisi",
                title = "Soft File Skripsi Revisi",
                description = "Format Word atau PDF (.pdf / .docx), maks. 10 MB",
                mimeTypes = listOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            ),
            UploadFileItem(
                key = "bukti_persetujuan_pembimbing_1",
                title = "Bukti Persetujuan Pembimbing 1",
                description = "Screenshot persetujuan pembimbing 1"
            ),
            UploadFileItem(
                key = "bukti_persetujuan_pembimbing_2",
                title = "Bukti Persetujuan Pembimbing 2",
                description = "Screenshot persetujuan pembimbing 2"
            ),
            UploadFileItem(
                key = "bukti_persetujuan_penguji_1",
                title = "Bukti Persetujuan Penguji 1",
                description = "Screenshot persetujuan penguji 1"
            ),
            UploadFileItem(
                key = "bukti_persetujuan_penguji_2",
                title = "Bukti Persetujuan Penguji 2",
                description = "Screenshot persetujuan penguji 2"
            )
        )
    }

    val isValid =
        nama.isNotBlank() &&
                npm.isNotBlank() &&
                pembimbing1.isNotBlank() &&
                pembimbing2.isNotBlank() &&
                penguji1.isNotBlank() &&
                penguji2.isNotBlank() &&
                tanggalKolokium.isNotBlank() &&
                judulIndonesia.isNotBlank() &&
                judulEnglish.isNotBlank() &&
                documents.all { selectedFileUris.containsKey(it.key) } &&
                !uploadState.isLoading

    LaunchedEffect(authState.departmentId) {
        lecturerViewModel.loadLecturersByDepartment(authState.departmentId)
    }

    LaunchedEffect(authState.name, authState.nim) {
        if (nama.isBlank()) {
            nama = authState.name.orEmpty()
        }

        if (npm.isBlank()) {
            npm = authState.nim.orEmpty()
        }
    }

    LaunchedEffect(lecturerOptions) {
        if (pembimbing1.isNotBlank() && pembimbing1 !in lecturerOptions) {
            pembimbing1 = ""
        }

        if (pembimbing2.isNotBlank() && pembimbing2 !in lecturerOptions) {
            pembimbing2 = ""
        }

        if (penguji1.isNotBlank() && penguji1 !in lecturerOptions) {
            penguji1 = ""
        }

        if (penguji2.isNotBlank() && penguji2 !in lecturerOptions) {
            penguji2 = ""
        }
    }

    LaunchedEffect(pembimbing1, pembimbing2, penguji1, penguji2) {
        if (pembimbing1.isNotBlank() && pembimbing1 == pembimbing2) {
            pembimbing2 = ""
        }

        if (penguji1.isNotBlank() && penguji1 in listOf(pembimbing1, pembimbing2)) {
            penguji1 = ""
        }

        if (penguji2.isNotBlank() && penguji2 in listOf(pembimbing1, pembimbing2, penguji1)) {
            penguji2 = ""
        }
    }

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            Toast.makeText(
                context,
                "Revisi kolokium berhasil dikirim ke TU",
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
                title = "Upload Revisi Kolokium",
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
                    text = "Upload revisi kolokium setelah sidang. Pastikan revisi sudah disetujui pembimbing dan penguji."
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
                        label = "Pembimbing 1",
                        placeholder = "Pilih dosen pembimbing 1",
                        value = pembimbing1,
                        options = pembimbing1Options,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { pembimbing1 = it }
                    )

                    AppDropdownField(
                        label = "Pembimbing 2",
                        placeholder = "Pilih dosen pembimbing 2",
                        value = pembimbing2,
                        options = pembimbing2Options,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { pembimbing2 = it }
                    )

                    AppDropdownField(
                        label = "Penguji 1",
                        placeholder = "Pilih dosen penguji 1",
                        value = penguji1,
                        options = penguji1Options,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { penguji1 = it }
                    )

                    AppDropdownField(
                        label = "Penguji 2",
                        placeholder = "Pilih dosen penguji 2",
                        value = penguji2,
                        options = penguji2Options,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { penguji2 = it }
                    )

                    AppTextField(
                        label = "Tanggal Pelaksanaan Kolokium",
                        placeholder = "Contoh: Rabu, 30 Oktober 2019",
                        value = tanggalKolokium,
                        onValueChange = { tanggalKolokium = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Berkas Revisi Kolokium"
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
                        label = "Judul Skripsi Bahasa Indonesia Setelah Revisi",
                        placeholder = "Tulis judul bahasa Indonesia yang sudah direvisi...",
                        value = judulIndonesia,
                        minLines = 3,
                        onValueChange = { judulIndonesia = it }
                    )

                    AppTextField(
                        label = "Judul Skripsi Bahasa Inggris Setelah Revisi",
                        placeholder = "Tulis judul bahasa Inggris yang sudah direvisi...",
                        value = judulEnglish,
                        minLines = 3,
                        onValueChange = { judulEnglish = it }
                    )
                }
            }

            item {
                InfoBox(
                    title = if (isDraftSaved) "Draft sudah disimpan" else "Catatan penting",
                    description = if (isDraftSaved) {
                        "Draft revisi kolokium tersimpan sementara di halaman ini."
                    } else {
                        "Pastikan seluruh revisi sudah disetujui oleh pembimbing dan penguji sebelum dikirim."
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
                                "Draft revisi kolokium berhasil disimpan",
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
                                stage = "revisi_kolokium",
                                studentName = nama.trim(),
                                nim = npm.trim(),
                                phone = null,
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
                                "Kirim Revisi"
                            }
                        )
                    }
                }
            }
        }
    }
}