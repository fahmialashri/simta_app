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
import com.project.component.AppTextField
import com.project.component.FormCard
import com.project.component.FormTopBar
import com.project.component.MahasiswaBottomNavItem
import com.project.component.MahasiswaBottomNavigation
import com.project.component.SectionTitle
import com.project.component.UploadFileCard
import com.project.component.UploadFileItem
import com.project.component.UploadPageAlert
import com.project.core.SimtaYellow
import com.project.lecturer.LecturerViewModel
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Suppress("UNUSED_PARAMETER")
@Composable
fun PendaftaranYudisiumFormScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel,
    lecturerViewModel: LecturerViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    var nama by remember { mutableStateOf(authState.name.orEmpty()) }
    var npm by remember { mutableStateOf(authState.nim.orEmpty()) }
    var nomorHp by remember { mutableStateOf("") }
    var judulSkripsi by remember { mutableStateOf("") }
    var judulInggris by remember { mutableStateOf("") }

    val selectedFileNames = remember { mutableStateMapOf<String, String>() }
    val selectedFileUris = remember { mutableStateMapOf<String, Uri>() }

    val documents = remember {
        listOf(
            UploadFileItem(
                key = "ktp",
                title = "KTP",
                description = "Format PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "kartu_keluarga",
                title = "Kartu Keluarga",
                description = "Format PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "ijazah_terakhir",
                title = "Ijazah Terakhir",
                description = "Format PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "transkrip_nilai",
                title = "Transkrip Nilai",
                description = "Format PDF, maks. 10 MB",
                mimeTypes = listOf("application/pdf")
            ),
            UploadFileItem(
                key = "bukti_bebas_perpustakaan",
                title = "Bukti Bebas Perpustakaan",
                description = "Format PDF atau gambar, maks. 10 MB"
            ),
            UploadFileItem(
                key = "bukti_revisi_skripsi",
                title = "Bukti Revisi Skripsi",
                description = "Format PDF atau gambar, maks. 10 MB"
            )
        )
    }

    val isValid =
        nama.isNotBlank() &&
                npm.isNotBlank() &&
                nomorHp.isNotBlank() &&
                judulSkripsi.isNotBlank() &&
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
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            FormTopBar(
                title = "Pendaftaran Yudisium",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            MahasiswaBottomNavigation(
                navController = navController,
                selectedItem = MahasiswaBottomNavItem.PENGAJUAN
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
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                UploadPageAlert(
                    text = "Form pendaftaran yudisium. Pastikan seluruh persyaratan kelulusan sudah lengkap."
                )
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Data Mahasiswa"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Nama Lengkap",
                        placeholder = "Nama lengkap mahasiswa",
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
                        placeholder = "Contoh: 628123456789",
                        value = nomorHp,
                        onValueChange = { nomorHp = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Data Skripsi"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Judul Skripsi",
                        placeholder = "Tulis judul skripsi...",
                        value = judulSkripsi,
                        minLines = 3,
                        onValueChange = { judulSkripsi = it }
                    )

                    AppTextField(
                        label = "Judul Skripsi Bahasa Inggris",
                        placeholder = "Opsional",
                        value = judulInggris,
                        minLines = 2,
                        onValueChange = { judulInggris = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Berkas Yudisium"
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
                            stage = "yudisium",
                            studentName = nama.trim(),
                            nim = npm.trim(),
                            phone = nomorHp.trim(),
                            title = judulSkripsi.trim(),
                            titleEnglish = judulInggris.trim().ifBlank { null },
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
                        text = if (uploadState.isLoading) "Mengirim..." else "Daftar Yudisium"
                    )
                }
            }
        }
    }
}