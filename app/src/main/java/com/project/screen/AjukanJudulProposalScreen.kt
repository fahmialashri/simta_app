package com.project.screen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.lecturer.LecturerViewModel
import com.project.navigation.Screen
import com.project.supervisor.SupervisorRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjukanJudulProposalScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    lecturerViewModel: LecturerViewModel,
    supervisorRequestViewModel: SupervisorRequestViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by authViewModel.uiState.collectAsState()
    val lecturerState by lecturerViewModel.uiState.collectAsState()
    val requestState by supervisorRequestViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf(authState.email.orEmpty()) }
    var npm by remember { mutableStateOf(authState.nim.orEmpty()) }
    var fullName by remember { mutableStateOf(authState.name.orEmpty()) }
    var phone by remember { mutableStateOf("") }
    var proposalTitle by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("") }
    var estimatedCompletion by remember { mutableStateOf("") }

    var selectedLecturerName by remember { mutableStateOf("") }
    var selectedLecturerId by remember { mutableLongStateOf(0L) }
    var lecturerDropdownExpanded by remember { mutableStateOf(false) }

    var buktiKrsUri by remember { mutableStateOf<Uri?>(null) }
    var buktiKrsFileName by remember { mutableStateOf("") }
    var buktiKrsMimeType by remember { mutableStateOf("application/pdf") }
    var buktiKrsBytes by remember { mutableStateOf<ByteArray?>(null) }

    val isSistemInformasi = authState.departmentId == 2L

    val topicOptions = listOf(
        "Software Engineering",
        "Data Science",
        "Network and Security",
        "Artificial Intelligence and Robotics"
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }

        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: Exception) {
            // Tidak semua provider file mengizinkan persist permission, jadi aman diabaikan.
        }

        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

        if (
            mimeType != "application/pdf" &&
            mimeType != "image/jpeg" &&
            mimeType != "image/png"
        ) {
            Toast.makeText(
                context,
                "Format file harus PDF, JPG, atau PNG.",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        val bytes = context.readBytesFromUri(uri)

        if (bytes == null || bytes.isEmpty()) {
            Toast.makeText(
                context,
                "File tidak terbaca. Coba pilih file lain.",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        val maxSizeBytes = 50 * 1024 * 1024

        if (bytes.size > maxSizeBytes) {
            Toast.makeText(
                context,
                "Ukuran file maksimal 50 MB.",
                Toast.LENGTH_SHORT
            ).show()
            return@rememberLauncherForActivityResult
        }

        buktiKrsUri = uri
        buktiKrsFileName = context.getFileNameFromUri(uri)
        buktiKrsMimeType = mimeType
        buktiKrsBytes = bytes

        Toast.makeText(
            context,
            "Bukti KRS berhasil dipilih.",
            Toast.LENGTH_SHORT
        ).show()
    }

    LaunchedEffect(authState.email, authState.nim, authState.name) {
        if (email.isBlank()) email = authState.email.orEmpty()
        if (npm.isBlank()) npm = authState.nim.orEmpty()
        if (fullName.isBlank()) fullName = authState.name.orEmpty()
    }

    LaunchedEffect(authState.departmentId, selectedTopic) {
        selectedLecturerName = ""
        selectedLecturerId = 0L
        lecturerDropdownExpanded = false

        when {
            authState.departmentId == null -> {
                // Tunggu profile selesai kebaca.
            }

            isSistemInformasi -> {
                lecturerViewModel.loadLecturersByDepartment(authState.departmentId)
            }

            selectedTopic.isNotBlank() -> {
                lecturerViewModel.loadLecturersByDepartmentAndExpertise(
                    departmentId = authState.departmentId,
                    expertise = selectedTopic
                )
            }
        }
    }

    LaunchedEffect(requestState.isSuccess) {
        if (requestState.isSuccess) {
            Toast.makeText(
                context,
                "Pengajuan berhasil dikirim.",
                Toast.LENGTH_SHORT
            ).show()

            supervisorRequestViewModel.resetState()

            navController.navigate(Screen.MahasiswaDashboard.route) {
                popUpTo(Screen.AjukanJudulProposal.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(requestState.errorMessage) {
        requestState.errorMessage?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF2F3F5),
        bottomBar = {
            AjukanJudulBottomNavigation(
                onHomeClick = {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.MahasiswaDashboard.route) {
                            inclusive = true
                        }
                    }
                },
                onPengajuanClick = {
                    navController.navigate(Screen.Pengajuan.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                    }
                },
                onBimbinganClick = {
                    navController.navigate(Screen.Bimbingan.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                    }
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.MahasiswaDashboard.route)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF2F3F5))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "AJUKAN USULAN JUDUL\nPROPOSAL SKRIPSI",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            FormCard {
                FormTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormTextField(
                    label = "NPM",
                    value = npm,
                    onValueChange = { npm = it },
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormTextField(
                    label = "Nama Lengkap",
                    value = fullName,
                    onValueChange = { fullName = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormTextField(
                    label = "No. Handphone (WA)",
                    value = phone,
                    onValueChange = { phone = it },
                    keyboardType = KeyboardType.Phone
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FormCard {
                FormTextField(
                    label = "Judul Proposal Skripsi",
                    value = proposalTitle,
                    onValueChange = { proposalTitle = it },
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                FormTextField(
                    label = "Alasan Pengajuan Judul",
                    value = reason,
                    onValueChange = { reason = it },
                    minLines = 4
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (!isSistemInformasi) {
                FormCard {
                    Text(
                        text = "Pilihan Topik Proposal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        topicOptions.forEach { topic ->
                            TopicOptionRow(
                                text = topic,
                                selected = selectedTopic == topic,
                                onClick = {
                                    selectedTopic = topic
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            FormCard {
                Text(
                    text = "Usulan Dosen Pembimbing",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = lecturerDropdownExpanded,
                    onExpandedChange = {
                        when {
                            !isSistemInformasi && selectedTopic.isBlank() -> {
                                Toast.makeText(
                                    context,
                                    "Pilih topik proposal terlebih dahulu.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            lecturerState.lecturers.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "Data dosen belum tersedia.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                lecturerDropdownExpanded = !lecturerDropdownExpanded
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = when {
                            selectedLecturerName.isNotBlank() -> selectedLecturerName
                            isSistemInformasi -> "Pilih dosen pembimbing Sistem Informasi"
                            selectedTopic.isBlank() -> "Pilih topik proposal terlebih dahulu"
                            lecturerState.isLoading -> "Memuat dosen..."
                            else -> "Pilih dosen bidang $selectedTopic"
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = lecturerDropdownExpanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(56.dp),
                        textStyle = TextStyle(fontSize = 12.sp),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SimtaRed,
                            unfocusedBorderColor = Color(0xFFBDBDBD),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = lecturerDropdownExpanded,
                        onDismissRequest = {
                            lecturerDropdownExpanded = false
                        }
                    ) {
                        lecturerState.lecturers.forEach { lecturer ->
                            val lecturerDisplayName = if (lecturer.title.isNullOrBlank()) {
                                lecturer.name
                            } else {
                                "${lecturer.name}, ${lecturer.title}"
                            }

                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = lecturerDisplayName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )

                                        if (!lecturer.expertise.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))

                                            Text(
                                                text = lecturer.expertise,
                                                fontSize = 10.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedLecturerId = lecturer.id
                                    selectedLecturerName = lecturerDisplayName
                                    lecturerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (!isSistemInformasi && selectedTopic.isBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Pilih topik terlebih dahulu agar dosen yang muncul sesuai bidang keahlian.",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                if (lecturerState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = lecturerState.errorMessage ?: "",
                        fontSize = 10.sp,
                        color = SimtaRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FormCard {
                Text(
                    text = "Bukti KRS Tugas Akhir",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .clickable(enabled = !requestState.isLoading) {
                            filePickerLauncher.launch(
                                arrayOf(
                                    "application/pdf",
                                    "image/jpeg",
                                    "image/png"
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (buktiKrsUri == null) {
                                Icons.Default.UploadFile
                            } else {
                                Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = if (buktiKrsUri == null) {
                                Color.Gray
                            } else {
                                Color(0xFF2E7D32)
                            },
                            modifier = Modifier.size(30.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (buktiKrsFileName.isBlank()) {
                                "Ketuk Untuk Unggah"
                            } else {
                                buktiKrsFileName
                            },
                            fontSize = 12.sp,
                            fontWeight = if (buktiKrsFileName.isBlank()) {
                                FontWeight.Normal
                            } else {
                                FontWeight.Bold
                            },
                            color = if (buktiKrsFileName.isBlank()) {
                                Color.DarkGray
                            } else {
                                Color(0xFF2E7D32)
                            }
                        )

                        Text(
                            text = "JPG, PNG atau PDF • Maks. 50 MB",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FormCard {
                FormTextField(
                    label = "Estimasi Penyelesaian BAB 1-3",
                    value = estimatedCompletion,
                    onValueChange = { estimatedCompletion = it },
                    placeholder = "Contoh: 2 bulan"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (authState.userId.isNullOrBlank()) {
                        Toast.makeText(
                            context,
                            "User belum terbaca. Silakan login ulang.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (proposalTitle.isBlank()) {
                        Toast.makeText(
                            context,
                            "Judul proposal wajib diisi.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (!isSistemInformasi && selectedTopic.isBlank()) {
                        Toast.makeText(
                            context,
                            "Pilih topik proposal terlebih dahulu.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (selectedLecturerId == 0L) {
                        Toast.makeText(
                            context,
                            "Pilih dosen pembimbing terlebih dahulu.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    if (buktiKrsBytes == null) {
                        Toast.makeText(
                            context,
                            "Bukti KRS wajib diunggah.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val finalTopic = if (isSistemInformasi) {
                        "Sistem Informasi"
                    } else {
                        selectedTopic
                    }

                    supervisorRequestViewModel.submitRequest(
                        studentId = authState.userId,
                        lecturerId = selectedLecturerId,
                        title = proposalTitle,
                        topic = finalTopic,
                        reason = reason,
                        phone = phone,
                        estimatedCompletion = estimatedCompletion,
                        buktiKrsFileName = buktiKrsFileName,
                        buktiKrsMimeType = buktiKrsMimeType,
                        buktiKrsBytes = buktiKrsBytes
                    )
                },
                enabled = !requestState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFCA28),
                    contentColor = Color.Black,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray
                )
            ) {
                Text(
                    text = if (requestState.isLoading) {
                        "Mengirim Pengajuan..."
                    } else {
                        "Kirim Pengajuan"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun Context.readBytesFromUri(uri: Uri): ByteArray? {
    return try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    } catch (_: Exception) {
        null
    }
}

private fun Context.getFileNameFromUri(uri: Uri): String {
    return try {
        val cursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (it.moveToFirst() && nameIndex >= 0) {
                return it.getString(nameIndex)
            }
        }

        "bukti_krs.pdf"
    } catch (_: Exception) {
        "bukti_krs.pdf"
    }
}

@Composable
private fun FormCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            content = content
        )
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    placeholder: String = ""
) {
    val fieldHeight = if (minLines > 1) {
        if (minLines >= 4) 128.dp else 112.dp
    } else {
        56.dp
    }

    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(
                        text = placeholder,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight),
            minLines = minLines,
            maxLines = if (minLines > 1) minLines else 1,
            singleLine = minLines == 1,
            textStyle = TextStyle(
                fontSize = 13.sp,
                lineHeight = 18.sp
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SimtaRed,
                unfocusedBorderColor = Color(0xFFBDBDBD),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = SimtaRed
            )
        )
    }
}

@Composable
private fun TopicOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Color.White, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier.size(24.dp),
            colors = RadioButtonDefaults.colors(
                selectedColor = SimtaRed,
                unselectedColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun AjukanJudulBottomNavigation(
    onHomeClick: () -> Unit,
    onPengajuanClick: () -> Unit,
    onBimbinganClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color.White,
        unselectedIconColor = Color.White.copy(alpha = 0.68f),
        selectedTextColor = Color.White,
        unselectedTextColor = Color.White.copy(alpha = 0.68f),
        indicatorColor = Color.Transparent
    )

    NavigationBar(
        containerColor = SimtaRed,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 9.sp
                )
            }
        )

        NavigationBarItem(
            selected = true,
            onClick = onPengajuanClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Article,
                    contentDescription = "Pengajuan",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Pengajuan",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onBimbinganClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Bimbingan",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Bimbingan",
                    fontSize = 9.sp
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onProfileClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = "Profil",
                    fontSize = 9.sp
                )
            }
        )
    }
}