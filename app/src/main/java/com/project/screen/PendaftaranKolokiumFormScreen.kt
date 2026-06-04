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
import com.project.component.SectionTitle
import com.project.component.UploadFileCard
import com.project.component.UploadFileItem
import com.project.component.UploadPageAlert
import com.project.core.SimtaYellow
import com.project.lecturer.LecturerViewModel
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Composable
fun PendaftaranKolokiumFormScreen(
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
    var nomorHp by remember { mutableStateOf("") }
    var pembimbing1 by remember { mutableStateOf("") }
    var pembimbing2 by remember { mutableStateOf("") }
    var judulIndonesia by remember { mutableStateOf("") }
    var judulEnglish by remember { mutableStateOf("") }

    val selectedFileNames = remember { mutableStateMapOf<String, String>() }
    val selectedFileUris = remember { mutableStateMapOf<String, Uri>() }

    val lecturerOptions = remember(lecturerState.lecturers) {
        lecturerState.lecturers
            .filter { it.isActive }
            .map { it.fullName }
            .distinct()
    }

    val pembimbing1Options = remember(lecturerOptions, pembimbing2) {
        lecturerOptions.filter { it != pembimbing2 }
    }

    val pembimbing2Options = remember(lecturerOptions, pembimbing1) {
        lecturerOptions.filter { it != pembimbing1 }
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
                key = "bukti_lunas_ukt",
                title = "Bukti Lunas Administrasi (UKT)",
                description = "Format PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "krs_pengambilan_skripsi",
                title = "KRS Pengambilan Skripsi",
                description = "Telah ditandatangani dosen wali. Format PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "transkrip_sementara",
                title = "Transkrip Sementara (ECAMPUS)",
                description = "Unduh dari ECAMPUS. Format PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "file_skripsi_lengkap",
                title = "File Skripsi Lengkap",
                description = "Cover sampai lampiran, format NAMA_SKRIPSI.pdf",
                mimeTypes = listOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            ),
            UploadFileItem(
                key = "bukti_persetujuan_pembimbing",
                title = "Bukti Persetujuan Pembimbing 1 & 2",
                description = "Screenshot chat WA, email, atau bukti persetujuan. Maks. 10 MB"
            ),
            UploadFileItem(
                key = "bukti_plagiarisme",
                title = "Bukti Cek Plagiarisme BAB 1-5",
                description = "Maksimal plagiarisme 30%. Format gambar atau PDF"
            )
        )
    }

    val isValid =
        nama.isNotBlank() &&
                npm.isNotBlank() &&
                nomorHp.isNotBlank() &&
                pembimbing1.isNotBlank() &&
                pembimbing2.isNotBlank() &&
                judulIndonesia.isNotBlank() &&
                judulEnglish.isNotBlank() &&
                documents.all { selectedFileUris.containsKey(it.key) } &&
                !uploadState.isLoading

    LaunchedEffect(authState.departmentId) {
        lecturerViewModel.loadLecturersByDepartment(authState.departmentId)
    }

    LaunchedEffect(authState.name, authState.nim) {
        if (nama.isBlank()) nama = authState.name.orEmpty()
        if (npm.isBlank()) npm = authState.nim.orEmpty()
    }

    LaunchedEffect(lecturerOptions) {
        if (pembimbing1.isNotBlank() && pembimbing1 !in lecturerOptions) {
            pembimbing1 = ""
        }

        if (pembimbing2.isNotBlank() && pembimbing2 !in lecturerOptions) {
            pembimbing2 = ""
        }
    }

    LaunchedEffect(pembimbing1, pembimbing2) {
        if (pembimbing1.isNotBlank() && pembimbing1 == pembimbing2) {
            pembimbing2 = ""
        }
    }

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            Toast.makeText(
                context,
                "Pendaftaran kolokium berhasil dikirim ke TU",
                Toast.LENGTH_SHORT
            ).show()

            uploadBerkasViewModel.resetState()

            navController.navigate(Screen.MahasiswaDashboard.route) {
                popUpTo(Screen.Pengajuan.route) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(uploadState.errorMessage) {
        uploadState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            FormTopBar(
                title = "Pendaftaran Sidang Kolokium",
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
                    text = "Form pendaftaran sidang kolokium. Pastikan semua berkas sudah lengkap dan telah disetujui kedua dosen pembimbing."
                )
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Data Diri"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Nama Lengkap",
                        placeholder = "Sesuai PUEBI. Contoh: Ade Mulyadi",
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
                    icon = Icons.Rounded.UploadFile,
                    title = "Judul Skripsi"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Judul Skripsi (Bahasa Indonesia)",
                        placeholder = "Tulis judul dalam Bahasa Indonesia...",
                        value = judulIndonesia,
                        minLines = 3,
                        onValueChange = { judulIndonesia = it }
                    )

                    AppTextField(
                        label = "Judul Skripsi (English)",
                        placeholder = "Write the title in English...",
                        value = judulEnglish,
                        minLines = 3,
                        onValueChange = { judulEnglish = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Dosen Pembimbing"
                )
            }

            item {
                FormCard {
                    AppDropdownField(
                        label = "Pembimbing Skripsi 1",
                        placeholder = "Pilih pembimbing 1",
                        value = pembimbing1,
                        options = pembimbing1Options,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { pembimbing1 = it }
                    )

                    AppDropdownField(
                        label = "Pembimbing Skripsi 2",
                        placeholder = "Pilih pembimbing 2",
                        value = pembimbing2,
                        options = pembimbing2Options,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { pembimbing2 = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Berkas Administrasi & Skripsi"
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
                            stage = "kolokium",
                            studentName = nama.trim(),
                            nim = npm.trim(),
                            phone = nomorHp.trim(),
                            title = judulIndonesia.trim(),
                            titleEnglish = judulEnglish.trim(),
                            supervisor1 = pembimbing1.trim(),
                            supervisor2 = pembimbing2.trim(),
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
                    Text(text = if (uploadState.isLoading) "Mengirim..." else "Daftar Sidang")
                }
            }
        }
    }
}