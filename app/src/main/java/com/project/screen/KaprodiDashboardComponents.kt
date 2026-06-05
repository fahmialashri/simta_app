package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.core.SimtaRed
import com.project.data.model.Lecturer
import com.project.data.repository.KaprodiStudentTrackingData
import com.project.data.repository.KaprodiSubmissionData
import com.project.kaprodi.KaprodiUiState

@Composable
internal fun KaprodiRequestSection(
    state: KaprodiUiState,
    onApprove: (Long) -> Unit,
    onReject: (Long, String?) -> Unit,
    onArchive: (Long) -> Unit,
    onRecommendLecturer: (Long, Long, String?) -> Unit
) {
    when {
        state.isLoading -> {
            LoadingBox()
        }

        state.submissions.isEmpty() -> {
            KaprodiMessageCard(
                message = "Belum ada data pengajuan yang cocok.",
                isError = false
            )
        }

        else -> {
            state.submissions.forEach { item ->
                KaprodiSubmissionCard(
                    item = item,
                    onApprove = {
                        onApprove(item.id)
                    },
                    onReject = { note ->
                        onReject(item.id, note)
                    },
                    onArchive = {
                        onArchive(item.id)
                    },
                    onRecommendLecturer = { lecturerId, note ->
                        onRecommendLecturer(item.id, lecturerId, note)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
internal fun KaprodiSubmissionCard(
    item: KaprodiSubmissionData,
    onApprove: () -> Unit,
    onReject: (String?) -> Unit,
    onArchive: () -> Unit,
    onRecommendLecturer: (Long, String?) -> Unit
) {
    var showRejectDialog by remember {
        mutableStateOf(false)
    }

    var rejectNote by remember {
        mutableStateOf("")
    }

    var showArchiveConfirm by remember {
        mutableStateOf(false)
    }

    var showDetailDialog by remember {
        mutableStateOf(false)
    }

    var showRecommendationDialog by remember {
        mutableStateOf(false)
    }

    val normalizedStatus = item.status.lowercase()

    val statusText = when (normalizedStatus) {
        "pending" -> "Menunggu Persetujuan"
        "accepted" -> "Disetujui"
        "rejected" -> "Ditolak"
        else -> item.status
    }

    val statusColor = when (normalizedStatus) {
        "pending" -> Color(0xFFFFA000)
        "accepted" -> Color(0xFF2E7D32)
        "rejected" -> SimtaRed
        else -> Color.DarkGray
    }

    if (showRejectDialog) {
        RejectReasonDialog(
            note = rejectNote,
            onNoteChange = {
                rejectNote = it
            },
            onDismiss = {
                showRejectDialog = false
                rejectNote = ""
            },
            onConfirm = {
                showRejectDialog = false
                onReject(rejectNote.trim().ifBlank { null })
                rejectNote = ""
            }
        )
    }

    if (showArchiveConfirm && normalizedStatus != "pending") {
        ConfirmArchiveDialog(
            title = "Hapus History",
            message = "History pengajuan ini akan dihapus dari tampilan Kaprodi. Data tidak dihapus permanen, hanya diarsipkan.",
            onDismiss = {
                showArchiveConfirm = false
            },
            onConfirm = {
                showArchiveConfirm = false
                onArchive()
            }
        )
    }

    if (showDetailDialog) {
        SubmissionDetailDialog(
            item = item,
            onDismiss = {
                showDetailDialog = false
            }
        )
    }

    if (showRecommendationDialog) {
        LecturerRecommendationDialog(
            lecturers = item.recommendedLecturers,
            selectedRecommendationId = item.recommendedLecturerId,
            existingNote = item.recommendationNote,
            onDismiss = {
                showRecommendationDialog = false
            },
            onSave = { lecturerId, note ->
                showRecommendationDialog = false
                onRecommendLecturer(lecturerId, note)
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = statusColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = statusColor
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.studentName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Text(
                        text = item.nim,
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.title.orEmpty().ifBlank { "Judul belum tersedia" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (!item.topic.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Topik: ${item.topic}",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Dosen pilihan: ${item.lecturerName}",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            if (!item.recommendedLecturerName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rekomendasi Kaprodi: ${item.recommendedLecturerName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SimtaRed
                )
            }

            Text(
                text = "Kuota: ${item.lecturerCurrentStudents}/${item.lecturerQuota}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.lecturerIsFull) SimtaRed else Color(0xFF2E7D32)
            )

            Text(
                text = "Sisa kuota: ${item.lecturerRemainingQuota.coerceAtLeast(0)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.lecturerIsFull) SimtaRed else Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(10.dp))

            StatusPill(
                text = statusText,
                color = statusColor
            )

            if (!item.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                KaprodiMessageCard(
                    message = "Catatan: ${item.note}",
                    isError = normalizedStatus == "rejected"
                )
            }

            if (!item.recommendationNote.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                KaprodiMessageCard(
                    message = "Catatan rekomendasi: ${item.recommendationNote}",
                    isError = false
                )
            }

            if (item.lecturerIsFull && normalizedStatus == "pending") {
                Spacer(modifier = Modifier.height(10.dp))

                KaprodiMessageCard(
                    message = "Kuota dosen pilihan penuh. Kaprodi bisa memilih dosen rekomendasi lain.",
                    isError = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = {
                    showDetailDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = SimtaRed,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = "Cek Bukti KRS",
                    color = SimtaRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    showRecommendationDialog = true
                },
                enabled = normalizedStatus == "pending",
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF455A64),
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.size(6.dp))

                Text(
                    text = "Pilih / Rekomendasikan Dosen",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (normalizedStatus == "pending") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showRejectDialog = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Tolak",
                            color = SimtaRed,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SimtaRed
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.size(6.dp))

                        Text(
                            text = "Setujui",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                KaprodiMessageCard(
                    message = "Pengajuan ini sudah masuk history dengan status: $statusText.",
                    isError = normalizedStatus == "rejected"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        showArchiveConfirm = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF455A64)
                    )
                ) {
                    Text(
                        text = "Hapus dari Tampilan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun LecturerManagementSection(
    lecturers: List<Lecturer>,
    isLoading: Boolean,
    errorMessage: String?,
    onAddClick: () -> Unit,
    onEditQuotaClick: (Lecturer) -> Unit,
    onDeactivateClick: (Lecturer) -> Unit,
    onReactivateClick: (Lecturer) -> Unit
) {
    Button(
        onClick = onAddClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SimtaRed)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = Color.White
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "Tambah Dosen",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    when {
        isLoading -> {
            LoadingBox()
        }

        errorMessage != null -> {
            KaprodiMessageCard(
                message = errorMessage,
                isError = true
            )
        }

        lecturers.isEmpty() -> {
            KaprodiMessageCard(
                message = "Belum ada data dosen yang cocok.",
                isError = false
            )
        }

        else -> {
            lecturers.forEach { lecturer ->
                LecturerCard(
                    lecturer = lecturer,
                    onEditQuotaClick = {
                        onEditQuotaClick(lecturer)
                    },
                    onDeactivateClick = {
                        onDeactivateClick(lecturer)
                    },
                    onReactivateClick = {
                        onReactivateClick(lecturer)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
internal fun LecturerCard(
    lecturer: Lecturer,
    onEditQuotaClick: () -> Unit,
    onDeactivateClick: () -> Unit,
    onReactivateClick: () -> Unit
) {
    val remainingQuota = (lecturer.quota - lecturer.currentStudents).coerceAtLeast(0)

    val quotaColor = if (lecturer.currentStudents >= lecturer.quota) {
        SimtaRed
    } else {
        Color(0xFF2E7D32)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (lecturer.isActive) Color.White else Color(0xFFF0F0F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = lecturer.fullName,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = lecturer.expertise ?: "Keahlian belum diisi",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kuota: ${lecturer.currentStudents}/${lecturer.quota}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = quotaColor
            )

            Text(
                text = "Sisa kuota: $remainingQuota",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = quotaColor
            )

            Text(
                text = if (lecturer.isActive) "Status: Aktif" else "Status: Nonaktif",
                fontSize = 11.sp,
                color = if (lecturer.isActive) Color(0xFF2E7D32) else SimtaRed
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditQuotaClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = SimtaRed,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    Text(
                        text = "Kuota",
                        color = SimtaRed
                    )
                }

                if (lecturer.isActive) {
                    Button(
                        onClick = onDeactivateClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SimtaRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.size(5.dp))

                        Text(
                            text = "Hapus",
                            color = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = onReactivateClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.size(5.dp))

                        Text(
                            text = "Aktifkan",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun StudentTrackingSection(
    students: List<KaprodiStudentTrackingData>,
    isLoading: Boolean
) {
    when {
        isLoading -> {
            LoadingBox()
        }

        students.isEmpty() -> {
            KaprodiMessageCard(
                message = "Belum ada data tracking mahasiswa yang cocok.",
                isError = false
            )
        }

        else -> {
            students.forEach { student ->
                StudentTrackingCard(student)

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
internal fun StudentTrackingCard(
    student: KaprodiStudentTrackingData
) {
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
                text = student.studentName,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "NIM: ${student.nim}",
                fontSize = 11.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Judul: ${student.thesisTitle ?: "Belum ada judul diterima"}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Pembimbing: ${student.lecturerName ?: "-"}",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = student.progressText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (student.latestChapter <= 0) SimtaRed else Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.height(10.dp))

            TrackingStatusRow("Seminar Proposal", student.seminarProposalStatus)
            TrackingStatusRow("Revisi Seminar Proposal", student.revisiSeminarProposalStatus)
            TrackingStatusRow("Kolokium", student.kolokiumStatus)
            TrackingStatusRow("Revisi Kolokium", student.revisiKolokiumStatus)
            TrackingStatusRow("Yudisium", student.yudisiumStatus)
        }
    }
}

@Composable
internal fun TrackingStatusRow(
    label: String,
    status: String?
) {
    val cleanStatus = status ?: "belum_daftar"

    val statusText = when (cleanStatus) {
        "menunggu_review" -> "Menunggu Review"
        "disetujui_tu" -> "Disetujui TU"
        "ditolak_tu" -> "Ditolak TU"
        "accepted" -> "Diterima"
        "rejected" -> "Ditolak"
        "pending" -> "Pending"
        else -> "Belum Daftar"
    }

    val statusColor = when (cleanStatus) {
        "menunggu_review", "pending" -> Color(0xFFFFA000)
        "disetujui_tu", "accepted" -> Color(0xFF2E7D32)
        "ditolak_tu", "rejected" -> SimtaRed
        else -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.DarkGray
        )

        Text(
            text = statusText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}

@Composable
internal fun KaprodiStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(92.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = color.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Column {
                Text(
                    text = value,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
internal fun KaprodiMessageCard(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEBEE) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isError) Icons.Default.Warning else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isError) SimtaRed else Color(0xFF2E7D32)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = message,
                fontSize = 12.sp,
                color = Color.DarkGray,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
internal fun LoadingBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = SimtaRed)
    }
}

@Composable
internal fun SearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 12.sp,
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SimtaRed,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
internal fun StatusPill(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(100.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}