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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.core.SimtaRed
import com.project.data.model.Lecturer
import com.project.lecturer.LecturerViewModel
import com.project.upload.UploadBerkasViewModel

@Composable
fun TuPlottingPengujiScreen(
    navController: NavHostController,
    stageId: String,
    uploadBerkasViewModel: UploadBerkasViewModel,
    lecturerViewModel: LecturerViewModel
) {
    val context = LocalContext.current

    val uploadState by uploadBerkasViewModel.uiState.collectAsState()
    val lecturerState by lecturerViewModel.uiState.collectAsState()

    val submission = uploadState.selectedSubmission

    var examiner1 by remember { mutableStateOf<Lecturer?>(null) }
    var examiner2 by remember { mutableStateOf<Lecturer?>(null) }

    val requiredExaminerCount = remember(submission?.stage) {
        getRequiredExaminerCount(submission?.stage)
    }

    val departmentName = uploadState.selectedSubmissionDepartmentId.toDepartmentName()

    val lecturerOptions = remember(lecturerState.lecturers) {
        lecturerState.lecturers
            .filter { it.isActive }
            .distinctBy { it.id }
    }

    val examiner1Options = remember(lecturerOptions, examiner2) {
        lecturerOptions.filter { it.id != examiner2?.id }
    }

    val examiner2Options = remember(lecturerOptions, examiner1) {
        lecturerOptions.filter { it.id != examiner1?.id }
    }

    val hasNewSubmissionNotification = remember(submission) {
        submission != null &&
                submission.status == "disetujui_tu" &&
                submission.examiner1.isNullOrBlank()
    }

    val canSave = when (requiredExaminerCount) {
        1 -> examiner1 != null && !uploadState.isLoading
        else -> examiner1 != null && examiner2 != null && !uploadState.isLoading
    }

    LaunchedEffect(stageId) {
        uploadBerkasViewModel.loadSubmissionForPlotting(stageId)
    }

    LaunchedEffect(uploadState.selectedSubmissionDepartmentId) {
        lecturerViewModel.loadLecturersByDepartment(uploadState.selectedSubmissionDepartmentId)
    }

    LaunchedEffect(submission?.examiner1, submission?.examiner2, lecturerOptions) {
        if (lecturerOptions.isNotEmpty()) {
            if (examiner1 == null && !submission?.examiner1.isNullOrBlank()) {
                examiner1 = lecturerOptions.firstOrNull {
                    it.fullName == submission?.examiner1 || it.name == submission?.examiner1
                }
            }

            if (examiner2 == null && !submission?.examiner2.isNullOrBlank()) {
                examiner2 = lecturerOptions.firstOrNull {
                    it.fullName == submission?.examiner2 || it.name == submission?.examiner2
                }
            }
        }
    }

    LaunchedEffect(uploadState.errorMessage) {
        uploadState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            uploadBerkasViewModel.clearError()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            Button(
                onClick = {
                    uploadBerkasViewModel.saveExaminerPlotting(
                        submissionId = stageId,
                        examiner1 = examiner1?.fullName.orEmpty(),
                        examiner2 = if (requiredExaminerCount == 1) {
                            null
                        } else {
                            examiner2?.fullName
                        },
                        reloadStage = submission?.stage,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Plotting dosen penguji berhasil disimpan",
                                Toast.LENGTH_SHORT
                            ).show()

                            navController.popBackStack()
                        }
                    )
                },
                enabled = canSave,
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
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Simpan Plotting",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
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
                        text = "Plotting Dosen Penguji",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Penguji difilter sesuai jurusan mahasiswa",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uploadState.isLoading && submission == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SimtaRed)
                }
            } else {
                if (hasNewSubmissionNotification) {
                    NewSubmissionNotificationCard(
                        studentName = submission?.studentName ?: "-",
                        stage = submission?.stage.orEmpty()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                SubmissionInfoCard(
                    studentName = submission?.studentName ?: "-",
                    nim = submission?.nim ?: "-",
                    stage = submission?.stage ?: "-",
                    title = submission?.title ?: "-",
                    requiredExaminerCount = requiredExaminerCount,
                    departmentName = departmentName
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoPlottingCard(
                    requiredExaminerCount = requiredExaminerCount,
                    stage = submission?.stage.orEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExaminerDropdownCard(
                    title = "Penguji 1",
                    selectedLecturer = examiner1,
                    lecturers = examiner1Options,
                    isLoading = lecturerState.isLoading,
                    emptyText = if (lecturerState.isLoading) {
                        "Memuat data dosen..."
                    } else {
                        "Data dosen jurusan $departmentName belum tersedia"
                    },
                    onSelect = { examiner1 = it }
                )

                if (requiredExaminerCount >= 2) {
                    Spacer(modifier = Modifier.height(14.dp))

                    ExaminerDropdownCard(
                        title = "Penguji 2",
                        selectedLecturer = examiner2,
                        lecturers = examiner2Options,
                        isLoading = lecturerState.isLoading,
                        emptyText = if (lecturerState.isLoading) {
                            "Memuat data dosen..."
                        } else {
                            "Data dosen jurusan $departmentName belum tersedia"
                        },
                        onSelect = { examiner2 = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (requiredExaminerCount == 1) {
                    Text(
                        text = "Aturan: seminar proposal hanya membutuhkan 1 dosen penguji.",
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                } else {
                    Text(
                        text = "Aturan: tahap ini membutuhkan 2 dosen penguji dan tidak boleh memilih dosen yang sama.",
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

private fun getRequiredExaminerCount(stage: String?): Int {
    return when (stage) {
        "seminar_proposal",
        "revisi_seminar_proposal" -> 1

        "kolokium",
        "pendaftaran_kolokium",
        "revisi_kolokium",
        "yudisium" -> 2

        else -> 1
    }
}

@Composable
private fun NewSubmissionNotificationCard(
    studentName: String,
    stage: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = SimtaRed,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pengajuan Baru Masuk",
                    color = SimtaRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$studentName sudah disetujui TU untuk tahap ${stage.toReadableStageName()} dan belum diplotting dosen penguji.",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun SubmissionInfoCard(
    studentName: String,
    nim: String,
    stage: String,
    title: String,
    requiredExaminerCount: Int,
    departmentName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Data Pengajuan",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            InfoText("Mahasiswa", studentName)
            InfoText("NIM", nim)
            InfoText("Tahap", stage.toReadableStageName())
            InfoText("Jurusan", departmentName)
            InfoText("Jumlah Penguji", "$requiredExaminerCount orang")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Judul",
                color = Color.DarkGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = title,
                color = Color.Black,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun InfoText(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = Color.DarkGray,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.38f)
        )

        Text(
            text = value,
            color = Color.Black,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.62f)
        )
    }
}

@Composable
private fun InfoPlottingCard(
    requiredExaminerCount: Int,
    stage: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = SimtaRed
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Aturan Plotting",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (requiredExaminerCount == 1) {
                    "${stage.toReadableStageName()} hanya membutuhkan 1 dosen penguji."
                } else {
                    "${stage.toReadableStageName()} membutuhkan 2 dosen penguji."
                },
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExaminerDropdownCard(
    title: String,
    selectedLecturer: Lecturer?,
    lecturers: List<Lecturer>,
    isLoading: Boolean,
    emptyText: String,
    onSelect: (Lecturer) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    if (!isLoading && lecturers.isNotEmpty()) {
                        expanded = !expanded
                    }
                }
            ) {
                OutlinedTextField(
                    value = selectedLecturer?.fullName.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            text = if (lecturers.isEmpty()) emptyText else "Pilih Dosen"
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    lecturers.forEach { lecturer ->
                        DropdownMenuItem(
                            text = {
                                Text(lecturer.fullName)
                            },
                            onClick = {
                                onSelect(lecturer)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun Long?.toDepartmentName(): String {
    return when (this) {
        1L -> "Informatika"
        2L -> "Sistem Informasi"
        else -> "Program Studi Tidak Diketahui"
    }
}

private fun String.toReadableStageName(): String {
    return when (this) {
        "seminar_proposal" -> "Seminar Proposal"
        "revisi_seminar_proposal" -> "Revisi Seminar Proposal"
        "kolokium" -> "Kolokium"
        "pendaftaran_kolokium" -> "Kolokium"
        "revisi_kolokium" -> "Revisi Kolokium"
        "yudisium" -> "Yudisium"
        else -> replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}