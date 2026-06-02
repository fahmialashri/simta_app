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
import androidx.compose.material.icons.rounded.Edit
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
import com.project.navigation.Screen
import com.project.upload.UploadBerkasViewModel

@Composable
fun PendaftaranSeminarProposalFormScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf(authState.email.orEmpty()) }
    var namaLengkap by remember { mutableStateOf(authState.name.orEmpty()) }
    var npm by remember { mutableStateOf(authState.nim.orEmpty()) }
    var judulProposal by remember { mutableStateOf("") }
    var nomorHp by remember { mutableStateOf("") }
    var pembimbing by remember { mutableStateOf("") }

    val selectedFileNames = remember { mutableStateMapOf<String, String>() }
    val selectedFileUris = remember { mutableStateMapOf<String, Uri>() }

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
                description = "Telah ditandatangani dosen wali / pembimbing"
            ),
            UploadFileItem(
                key = "file_proposal_skripsi",
                title = "File Proposal Skripsi",
                description = "Cover sampai daftar pustaka, format NASKAH_Proposal_npm",
                mimeTypes = listOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            ),
            UploadFileItem(
                key = "bukti_persetujuan_pembimbing",
                title = "Bukti Persetujuan Pembimbing",
                description = "Screenshot chat WA, email, atau bukti persetujuan"
            ),
            UploadFileItem(
                key = "bebas_plagiarisme_bab_1",
                title = "Bukti Plagiarisme BAB 1",
                description = "Upload bukti hasil plagiarisme BAB 1"
            ),
            UploadFileItem(
                key = "bebas_plagiarisme_bab_2",
                title = "Bukti Plagiarisme BAB 2",
                description = "Upload bukti hasil plagiarisme BAB 2"
            ),
            UploadFileItem(
                key = "bebas_plagiarisme_bab_3",
                title = "Bukti Plagiarisme BAB 3",
                description = "Upload bukti hasil plagiarisme BAB 3"
            )
        )
    }

    val isValid =
        email.isNotBlank() &&
                namaLengkap.isNotBlank() &&
                npm.isNotBlank() &&
                judulProposal.isNotBlank() &&
                nomorHp.isNotBlank() &&
                pembimbing.isNotBlank() &&
                documents.all { selectedFileUris.containsKey(it.key) } &&
                !uploadState.isLoading

    LaunchedEffect(authState.email, authState.name, authState.nim) {
        if (email.isBlank()) email = authState.email.orEmpty()
        if (namaLengkap.isBlank()) namaLengkap = authState.name.orEmpty()
        if (npm.isBlank()) npm = authState.nim.orEmpty()
    }

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            Toast.makeText(
                context,
                "Pendaftaran seminar proposal berhasil dikirim ke TU",
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
                title = "Pendaftaran Seminar Proposal",
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
                    text = "Lengkapi semua data dan unggah berkas yang diminta sebelum daftar."
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
                        label = "Email",
                        value = email,
                        onValueChange = { email = it }
                    )

                    AppTextField(
                        label = "Nama Lengkap",
                        placeholder = "Sesuai KTP. Contoh: Zerta Dwi Putra",
                        value = namaLengkap,
                        onValueChange = { namaLengkap = it }
                    )

                    AppTextField(
                        label = "NPM",
                        placeholder = "NPM lengkap anda",
                        value = npm,
                        onValueChange = { npm = it }
                    )

                    AppTextField(
                        label = "Judul Proposal Penelitian",
                        placeholder = "Tulis judul proposal penelitian...",
                        value = judulProposal,
                        minLines = 3,
                        onValueChange = { judulProposal = it }
                    )

                    AppTextField(
                        label = "Nomor Handphone",
                        placeholder = "Gunakan 62 sebagai pengganti 0. Contoh: 628123456789",
                        value = nomorHp,
                        onValueChange = { nomorHp = it }
                    )

                    AppDropdownField(
                        label = "Pembimbing Proposal",
                        placeholder = "-- Pilih dosen pembimbing --",
                        value = pembimbing,
                        options = sampleLecturersSempro(),
                        onSelect = { pembimbing = it }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Rounded.UploadFile,
                    title = "Berkas Lampiran"
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
                            stage = "seminar_proposal",
                            studentName = namaLengkap.trim(),
                            nim = npm.trim(),
                            phone = nomorHp.trim(),
                            title = judulProposal.trim(),
                            titleEnglish = null,
                            supervisor1 = pembimbing.trim(),
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
                            "Kirim Pendaftaran"
                        }
                    )
                }
            }
        }
    }
}

private fun sampleLecturersSempro(): List<String> {
    return listOf(
        "Dr. Dosen Pembimbing 1",
        "Dr. Dosen Pembimbing 2",
        "Dr. Dosen Pembimbing 3",
        "Dr. Dosen Pembimbing 4"
    )
}