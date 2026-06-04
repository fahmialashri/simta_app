package com.project.screen

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.data.model.Lecturer
import com.project.kaprodi.KaprodiViewModel
import com.project.lecturer.LecturerViewModel

@Composable
fun KaprodiDashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit = {},
    viewModel: KaprodiViewModel = viewModel(),
    lecturerViewModel: LecturerViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val lecturerState by lecturerViewModel.uiState.collectAsState()

    val kaprodiDepartmentId = authState.departmentId ?: 1L

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var pengajuanFilter by remember {
        mutableStateOf("pending")
    }

    var pengajuanSearch by remember {
        mutableStateOf("")
    }

    var dosenSearch by remember {
        mutableStateOf("")
    }

    var trackingSearch by remember {
        mutableStateOf("")
    }

    var showAddLecturerDialog by remember {
        mutableStateOf(false)
    }

    var quotaEditLecturer by remember {
        mutableStateOf<Lecturer?>(null)
    }

    var hasShownOpenNotification by remember {
        mutableStateOf(false)
    }

    var showOpenNotificationDialog by remember {
        mutableStateOf(false)
    }

    var showNotificationListDialog by remember {
        mutableStateOf(false)
    }

    val pendingSubmissions = state.submissions.filter {
        it.status.equals("pending", ignoreCase = true)
    }

    val pendingCount = pendingSubmissions.size

    val approvedCount = state.submissions.count {
        it.status.equals("accepted", ignoreCase = true)
    }

    val rejectedCount = state.submissions.count {
        it.status.equals("rejected", ignoreCase = true)
    }

    val historyCount = approvedCount + rejectedCount

    val studentCount = state.studentTrackings.size

    LaunchedEffect(kaprodiDepartmentId) {
        selectedTab = 0
        pengajuanFilter = "pending"

        viewModel.loadRequests(kaprodiDepartmentId)
        viewModel.loadStudentTracking(kaprodiDepartmentId)
        lecturerViewModel.loadAllLecturersForKaprodi(kaprodiDepartmentId)
    }

    LaunchedEffect(pendingCount, state.isLoading) {
        if (!state.isLoading && pendingCount > 0 && !hasShownOpenNotification) {
            showOpenNotificationDialog = true
            hasShownOpenNotification = true
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
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
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Dashboard Kaprodi",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = "Pengajuan, dosen, dan tracking mahasiswa",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    onClick = {
                        showNotificationListDialog = true
                    }
                ) {
                    Box(
                        modifier = Modifier.padding(12.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifikasi",
                            tint = SimtaRed,
                            modifier = Modifier.size(26.dp)
                        )

                        if (pendingCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = SimtaRed
                                    ),
                                    shape = CircleShape
                                ) {
                                    Box(
                                        modifier = Modifier.size(18.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = pendingCount.toString(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = SimtaRed),
                    onClick = onLogout
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (pendingCount > 0) {
                KaprodiMessageCard(
                    message = "Ada $pendingCount pengajuan baru yang menunggu persetujuan.",
                    isError = false
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KaprodiStatCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Pengajuan Baru",
                    value = pendingCount.toString(),
                    icon = Icons.Default.Timer,
                    color = Color(0xFFFFA000),
                    onClick = {
                        selectedTab = 0
                        pengajuanFilter = "pending"
                        viewModel.loadRequests(kaprodiDepartmentId)
                    }
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KaprodiStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Mahasiswa",
                        value = studentCount.toString(),
                        icon = Icons.Default.Person,
                        color = Color(0xFF1976D2),
                        onClick = {
                            selectedTab = 2
                            viewModel.loadStudentTracking(kaprodiDepartmentId)
                        }
                    )

                    KaprodiStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Dosen Aktif",
                        value = lecturerState.lecturers.count { it.isActive }.toString(),
                        icon = Icons.Default.School,
                        color = Color(0xFF2E7D32),
                        onClick = {
                            selectedTab = 1
                            lecturerViewModel.loadAllLecturersForKaprodi(kaprodiDepartmentId)
                        }
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KaprodiStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Disetujui",
                        value = approvedCount.toString(),
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF2E7D32),
                        onClick = {
                            selectedTab = 0
                            pengajuanFilter = "accepted"
                            viewModel.loadRequests(kaprodiDepartmentId)
                        }
                    )

                    KaprodiStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Ditolak",
                        value = rejectedCount.toString(),
                        icon = Icons.Default.Warning,
                        color = SimtaRed,
                        onClick = {
                            selectedTab = 0
                            pengajuanFilter = "rejected"
                            viewModel.loadRequests(kaprodiDepartmentId)
                        }
                    )
                }

                KaprodiStatCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = "History",
                    value = historyCount.toString(),
                    icon = Icons.Default.Person,
                    color = Color(0xFF455A64),
                    onClick = {
                        selectedTab = 0
                        pengajuanFilter = "history"
                        viewModel.loadRequests(kaprodiDepartmentId)
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SimtaRed
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        pengajuanFilter = "pending"
                        viewModel.loadRequests(kaprodiDepartmentId)
                    },
                    text = {
                        Text("Pengajuan")
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        lecturerViewModel.loadAllLecturersForKaprodi(kaprodiDepartmentId)
                    },
                    text = {
                        Text("Dosen")
                    }
                )

                Tab(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        viewModel.loadStudentTracking(kaprodiDepartmentId)
                    },
                    text = {
                        Text("Tracking")
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (selectedTab) {
                0 -> {
                    SearchBox(
                        value = pengajuanSearch,
                        onValueChange = {
                            pengajuanSearch = it
                        },
                        placeholder = "Cari nama, NIM, dosen, judul, status..."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredByStatus = state.submissions.filter { item ->
                        when (pengajuanFilter) {
                            "pending" -> item.status.equals("pending", ignoreCase = true)
                            "accepted" -> item.status.equals("accepted", ignoreCase = true)
                            "rejected" -> item.status.equals("rejected", ignoreCase = true)
                            "history" -> item.status.equals("accepted", ignoreCase = true) ||
                                    item.status.equals("rejected", ignoreCase = true)
                            else -> true
                        }
                    }

                    val filteredSubmissions = filteredByStatus.filter { item ->
                        val keyword = pengajuanSearch.trim().lowercase()

                        keyword.isBlank() ||
                                item.studentName.lowercase().contains(keyword) ||
                                item.nim.lowercase().contains(keyword) ||
                                item.lecturerName.lowercase().contains(keyword) ||
                                item.recommendedLecturerName.orEmpty().lowercase().contains(keyword) ||
                                item.title.orEmpty().lowercase().contains(keyword) ||
                                item.topic.orEmpty().lowercase().contains(keyword) ||
                                item.status.lowercase().contains(keyword) ||
                                item.note.orEmpty().lowercase().contains(keyword) ||
                                item.recommendationNote.orEmpty().lowercase().contains(keyword)
                    }

                    Text(
                        text = when (pengajuanFilter) {
                            "pending" -> "Menampilkan pengajuan baru"
                            "accepted" -> "Menampilkan pengajuan yang disetujui"
                            "rejected" -> "Menampilkan pengajuan yang ditolak"
                            "history" -> "Menampilkan history pengajuan"
                            else -> "Menampilkan semua pengajuan"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    KaprodiRequestSection(
                        state = state.copy(submissions = filteredSubmissions),
                        onApprove = { requestId ->
                            viewModel.approveRequest(
                                requestId = requestId,
                                departmentId = kaprodiDepartmentId
                            )
                        },
                        onReject = { requestId, note ->
                            viewModel.rejectRequest(
                                requestId = requestId,
                                note = note,
                                departmentId = kaprodiDepartmentId
                            )
                        },
                        onArchive = { requestId ->
                            viewModel.archiveRequest(
                                requestId = requestId,
                                departmentId = kaprodiDepartmentId
                            )
                        },
                        onRecommendLecturer = { requestId, lecturerId, note ->
                            viewModel.saveLecturerRecommendation(
                                requestId = requestId,
                                lecturerId = lecturerId,
                                note = note,
                                departmentId = kaprodiDepartmentId
                            )
                        }
                    )
                }

                1 -> {
                    SearchBox(
                        value = dosenSearch,
                        onValueChange = {
                            dosenSearch = it
                        },
                        placeholder = "Cari nama dosen, expertise, status, kuota..."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredLecturers = lecturerState.lecturers.filter { lecturer ->
                        val keyword = dosenSearch.trim().lowercase()
                        val quotaText = "${lecturer.currentStudents}/${lecturer.quota}"
                        val remainingQuota = (lecturer.quota - lecturer.currentStudents)
                            .coerceAtLeast(0)
                            .toString()

                        keyword.isBlank() ||
                                lecturer.fullName.lowercase().contains(keyword) ||
                                lecturer.name.lowercase().contains(keyword) ||
                                lecturer.expertise.orEmpty().lowercase().contains(keyword) ||
                                quotaText.contains(keyword) ||
                                remainingQuota.contains(keyword) ||
                                if (lecturer.isActive) {
                                    "aktif".contains(keyword)
                                } else {
                                    "nonaktif".contains(keyword)
                                }
                    }

                    LecturerManagementSection(
                        lecturers = filteredLecturers,
                        isLoading = lecturerState.isLoading,
                        errorMessage = lecturerState.errorMessage,
                        onAddClick = {
                            showAddLecturerDialog = true
                        },
                        onEditQuotaClick = { lecturer ->
                            quotaEditLecturer = lecturer
                        },
                        onDeactivateClick = { lecturer ->
                            lecturerViewModel.deactivateLecturer(lecturer.id)
                        },
                        onReactivateClick = { lecturer ->
                            lecturerViewModel.reactivateLecturer(lecturer.id)
                        }
                    )
                }

                2 -> {
                    SearchBox(
                        value = trackingSearch,
                        onValueChange = {
                            trackingSearch = it
                        },
                        placeholder = "Cari nama, NIM, pembimbing, BAB, status..."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val filteredStudents = state.studentTrackings.filter { student ->
                        val keyword = trackingSearch.trim().lowercase()

                        keyword.isBlank() ||
                                student.studentName.lowercase().contains(keyword) ||
                                student.nim.lowercase().contains(keyword) ||
                                student.lecturerName.orEmpty().lowercase().contains(keyword) ||
                                student.thesisTitle.orEmpty().lowercase().contains(keyword) ||
                                student.progressText.lowercase().contains(keyword) ||
                                student.seminarProposalStatus.orEmpty().lowercase().contains(keyword) ||
                                student.revisiSeminarProposalStatus.orEmpty().lowercase().contains(keyword) ||
                                student.kolokiumStatus.orEmpty().lowercase().contains(keyword) ||
                                student.revisiKolokiumStatus.orEmpty().lowercase().contains(keyword) ||
                                student.yudisiumStatus.orEmpty().lowercase().contains(keyword)
                    }

                    StudentTrackingSection(
                        students = filteredStudents,
                        isLoading = state.isLoading
                    )
                }
            }
        }
    }

    if (showNotificationListDialog) {
        NotificationListDialog(
            pendingSubmissions = pendingSubmissions,
            onDismiss = {
                showNotificationListDialog = false
            },
            onOpenPengajuan = {
                showNotificationListDialog = false
                selectedTab = 0
                pengajuanFilter = "pending"
            }
        )
    }

    if (showOpenNotificationDialog) {
        MessageDialog(
            title = "Pengajuan Baru",
            message = "Ada $pendingCount pengajuan baru dari mahasiswa yang menunggu persetujuan.",
            isError = false,
            onDismiss = {
                showOpenNotificationDialog = false
                selectedTab = 0
                pengajuanFilter = "pending"
            }
        )
    }

    if (state.successMessage != null) {
        MessageDialog(
            title = "Berhasil",
            message = state.successMessage.orEmpty(),
            isError = false,
            onDismiss = {
                viewModel.clearMessage()
            }
        )
    }

    if (state.errorMessage != null) {
        MessageDialog(
            title = "Peringatan",
            message = state.errorMessage.orEmpty(),
            isError = true,
            onDismiss = {
                viewModel.clearMessage()
            }
        )
    }

    if (lecturerState.successMessage != null) {
        MessageDialog(
            title = "Berhasil",
            message = lecturerState.successMessage.orEmpty(),
            isError = false,
            onDismiss = {
                lecturerViewModel.clearMessage()
            }
        )
    }

    if (lecturerState.errorMessage != null) {
        MessageDialog(
            title = "Peringatan",
            message = lecturerState.errorMessage.orEmpty(),
            isError = true,
            onDismiss = {
                lecturerViewModel.clearMessage()
            }
        )
    }

    if (showAddLecturerDialog) {
        AddLecturerDialog(
            departmentId = kaprodiDepartmentId,
            onDismiss = {
                showAddLecturerDialog = false
            },
            onSave = { departmentId, name, title, expertise, quota ->
                lecturerViewModel.addLecturer(
                    departmentId = departmentId,
                    name = name,
                    title = title,
                    expertise = expertise,
                    quota = quota
                )
                showAddLecturerDialog = false
            }
        )
    }

    quotaEditLecturer?.let { lecturer ->
        EditQuotaDialog(
            lecturer = lecturer,
            onDismiss = {
                quotaEditLecturer = null
            },
            onSave = { quota, currentStudents ->
                lecturerViewModel.updateQuota(
                    lecturerId = lecturer.id,
                    quota = quota
                )

                lecturerViewModel.updateCurrentStudents(
                    lecturerId = lecturer.id,
                    currentStudents = currentStudents
                )

                quotaEditLecturer = null
            }
        )
    }
}