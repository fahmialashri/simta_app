package com.project.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.data.model.ThesisChapter
import com.project.data.model.ThesisSubmission
import com.project.navigation.Screen
import com.project.notification.GuidanceReminderScheduler
import com.project.supervisor.SupervisorRequestViewModel
import com.project.upload.UploadBerkasViewModel

private data class MahasiswaNotification(
    val id: String,
    val title: String,
    val message: String,
    val status: String,
    val isRead: Boolean = false
)

@Composable
fun MahasiswaDashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    supervisorRequestViewModel: SupervisorRequestViewModel,
    uploadBerkasViewModel: UploadBerkasViewModel
) {
    val context = LocalContext.current

    val authState by authViewModel.uiState.collectAsState()
    val requestState by supervisorRequestViewModel.uiState.collectAsState()
    val uploadState by uploadBerkasViewModel.uiState.collectAsState()

    var showNotifications by remember { mutableStateOf(false) }
    var readNotificationIds by remember {
        mutableStateOf(loadReadNotificationIds(context))
    }

    LaunchedEffect(authState.userId) {
        supervisorRequestViewModel.loadMyRequestProgress(authState.userId)
        uploadBerkasViewModel.loadMySubmissions(authState.userId)

        if (!authState.userId.isNullOrBlank()) {
            GuidanceReminderScheduler.schedule(
                context = context,
                studentId = authState.userId.orEmpty()
            )
        }
    }

    val request = requestState.activeRequest
    val isPending = request?.status == "pending"
    val isAccepted = request?.status == "accepted"
    val isRejected = request?.status == "rejected"

    val approvedCount = requestState.chapters.count { it.status == "approved" }
    val totalChapters = requestState.chapters.size

    val progress = if (isAccepted && totalChapters > 0) {
        approvedCount.toFloat() / totalChapters.toFloat()
    } else {
        0f
    }

    val progressLabel = if (isAccepted && totalChapters > 0) {
        "${(progress * 100).toInt()} % Selesai"
    } else {
        "0 % Selesai"
    }

    val departmentName = when (authState.departmentId) {
        1L -> "Informatika"
        2L -> "Sistem Informasi"
        else -> "Program Studi"
    }

    val latestSubmissionWithSupervisor2 = uploadState.submissions.firstOrNull {
        !it.supervisor2.isNullOrBlank()
    }

    val supervisor1Name = requestState.activeLecturer?.fullName ?: "Belum memilih dosen"
    val supervisor2Name = latestSubmissionWithSupervisor2?.supervisor2

    val notifications = remember(
        request?.status,
        request?.title,
        requestState.activeLecturer?.fullName,
        uploadState.submissions,
        readNotificationIds
    ) {
        buildMahasiswaNotifications(
            requestStatus = request?.status,
            lecturerName = requestState.activeLecturer?.fullName,
            requestTitle = request?.title,
            submissions = uploadState.submissions
        ).map { notification ->
            notification.copy(
                isRead = notification.id in readNotificationIds
            )
        }
    }

    val hasUnreadNotification = notifications.any { !it.isRead }
    val notificationBadgeCount = if (hasUnreadNotification) 1 else 0

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            SimtaBottomNavigation(
                onHomeClick = {
                    // Sudah di dashboard
                },
                onPengajuanClick = {
                    navController.navigate(Screen.Pengajuan.route)
                },
                onBimbinganClick = {
                    navController.navigate(Screen.Bimbingan.route)
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
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            DashboardHeader(
                name = authState.name ?: "MAHASISWA",
                departmentName = departmentName,
                notificationBadgeCount = notificationBadgeCount,
                onNotificationClick = {
                    val nextShowState = !showNotifications
                    showNotifications = nextShowState

                    if (nextShowState && notifications.isNotEmpty()) {
                        val updatedReadIds = readNotificationIds + notifications.map { it.id }
                        readNotificationIds = updatedReadIds

                        saveReadNotificationIds(
                            context = context,
                            ids = updatedReadIds
                        )
                    }
                }
            )

            if (showNotifications) {
                Spacer(modifier = Modifier.height(12.dp))

                NotificationPanel(
                    notifications = notifications,
                    onPengajuanClick = {
                        showNotifications = false
                        navController.navigate(Screen.Pengajuan.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            QuoteCard()

            Spacer(modifier = Modifier.height(20.dp))

            GuidanceReminderSection(
                daysSinceLastGuidance = null,
                onScheduleClick = {
                    navController.navigate(Screen.Bimbingan.route)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            SupervisorCard(
                supervisor1Name = supervisor1Name,
                supervisor2Name = supervisor2Name,
                status = when {
                    isPending -> "(Menunggu Persetujuan)"
                    isAccepted -> "(Pembimbing Aktif)"
                    isRejected -> "(Pengajuan Ditolak)"
                    else -> "(Klik untuk pengajuan)"
                },
                onClick = {
                    navController.navigate(Screen.Pengajuan.route)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProgressCard(
                progress = progress,
                label = progressLabel
            )

            Spacer(modifier = Modifier.height(20.dp))

            when {
                requestState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SimtaRed)
                    }
                }

                requestState.errorMessage != null -> {
                    MahasiswaErrorCard(
                        message = requestState.errorMessage ?: "Terjadi kesalahan"
                    )
                }

                isAccepted -> {
                    ComponentStatusCard(
                        chapters = requestState.chapters
                    )
                }

                isPending -> {
                    WaitingApprovalCard(
                        lecturerName = requestState.activeLecturer?.fullName ?: "-",
                        title = request?.title ?: "-"
                    )
                }

                isRejected -> {
                    RejectedCard(
                        note = request?.lecturerNote
                    )
                }

                else -> {
                    EmptySubmissionCard(
                        onClick = {
                            navController.navigate(Screen.Pengajuan.route)
                        }
                    )
                }
            }
        }
    }
}

private fun buildMahasiswaNotifications(
    requestStatus: String?,
    lecturerName: String?,
    requestTitle: String?,
    submissions: List<ThesisSubmission>
): List<MahasiswaNotification> {
    val result = mutableListOf<MahasiswaNotification>()

    when (requestStatus) {
        "accepted" -> {
            result.add(
                MahasiswaNotification(
                    id = "supervisor_accepted_${requestTitle.orEmpty()}_${lecturerName.orEmpty()}",
                    title = "Pengajuan Pembimbing Disetujui",
                    message = "Pengajuan judul ${requestTitle.orEmpty().ifBlank { "skripsi" }} sudah disetujui. Dosen pembimbing: ${lecturerName ?: "-"}.",
                    status = "approved"
                )
            )
        }

        "rejected" -> {
            result.add(
                MahasiswaNotification(
                    id = "supervisor_rejected_${requestTitle.orEmpty()}",
                    title = "Pengajuan Pembimbing Ditolak",
                    message = "Pengajuan judul ${requestTitle.orEmpty().ifBlank { "skripsi" }} ditolak. Silakan cek kembali pengajuan kamu.",
                    status = "rejected"
                )
            )
        }

        "pending" -> {
            result.add(
                MahasiswaNotification(
                    id = "supervisor_pending_${requestTitle.orEmpty()}",
                    title = "Pengajuan Pembimbing Diproses",
                    message = "Pengajuan pembimbing kamu masih menunggu persetujuan.",
                    status = "pending"
                )
            )
        }
    }

    submissions.forEach { submission ->
        val stageName = submission.stage.toReadableStageName()
        val notificationBaseId = "submission_${submission.id}_${submission.stage}_${submission.status}"

        when (submission.status) {
            "menunggu_review" -> {
                result.add(
                    MahasiswaNotification(
                        id = notificationBaseId,
                        title = "$stageName Menunggu Review",
                        message = "Berkas $stageName kamu sudah terkirim dan sedang menunggu review TU.",
                        status = "pending"
                    )
                )
            }

            "disetujui_tu" -> {
                result.add(
                    MahasiswaNotification(
                        id = notificationBaseId,
                        title = "$stageName Disetujui TU",
                        message = "Berkas $stageName kamu sudah disetujui oleh TU.",
                        status = "approved"
                    )
                )
            }

            "ditolak_tu" -> {
                result.add(
                    MahasiswaNotification(
                        id = notificationBaseId,
                        title = "$stageName Ditolak TU",
                        message = "Berkas $stageName kamu ditolak oleh TU. Silakan periksa dan upload ulang jika diperlukan.",
                        status = "rejected"
                    )
                )
            }

            "disetujui_kaprodi" -> {
                result.add(
                    MahasiswaNotification(
                        id = notificationBaseId,
                        title = "$stageName Disetujui Kaprodi",
                        message = "Pengajuan $stageName kamu sudah disetujui oleh Kaprodi.",
                        status = "approved"
                    )
                )
            }

            "ditolak_kaprodi" -> {
                result.add(
                    MahasiswaNotification(
                        id = notificationBaseId,
                        title = "$stageName Ditolak Kaprodi",
                        message = "Pengajuan $stageName kamu ditolak oleh Kaprodi.",
                        status = "rejected"
                    )
                )
            }
        }
    }

    return result
}

private fun String.toReadableStageName(): String {
    return when (this) {
        "seminar_proposal" -> "Seminar Proposal"
        "revisi_seminar_proposal" -> "Revisi Seminar Proposal"
        "kolokium" -> "Kolokium"
        "pendaftaran_kolokium" -> "Kolokium"
        "revisi_kolokium" -> "Revisi Kolokium"
        "yudisium" -> "Yudisium"
        else -> this
            .replace("_", " ")
            .replaceFirstChar { it.uppercase() }
    }
}

@Composable
private fun DashboardHeader(
    name: String,
    departmentName: String,
    notificationBadgeCount: Int,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SimtaRed,
                            Color(0xFFD32F2F),
                            Color(0xFF8B0000)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Selamat Datang!",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = name.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = departmentName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SimtaRed
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onNotificationClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )

            if (notificationBadgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(SimtaRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "1",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationPanel(
    notifications: List<MahasiswaNotification>,
    onPengajuanClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8F8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Notifikasi",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )

            if (notifications.isEmpty()) {
                Text(
                    text = "Belum ada notifikasi approval.",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            } else {
                notifications.forEach { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = onPengajuanClick
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: MahasiswaNotification,
    onClick: () -> Unit
) {
    val icon: ImageVector
    val iconColor: Color
    val bgColor: Color

    when (notification.status) {
        "approved" -> {
            icon = Icons.Default.CheckCircle
            iconColor = Color(0xFF2E7D32)
            bgColor = Color(0xFFE8F5E9)
        }

        "rejected" -> {
            icon = Icons.Default.ErrorOutline
            iconColor = SimtaRed
            bgColor = Color(0xFFFFEBEE)
        }

        else -> {
            icon = Icons.Default.Schedule
            iconColor = Color(0xFFFFA000)
            bgColor = Color(0xFFFFF8E1)
        }
    }

    val itemBackground = if (notification.isRead) {
        Color(0xFFF2F2F2)
    } else {
        Color.White
    }

    val titleColor = if (notification.isRead) {
        Color(0xFF777777)
    } else {
        Color.Black
    }

    val messageColor = if (notification.isRead) {
        Color(0xFF8A8A8A)
    } else {
        Color.DarkGray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(itemBackground)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    color = titleColor,
                    fontSize = 12.sp,
                    fontWeight = if (notification.isRead) {
                        FontWeight.SemiBold
                    } else {
                        FontWeight.Bold
                    },
                    modifier = Modifier.weight(1f)
                )

                if (notification.isRead) {
                    Text(
                        text = "Sudah dibaca",
                        color = Color(0xFF888888),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SimtaRed)
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = notification.message,
                color = messageColor,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun QuoteCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SimtaRed
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Masa depan adalah milik mereka yang percaya\ndengan impiannya.",
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "- Tan Malaka -",
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun GuidanceReminderSection(
    daysSinceLastGuidance: Int?,
    onScheduleClick: () -> Unit = {}
) {
    val days = daysSinceLastGuidance

    val level = when {
        days == null -> -1
        days >= 21 -> 3
        days >= 14 -> 2
        days >= 7 -> 1
        else -> 0
    }

    val badgeText: String
    val titleText: String
    val messageText: String
    val accentColor: Color
    val progressValue: Float

    when (level) {
        3 -> {
            badgeText = "Peringatan Terakhir"
            titleText = "21 Hari"
            messageText = "Sudah 21 hari atau lebih sejak bimbingan terakhir. Segera hubungi dosen pembimbing agar progres TA tidak terhambat."
            accentColor = Color(0xFFD32F2F)
            progressValue = 1f
        }

        2 -> {
            badgeText = "Pengingat Kedua"
            titleText = "14 Hari"
            messageText = "Sudah 14 hari belum melakukan bimbingan. Jangan sampai progres skripsi tertunda."
            accentColor = Color(0xFFFF9800)
            progressValue = 0.66f
        }

        1 -> {
            badgeText = "Pengingat Pertama"
            titleText = "7 Hari"
            messageText = "Sudah 7 hari sejak bimbingan terakhir. Yuk segera hubungi dosen pembimbing."
            accentColor = Color(0xFFFFC107)
            progressValue = 0.33f
        }

        0 -> {
            badgeText = "Bimbingan Aman"
            titleText = "${days ?: 0} Hari"
            messageText = "Kamu masih berada di zona aman. Pertahankan rutinitas bimbingan agar progres tetap lancar."
            accentColor = Color(0xFF4CAF50)
            progressValue = 0f
        }

        else -> {
            badgeText = "Belum Ada Data"
            titleText = "—"
            messageText = "Belum ada data bimbingan terakhir yang bisa dihitung."
            accentColor = Color(0xFF9E9E9E)
            progressValue = 0f
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = accentColor
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pengingat Bimbingan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = badgeText,
                        color = accentColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = titleText,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sejak bimbingan terakhir",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressValue.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(50))
                        .background(accentColor)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = messageText,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor)
                    .clickable { onScheduleClick() }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Lakukan Bimbingan",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SupervisorCard(
    supervisor1Name: String,
    supervisor2Name: String?,
    status: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Dosen Pembimbing",
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            SupervisorRow(
                lecturerName = supervisor1Name,
                label = when {
                    supervisor1Name == "Belum memilih dosen" -> status
                    else -> "Pembimbing 1"
                }
            )

            if (!supervisor2Name.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                SupervisorRow(
                    lecturerName = supervisor2Name,
                    label = "Pembimbing 2"
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Pembimbing 2 belum tersedia. Data akan muncul setelah revisi seminar proposal dikirim dan dosen penguji proposal tercatat.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
private fun SupervisorRow(
    lecturerName: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8EAED)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = lecturerName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun ProgressCard(
    progress: Float,
    label: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status Bimbingan",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "(BAB 1 - BAB 5)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(50))
                        .background(SimtaRed),
                    contentAlignment = Alignment.Center
                ) {
                    if (progress > 0.2f) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(5) { index ->
                    val babNumber = index + 1
                    val isDone = progress >= babNumber / 5f

                    Text(
                        text = if (isDone) "BAB $babNumber ✓" else "BAB $babNumber",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) SimtaRed else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ComponentStatusCard(
    chapters: List<ThesisChapter>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SimtaRed)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Komponen",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Status",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (chapters.isEmpty()) {
                    Text(
                        text = "Data BAB belum tersedia.",
                        fontSize = 12.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    chapters.forEach { chapter ->
                        BabStatusRow(
                            title = chapter.title.uppercase(),
                            status = chapter.status
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BabStatusRow(
    title: String,
    status: String
) {
    val (label, bgColor, textColor) = when (status) {
        "approved" -> Triple("Disetujui", Color(0xFF8BC34A), Color.White)
        "revision" -> Triple("Revisi", Color(0xFFFFCA28), Color.White)
        "process" -> Triple("Proses", Color(0xFFD0D0D0), Color.White)
        else -> Triple("Belum", Color(0xFFE0E0E0), Color.DarkGray)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .width(80.dp)
                .clip(RoundedCornerShape(50))
                .background(bgColor)
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun EmptySubmissionCard(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pengajuan Pembimbing",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Belum ada pengajuan dosen pembimbing.",
                fontSize = 12.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Klik di sini untuk membuka formulir pengajuan.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SimtaRed
            )
        }
    }
}

@Composable
private fun WaitingApprovalCard(
    lecturerName: String,
    title: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Status Pengajuan",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Menunggu Persetujuan",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA000)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dosen: $lecturerName",
                fontSize = 12.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Judul: $title",
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun RejectedCard(
    note: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F3F4)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Pengajuan Ditolak",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SimtaRed
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note ?: "Silakan ajukan dosen pembimbing lain.",
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun MahasiswaErrorCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Text(
            text = message,
            color = SimtaRed,
            modifier = Modifier.padding(16.dp),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SimtaBottomNavigation(
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
            selected = true,
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
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
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
                Text(
                    text = "Pengajuan",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
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
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Bimbingan",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
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
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Profil",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        )
    }
}

private const val NOTIFICATION_PREF_NAME = "simta_notification_pref"
private const val READ_NOTIFICATION_IDS_KEY = "read_notification_ids"

private fun loadReadNotificationIds(
    context: Context
): Set<String> {
    return context
        .getSharedPreferences(NOTIFICATION_PREF_NAME, Context.MODE_PRIVATE)
        .getStringSet(READ_NOTIFICATION_IDS_KEY, emptySet())
        ?.toSet()
        ?: emptySet()
}

private fun saveReadNotificationIds(
    context: Context,
    ids: Set<String>
) {
    context
        .getSharedPreferences(NOTIFICATION_PREF_NAME, Context.MODE_PRIVATE)
        .edit()
        .putStringSet(READ_NOTIFICATION_IDS_KEY, ids)
        .apply()
}
