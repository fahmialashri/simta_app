package com.project.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.bimbingan.BimbinganViewModel
import com.project.core.SimtaGreen
import com.project.core.SimtaRed
import com.project.data.model.ChapterSubmission
import com.project.navigation.Screen
import com.project.supervisor.SupervisorRequestViewModel

@Composable
fun BimbinganDetailScreen(
    chapterId: Long,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    supervisorRequestViewModel: SupervisorRequestViewModel,
    bimbinganViewModel: BimbinganViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val supervisorState by supervisorRequestViewModel.uiState.collectAsState()
    val bimbinganState by bimbinganViewModel.uiState.collectAsState()

    val chapter = bimbinganState.chapters.find { it.id == chapterId }
    val request = supervisorState.activeRequest

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedMimeType by remember { mutableStateOf<String?>(null) }

    var driveUrl by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedUri = uri

        if (uri != null) {
            selectedMimeType = context.contentResolver.getType(uri)
            selectedFileName = context.getFileName(uri)
        }
    }

    LaunchedEffect(chapterId) {
        bimbinganViewModel.loadSubmissions(chapterId)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA), // Background abu-abu muda
        bottomBar = {
            DetailBottomNavigation(
                onHomeClick = {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.MahasiswaDashboard.route) { inclusive = true }
                    }
                },
                onPengajuanClick = {
                    navController.navigate(Screen.Pengajuan.route)
                },
                onBimbinganClick = {
                    navController.navigate(Screen.Bimbingan.route) {
                        popUpTo(Screen.Bimbingan.route) { inclusive = true }
                    }
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Modern Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Detail Bimbingan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = chapter?.title ?: "BAB",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(SimtaGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Detail",
                        color = SimtaGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Upload & Interaction Card
            UploadCard(
                selectedFileName = selectedFileName,
                driveUrl = driveUrl,
                note = note,
                lecturerNote = chapter?.lecturerNote,
                onChooseFile = {
                    filePicker.launch(
                        arrayOf(
                            "application/pdf",
                            "application/msword",
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                        )
                    )
                },
                onDriveUrlChange = { driveUrl = it },
                onNoteChange = { note = it },
                onSubmit = {
                    bimbinganViewModel.submitChapter(
                        context = context,
                        studentId = authState.userId,
                        supervisorRequestId = request?.id,
                        chapterId = chapterId,
                        fileUri = selectedUri,
                        fileName = selectedFileName,
                        mimeType = selectedMimeType,
                        driveUrl = driveUrl,
                        note = note
                    )
                }
            )

            // Pesan Status (Error/Success)
            if (bimbinganState.errorMessage != null || bimbinganState.successMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))

                val isError = bimbinganState.errorMessage != null
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isError) Color(0xFFFFEBEE) else SimtaGreen.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = bimbinganState.errorMessage ?: bimbinganState.successMessage ?: "",
                        color = if (isError) SimtaRed else SimtaGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log Files Section
            LogFilesCard(
                submissions = bimbinganState.submissions,
                onOpenUrl = { url ->
                    context.openUrl(url)
                }
            )

            Spacer(modifier = Modifier.height(32.dp)) // Extra padding bawah
        }
    }
}

@Composable
private fun UploadCard(
    selectedFileName: String?,
    driveUrl: String,
    note: String,
    lecturerNote: String?,
    onChooseFile: () -> Unit,
    onDriveUrlChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Area Drop/Pilih File
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F3F4))
                    .clickable { onChooseFile() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = SimtaRed,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedFileName ?: "Klik untuk upload file (PDF/DOCX)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedFileName != null) Color.Black else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = driveUrl,
                onValueChange = onDriveUrlChange,
                label = { Text("Link Google Drive (Opsional)", fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = SimtaRed
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = SimtaRed
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Highlight Komentar Dosen
            if (!lecturerNote.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF8E1)) // Warna kuning super tipis buat highlight
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Komentar Dosen Pembimbing",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFF57F17)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = lecturerNote,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tulis Catatan / Balasan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                placeholder = {
                    Text(
                        text = "Jelaskan revisi atau progres anda di sini...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = SimtaRed
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = SimtaRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Kirim Pembaruan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun LogFilesCard(
    submissions: List<ChapterSubmission>,
    onOpenUrl: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Riwayat Pengiriman",
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (submissions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada riwayat file yang dikirim.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        } else {
            submissions.forEach { submission ->
                LogFileItem(
                    submission = submission,
                    onOpenUrl = onOpenUrl
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun LogFileItem(
    submission: ChapterSubmission,
    onOpenUrl: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F3F4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = SimtaRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = submission.fileName ?: "Link Google Drive",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Black,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = submission.note ?: "Tidak ada catatan",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SimtaRed.copy(alpha = 0.1f))
                    .clickable {
                        val url = submission.fileUrl ?: submission.driveUrl
                        if (!url.isNullOrBlank()) {
                            onOpenUrl(url)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = SimtaRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailBottomNavigation(
    onHomeClick: () -> Unit,
    onPengajuanClick: () -> Unit,
    onBimbinganClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = SimtaRed,
        unselectedIconColor = Color.White.copy(alpha = 0.7f),
        selectedTextColor = Color.White,
        unselectedTextColor = Color.White.copy(alpha = 0.7f),
        indicatorColor = Color.White
    )

    NavigationBar(
        containerColor = SimtaRed,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onPengajuanClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Article,
                    contentDescription = "Pengajuan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Pengajuan", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        )

        NavigationBarItem(
            selected = true, // Menu bimbingan tetap aktif karena detail ada dalam flow bimbingan
            onClick = onBimbinganClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Bimbingan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Bimbingan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Profil", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        )
    }
}

private fun Context.getFileName(uri: Uri): String? {
    var result: String? = null

    if (uri.scheme == "content") {
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    result = it.getString(index)
                }
            }
        }
    }

    if (result == null) {
        result = uri.path?.substringAfterLast('/')
    }

    return result
}

private fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}