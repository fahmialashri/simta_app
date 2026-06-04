package com.project.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.project.core.SimtaRed
import com.project.data.model.Lecturer
import com.project.data.repository.KaprodiLecturerRecommendation
import com.project.data.repository.KaprodiSubmissionData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddLecturerDialog(
    departmentId: Long,
    onDismiss: () -> Unit,
    onSave: (
        departmentId: Long,
        name: String,
        title: String?,
        expertise: String?,
        quota: Int
    ) -> Unit
) {
    var name by remember {
        mutableStateOf("")
    }

    var title by remember {
        mutableStateOf("")
    }

    var expertise by remember {
        mutableStateOf("")
    }

    var quotaText by remember {
        mutableStateOf("5")
    }

    var expertiseExpanded by remember {
        mutableStateOf(false)
    }

    val expertiseOptions = remember(departmentId) {
        when (departmentId) {
            1L -> listOf(
                "Software Engineering",
                "Data Science",
                "Artificial Intelligence and Robotics",
                "Computer Network & Security"
            )

            else -> listOf(
                "Data dan Informasi Manajemen",
                "Infrastruktur dan Sistem Teknologi Informasi"
            )
        }
    }

    val isValid =
        name.isNotBlank() &&
                expertise.isNotBlank() &&
                (quotaText.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Dosen",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Department ID: $departmentId",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Nama Dosen")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("Gelar")
                    },
                    placeholder = {
                        Text("Contoh: M.Kom / Ph.D")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expertiseExpanded,
                    onExpandedChange = {
                        expertiseExpanded = !expertiseExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = expertise,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Expertise / Bidang Keahlian")
                        },
                        placeholder = {
                            Text("Pilih expertise dosen")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expertiseExpanded
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expertiseExpanded,
                        onDismissRequest = {
                            expertiseExpanded = false
                        }
                    ) {
                        expertiseOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(option)
                                },
                                onClick = {
                                    expertise = option
                                    expertiseExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quotaText,
                    onValueChange = {
                        quotaText = it
                    },
                    label = {
                        Text("Kuota")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                if (!isValid) {
                    Text(
                        text = "Nama dosen, expertise, dan kuota wajib diisi.",
                        fontSize = 11.sp,
                        color = SimtaRed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        departmentId,
                        name.trim(),
                        title.trim().ifBlank { null },
                        expertise.trim(),
                        quotaText.toIntOrNull() ?: 5
                    )
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SimtaRed,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Simpan",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
internal fun EditQuotaDialog(
    lecturer: Lecturer,
    onDismiss: () -> Unit,
    onSave: (
        quota: Int,
        currentStudents: Int
    ) -> Unit
) {
    var quotaText by remember {
        mutableStateOf(lecturer.quota.toString())
    }

    var currentStudentsText by remember {
        mutableStateOf(lecturer.currentStudents.toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Kuota Dosen",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = lecturer.fullName,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                OutlinedTextField(
                    value = quotaText,
                    onValueChange = {
                        quotaText = it
                    },
                    label = {
                        Text("Kuota Maksimal")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = currentStudentsText,
                    onValueChange = {
                        currentStudentsText = it
                    },
                    label = {
                        Text("Jumlah Mahasiswa Saat Ini")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        quotaText.toIntOrNull() ?: lecturer.quota,
                        currentStudentsText.toIntOrNull() ?: lecturer.currentStudents
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SimtaRed
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Simpan",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
internal fun RejectReasonDialog(
    note: String,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Alasan Penolakan",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Text(
                    text = "Tulis alasan penolakan agar mahasiswa bisa melihat alasan pengajuannya ditolak.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = {
                        Text("Alasan penolakan")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Batal",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SimtaRed
                        )
                    ) {
                        Text(
                            text = "Tolak",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ConfirmArchiveDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Batal",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF455A64)
                        )
                    ) {
                        Text(
                            text = "Hapus",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun NotificationListDialog(
    pendingSubmissions: List<KaprodiSubmissionData>,
    onDismiss: () -> Unit,
    onOpenPengajuan: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Notifikasi",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                if (pendingSubmissions.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8F9FA)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Belum ada notifikasi pengajuan baru.",
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )
                    }
                } else {
                    Text(
                        text = "Ada ${pendingSubmissions.size} pengajuan baru:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 260.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(pendingSubmissions) { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8F9FA)
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = item.studentName,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontSize = 13.sp
                                    )

                                    Text(
                                        text = "${item.nim} • ${item.lecturerName}",
                                        color = Color.DarkGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Tutup",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onOpenPengajuan,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SimtaRed
                        )
                    ) {
                        Text(
                            text = "Lihat",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun SubmissionDetailDialog(
    item: KaprodiSubmissionData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    fun openUrl(url: String?) {
        if (!url.isNullOrBlank()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Cek Bukti KRS",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F9FA)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = item.studentName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )

                        Text(
                            text = "NIM: ${item.nim}",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "Judul: ${item.title ?: "-"}",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.krsFileUrl.isNullOrBlank()) {
                            Color(0xFFFFF3F3)
                        } else {
                            Color(0xFFF7FFF8)
                        }
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (item.krsFileUrl.isNullOrBlank()) {
                            SimtaRed.copy(alpha = 0.25f)
                        } else {
                            Color(0xFF2E7D32).copy(alpha = 0.25f)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = if (item.krsFileUrl.isNullOrBlank()) {
                                        SimtaRed.copy(alpha = 0.10f)
                                    } else {
                                        Color(0xFF2E7D32).copy(alpha = 0.10f)
                                    },
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (item.krsFileUrl.isNullOrBlank()) {
                                    Icons.Default.Warning
                                } else {
                                    Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = if (item.krsFileUrl.isNullOrBlank()) {
                                    SimtaRed
                                } else {
                                    Color(0xFF2E7D32)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.size(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Bukti KRS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black
                            )

                            Text(
                                text = if (item.krsFileUrl.isNullOrBlank()) {
                                    "Belum tersedia"
                                } else {
                                    "File tersedia"
                                },
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        openUrl(item.krsFileUrl)
                    },
                    enabled = !item.krsFileUrl.isNullOrBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaRed,
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = if (item.krsFileUrl.isNullOrBlank()) {
                            "Bukti KRS Belum Tersedia"
                        } else {
                            "Buka Bukti KRS"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Tutup",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun LecturerRecommendationDialog(
    lecturers: List<KaprodiLecturerRecommendation>,
    selectedRecommendationId: Long?,
    existingNote: String?,
    onDismiss: () -> Unit,
    onSave: (Long, String?) -> Unit
) {
    var selectedLecturerId by remember {
        mutableStateOf(selectedRecommendationId ?: 0L)
    }

    var note by remember {
        mutableStateOf(existingNote.orEmpty())
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Pilih Rekomendasi Dosen",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    lineHeight = 26.sp
                )

                Text(
                    text = "Pilih dosen yang paling sesuai untuk mahasiswa ini.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )

                if (lecturers.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3F3)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = SimtaRed.copy(alpha = 0.25f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = SimtaRed
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Text(
                                text = "Belum ada dosen rekomendasi yang tersedia.",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(lecturers) { lecturer ->
                            val selected = selectedLecturerId == lecturer.id

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLecturerId = lecturer.id
                                    },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selected) {
                                        SimtaRed.copy(alpha = 0.08f)
                                    } else {
                                        Color(0xFFF8F9FA)
                                    }
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selected) {
                                        SimtaRed
                                    } else {
                                        Color.LightGray.copy(alpha = 0.7f)
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (selected) 2.dp else 0.dp
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(
                                                color = if (selected) {
                                                    SimtaRed.copy(alpha = 0.12f)
                                                } else {
                                                    Color.White
                                                },
                                                shape = RoundedCornerShape(14.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (selected) {
                                                Icons.Default.CheckCircle
                                            } else {
                                                Icons.Default.Person
                                            },
                                            contentDescription = null,
                                            tint = if (selected) {
                                                SimtaRed
                                            } else {
                                                Color.DarkGray
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.size(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = lecturer.fullName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black,
                                            lineHeight = 17.sp
                                        )

                                        Text(
                                            text = lecturer.expertise ?: "Expertise belum diisi",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray,
                                            lineHeight = 15.sp
                                        )

                                        Text(
                                            text = "Sisa kuota ${lecturer.remainingQuota} dari ${lecturer.quota}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (lecturer.remainingQuota > 0) {
                                                Color(0xFF2E7D32)
                                            } else {
                                                SimtaRed
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = note,
                        onValueChange = {
                            note = it
                        },
                        label = {
                            Text("Catatan rekomendasi")
                        },
                        placeholder = {
                            Text("Opsional")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3,
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Batal",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (selectedLecturerId != 0L) {
                                onSave(
                                    selectedLecturerId,
                                    note.trim().ifBlank { null }
                                )
                            }
                        },
                        enabled = selectedLecturerId != 0L,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SimtaRed,
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Simpan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun MessageDialog(
    title: String,
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isError) SimtaRed else Color(0xFF2E7D32)
                    )
                ) {
                    Text(
                        text = "Tutup",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}