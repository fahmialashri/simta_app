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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.project.core.SimtaGreen
import com.project.core.SimtaRed
import com.project.data.model.ChapterSubmission
import com.project.data.model.DosenReviewItem

@Composable
internal fun ReviewCard(
    item: DosenReviewItem,
    onOpenUrl: (String) -> Unit,
    onApproveClick: (String?) -> Unit,
    onRevisionClick: (String?) -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRevisionDialog by remember { mutableStateOf(false) }
    var lecturerNote by remember { mutableStateOf("") }

    if (showApproveDialog) {
        NoteDialog(
            title = "Setujui BAB",
            description = "Tambahkan catatan persetujuan jika diperlukan.",
            confirmText = "Setujui",
            confirmColor = SimtaGreen,
            note = lecturerNote,
            onNoteChange = {
                lecturerNote = it
            },
            onDismiss = {
                showApproveDialog = false
                lecturerNote = ""
            },
            onConfirm = {
                showApproveDialog = false
                onApproveClick(
                    lecturerNote.trim().ifBlank {
                        null
                    }
                )
                lecturerNote = ""
            }
        )
    }

    if (showRevisionDialog) {
        NoteDialog(
            title = "Minta Revisi",
            description = "Berikan catatan revisi untuk mahasiswa.",
            confirmText = "Kirim Revisi",
            confirmColor = SimtaRed,
            note = lecturerNote,
            onNoteChange = {
                lecturerNote = it
            },
            onDismiss = {
                showRevisionDialog = false
                lecturerNote = ""
            },
            onConfirm = {
                showRevisionDialog = false
                onRevisionClick(
                    lecturerNote.trim().ifBlank {
                        null
                    }
                )
                lecturerNote = ""
            }
        )
    }

    ReviewContentCard(
        item = item,
        onOpenUrl = onOpenUrl,
        showActions = true,
        onRevisionClick = {
            showRevisionDialog = true
        },
        onApproveClick = {
            showApproveDialog = true
        }
    )
}

@Composable
internal fun HistoryCard(
    item: DosenReviewItem,
    onOpenUrl: (String) -> Unit
) {
    ReviewContentCard(
        item = item,
        onOpenUrl = onOpenUrl,
        showActions = false,
        onRevisionClick = {},
        onApproveClick = {}
    )
}

@Composable
private fun ReviewContentCard(
    item: DosenReviewItem,
    onOpenUrl: (String) -> Unit,
    showActions: Boolean,
    onRevisionClick: () -> Unit,
    onApproveClick: () -> Unit
) {
    val chapterStatus = item.chapter.status

    val statusLabel = when (chapterStatus) {
        "approved" -> "Disetujui"
        "revision" -> "Revisi"
        "process" -> "Menunggu Review"
        else -> "Belum"
    }

    val statusColor = when (chapterStatus) {
        "approved" -> SimtaGreen
        "revision" -> Color(0xFFFFC247)
        "process" -> SimtaRed
        else -> Color.Gray
    }

    val chapterTitle = item.chapter.title ?: "Judul BAB belum diisi"
    val thesisTitle = item.request.title ?: "Judul skripsi belum diisi"
    val studentName = item.student?.fullName ?: "Mahasiswa"
    val studentNim = item.student?.nim ?: "-"
    val lecturerNote = item.chapter.lecturerNote

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SimtaRed.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = SimtaRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SimtaRed.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BAB ${item.chapter.chapterNumber}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SimtaRed
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(statusColor)
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = chapterTitle,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$studentName • $studentNim",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = Color(0xFFF1F3F4),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Judul Skripsi:",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = thesisTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!lecturerNote.isNullOrBlank()) {
                Text(
                    text = "Catatan Dosen:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF8E1))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Komentar Dosen",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFF57F17)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = lecturerNote,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = if (showActions) {
                    "Dokumen yang Dikirim Mahasiswa:"
                } else {
                    "Riwayat Dokumen Mahasiswa:"
                },
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (item.submissions.isEmpty()) {
                Text(
                    text = "Belum ada file/link yang dikirim.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                item.submissions.forEachIndexed { index, submission ->
                    SubmissionHistoryItem(
                        index = index + 1,
                        submission = submission,
                        onOpenUrl = onOpenUrl
                    )

                    if (index != item.submissions.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            if (showActions) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onRevisionClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SimtaRed
                        )
                    ) {
                        Text(
                            text = "Minta Revisi",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SimtaGreen
                        )
                    ) {
                        Text(
                            text = "Setujui",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubmissionHistoryItem(
    index: Int,
    submission: ChapterSubmission,
    onOpenUrl: (String) -> Unit
) {
    val fileName = submission.fileName ?: "Dokumen Mahasiswa"
    val fileUrl = submission.fileUrl
    val driveUrl = submission.driveUrl
    val note = submission.note

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F9FA)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(SimtaRed.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$index",
                        color = SimtaRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = fileName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color.Black
                    )

                    Text(
                        text = "Riwayat kiriman mahasiswa",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            if (!fileUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                ClickableFileItem(
                    iconType = "file",
                    label = submission.fileName ?: "Download Dokumen",
                    onClick = {
                        onOpenUrl(fileUrl)
                    }
                )
            }

            if (!driveUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                ClickableFileItem(
                    iconType = "link",
                    label = "Buka Google Drive",
                    onClick = {
                        onOpenUrl(driveUrl)
                    }
                )
            }

            if (!note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Pesan/Catatan Mahasiswa:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = note,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClickableFileItem(
    iconType: String,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF1F3F4))
            .clickable {
                onClick()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (iconType == "file") {
                Icons.Default.CloudDownload
            } else {
                Icons.Default.OpenInNew
            },
            contentDescription = null,
            tint = SimtaRed,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
    }
}