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

@Composable
fun UploadRevisiSeminarProposalScreen(
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
    var judulProposal by remember { mutableStateOf("") }
    var pembimbing1 by remember { mutableStateOf("") }
    var pengujiProposal by remember { mutableStateOf("") }

    val selectedFileNames = remember { mutableStateMapOf<String, String>() }
    val selectedFileUris = remember { mutableStateMapOf<String, Uri>() }

    val lecturerOptions = remember(lecturerState.lecturers) {
        lecturerState.lecturers
            .filter { it.isActive }
            .map { it.fullName }
            .distinct()
    }

    val pengujiOptions = remember(lecturerOptions, pembimbing1) {
        lecturerOptions.filter { it != pembimbing1 }
    }

    val pembimbingOptions = remember(lecturerOptions, pengujiProposal) {
        lecturerOptions.filter { it != pengujiProposal }
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
                key = "proposal_revisi",
                title = "File Proposal Revisi",
                description = "Format PDF atau Word (.pdf / .doc / .docx), maks. 10 MB",
                mimeTypes = listOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            ),
            UploadFileItem(
                key = "lembar_revisi_sempro",
                title = "Lembar Revisi Seminar Proposal",
                description = "Upload lembar revisi yang sudah ditandatangani"
            )
        )
    }

    val isValid =
        nama.isNotBlank() &&
                npm.isNotBlank() &&
                nomorHp.isNotBlank() &&
                judulProposal.isNotBlank() &&
                pembimbing1.isNotBlank() &&
                pengujiProposal.isNotBlank() &&
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

        if (pengujiProposal.isNotBlank() && pengujiProposal !in lecturerOptions) {
            pengujiProposal = ""
        }
    }

    LaunchedEffect(uploadState.isSuccess) {
        if (uploadState.isSuccess) {
            Toast.makeText(
                context,
                "Upload revisi seminar proposal berhasil dikirim ke TU",
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
                title = "Revisi Seminar Proposal",
                onBackClick = {
                    navController.popBackStack()
                }
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
                    text = "Upload revisi seminar proposal. Dosen penguji proposal yang dipilih akan otomatis tercatat sebagai pembimbing 2."
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
                    title = "Data Revisi Proposal"
                )
            }

            item {
                FormCard {
                    AppTextField(
                        label = "Judul Proposal Skripsi",
                        placeholder = "Tulis judul proposal skripsi...",
                        value = judulProposal,
                        minLines = 3,
                        onValueChange = { judulProposal = it }
                    )

                    AppDropdownField(
                        label = "Dosen Pembimbing 1",
                        placeholder = "Pilih dosen pembimbing 1",
                        value = pembimbing1,
                        options = pembimbingOptions,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { pembimbing1 = it }
                    )

                    AppDropdownField(
                        label = "Dosen Penguji Proposal",
                        placeholder = "Pilih dosen penguji proposal",
                        value = pengujiProposal,
                        options = pengujiOptions,
                        enabled = !lecturerState.isLoading,
                        emptyText = dropdownEmptyText,
                        onSelect = { pengujiProposal = it }
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
                Button(
                    onClick = {
                        uploadBerkasViewModel.submitRegistration(
                            context = context,
                            userId = authState.userId,
                            stage = "revisi_seminar_proposal",
                            studentName = nama.trim(),
                            nim = npm.trim(),
                            phone = nomorHp.trim(),
                            title = judulProposal.trim(),
                            titleEnglish = null,
                            supervisor1 = pembimbing1.trim(),
                            supervisor2 = pengujiProposal.trim(),
                            examiner1 = pengujiProposal.trim(),
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
                            "Upload Revisi Seminar Proposal"
                        }
                    )
                }
            }
        }
    }
}