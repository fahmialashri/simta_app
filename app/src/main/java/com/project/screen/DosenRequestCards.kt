package com.project.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.core.SimtaGreen
import com.project.core.SimtaRed
import com.project.data.repository.KaprodiSubmissionData

@Composable
internal fun RequestCard(
    item: KaprodiSubmissionData,
    onAcceptClick: () -> Unit,
    onRejectClick: (String?) -> Unit
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectNote by remember { mutableStateOf("") }

    if (showRejectDialog) {
        NoteDialog(
            title = "Tolak Pengajuan",
            description = "Berikan alasan penolakan untuk mahasiswa.",
            confirmText = "Tolak",
            confirmColor = SimtaRed,
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
                onRejectClick(
                    rejectNote.trim().ifBlank {
                        null
                    }
                )
                rejectNote = ""
            }
        )
    }

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
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F3F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = item.studentName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "NIM: ${item.nim}",
                        fontSize = 12.sp,
                        color = Color.Gray
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
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Dosen Pembimbing yang Dipilih:",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = SimtaRed,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = item.lecturerName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Status:",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFA000)
            )

            if (!item.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8F9FA))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Catatan:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.note,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        showRejectDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaRed.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "Tolak",
                        color = SimtaRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onAcceptClick,
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
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun NoteDialog(
    title: String,
    description: String,
    confirmText: String,
    confirmColor: Color,
    note: String,
    onNoteChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    label = {
                        Text("Catatan")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SimtaRed,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmColor
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = confirmText,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "Batal",
                    color = Color.Gray
                )
            }
        }
    )
}