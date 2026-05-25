package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.auth.AuthViewModel
import com.project.component.SimtaButton
import com.project.core.SimtaGreen
import com.project.core.SimtaRed
import com.project.data.model.Lecturer
import com.project.lecturer.LecturerViewModel
import com.project.supervisor.SupervisorRequestViewModel

@Composable
fun LecturerDetailScreen(
    lecturerId: Long,
    lecturerViewModel: LecturerViewModel,
    supervisorRequestViewModel: SupervisorRequestViewModel,
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val lecturerState by lecturerViewModel.uiState.collectAsState()
    val requestState by supervisorRequestViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Logic hitung kata untuk Judul
    val wordCount = if (title.isBlank()) 0 else title.trim().split("\\s+".toRegex()).size
    val isTitleValid = wordCount <= 14

    LaunchedEffect(Unit) {
        if (lecturerState.lecturers.isEmpty()) {
            lecturerViewModel.loadLecturers()
        }
        supervisorRequestViewModel.resetState()
    }

    LaunchedEffect(requestState.isSuccess) {
        if (requestState.isSuccess) {
            showSuccessDialog = true
        }
    }

    val lecturer: Lecturer? = lecturerState.lecturers.find { it.id == lecturerId }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            shape = RoundedCornerShape(24.dp),
            icon = { Icon(Icons.Default.CheckCircle, null, tint = SimtaGreen, modifier = Modifier.size(48.dp)) },
            title = { Text("Pengajuan Berhasil", fontWeight = FontWeight.Bold) },
            text = { Text("Permohonan dosen pembimbing kamu sudah terkirim. Tunggu konfirmasi dari dosen ya!", textAlign = TextAlign.Center) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        supervisorRequestViewModel.resetState()
                        onSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SimtaRed),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Mantap!", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header Background Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SimtaRed, SimtaRed.copy(alpha = 0.8f))
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 20.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Profil Dosen",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                lecturerState.isLoading -> {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                lecturer == null -> {
                    Text("Dosen tidak ditemukan.", color = Color.White, modifier = Modifier.padding(20.dp))
                }
                else -> {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                        // 1. INFO CARD DOSEN
                        LecturerInfoCardModern(lecturer = lecturer)

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. FORM CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Form Pengajuan Skripsi",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Judul Skripsi + Validasi Kata
                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("Judul Skripsi") },
                                    placeholder = { Text("Contoh: Implementasi AI pada...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = !isTitleValid,
                                    supportingText = {
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(if (isTitleValid) "Maksimal 14 kata" else "Judul terlalu panjang!")
                                            Text("$wordCount / 14")
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SimtaRed, focusedLabelColor = SimtaRed),
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Topik
                                OutlinedTextField(
                                    value = topic,
                                    onValueChange = { topic = it },
                                    label = { Text("Bidang Penelitian") },
                                    placeholder = { Text("Contoh: Machine Learning / UI UX") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SimtaRed, focusedLabelColor = SimtaRed),
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Pesan
                                OutlinedTextField(
                                    value = message,
                                    onValueChange = { message = it },
                                    label = { Text("Pesan Tambahan (Opsional)") },
                                    modifier = Modifier.fillMaxWidth().height(120.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SimtaRed, focusedLabelColor = SimtaRed),
                                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Tombol Submit
                                SimtaButton(
                                    text = if (requestState.isLoading) "Mengirim..." else "Kirim Pengajuan",
                                    enabled = !requestState.isLoading && lecturer.isAvailable && isTitleValid && title.isNotBlank() && topic.isNotBlank()
                                ) {
                                    supervisorRequestViewModel.submitRequest(
                                        studentId = authState.userId,
                                        lecturerId = lecturer.id,
                                        title = title.trim(),
                                        topic = topic.trim(),
                                        message = message.trim().ifBlank { null }
                                    )
                                }

                                if (!lecturer.isAvailable) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        color = SimtaRed.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Info, null, tint = SimtaRed, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Kuota dosen penuh. Coba pilih dosen lain.", color = SimtaRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LecturerInfoCardModern(lecturer: Lecturer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFF1F3F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = lecturer.fullName, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                    Surface(
                        color = SimtaRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = lecturer.expertise ?: "General",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = SimtaRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color(0xFFF1F3F4), thickness = 1.dp)
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoItem(label = "Kuota Max", value = "${lecturer.quota}")
                InfoItem(label = "Bimbingan", value = "${lecturer.currentStudents}")
                InfoItem(
                    label = "Sisa Kuota",
                    value = "${lecturer.remainingQuota}",
                    valueColor = if (lecturer.isAvailable) SimtaGreen else SimtaRed
                )
            }
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String, valueColor: Color = Color.Black) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 16.sp, color = valueColor, fontWeight = FontWeight.ExtraBold)
    }
}