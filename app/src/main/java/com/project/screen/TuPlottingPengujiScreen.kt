package com.project.screen

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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.core.SimtaRed

data class LecturerOptionUi(
    val id: String,
    val name: String
)

data class ExaminerInputUi(
    val order: Int,
    var lecturerId: String = "",
    var lecturerName: String = "",
    var willBeSupervisor: Boolean = false,
    var supervisorOrder: Int? = null
)

@Composable
fun TuPlottingPengujiScreen(
    navController: NavHostController,
    stageId: String
) {
    val lecturers = remember {
        listOf(
            LecturerOptionUi("1", "Dr. Budi Santoso, M.Kom"),
            LecturerOptionUi("2", "Dr. Rina Marlina, M.Kom"),
            LecturerOptionUi("3", "Dr. Ahmad Hidayat, M.T"),
            LecturerOptionUi("4", "Dr. Sari Permata, M.Kom")
        )
    }

    val examiners = remember {
        mutableStateListOf(
            ExaminerInputUi(order = 1),
            ExaminerInputUi(order = 2)
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        bottomBar = {
            Button(
                onClick = {
                    // Nanti sambungkan ke Supabase:
                    // viewModel.saveExaminerPlots(stageId, examiners)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SimtaRed
                )
            ) {
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
                        text = "Atur penguji dan pembimbing tambahan",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            InfoPlottingCard()

            Spacer(modifier = Modifier.height(16.dp))

            examiners.forEachIndexed { index, item ->
                ExaminerFormCard(
                    item = item,
                    lecturers = lecturers,
                    onChange = { updated ->
                        examiners[index] = updated
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            Button(
                onClick = {
                    examiners.add(
                        ExaminerInputUi(order = examiners.size + 1)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Tambah Penguji",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InfoPlottingCard() {
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
                text = "Seminar Proposal: penguji bisa menjadi pembimbing 2. Kolokium: penguji bisa menjadi pembimbing 3 dan 4.",
                color = Color.White.copy(alpha = 0.92f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExaminerFormCard(
    item: ExaminerInputUi,
    lecturers: List<LecturerOptionUi>,
    onChange: (ExaminerInputUi) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Penguji ${item.order}",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = item.lecturerName,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Pilih Dosen")
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
                                Text(lecturer.name)
                            },
                            onClick = {
                                onChange(
                                    item.copy(
                                        lecturerId = lecturer.id,
                                        lecturerName = lecturer.name
                                    )
                                )
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.willBeSupervisor,
                    onCheckedChange = { checked ->
                        onChange(
                            item.copy(
                                willBeSupervisor = checked,
                                supervisorOrder = if (checked) 2 else null
                            )
                        )
                    }
                )

                Text(
                    text = "Jadikan pembimbing tambahan",
                    fontSize = 13.sp,
                    color = Color.Black
                )
            }

            if (item.willBeSupervisor) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SupervisorOrderButton(
                        label = "Pembimbing 2",
                        selected = item.supervisorOrder == 2,
                        onClick = {
                            onChange(item.copy(supervisorOrder = 2))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    SupervisorOrderButton(
                        label = "Pembimbing 3",
                        selected = item.supervisorOrder == 3,
                        onClick = {
                            onChange(item.copy(supervisorOrder = 3))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                SupervisorOrderButton(
                    label = "Pembimbing 4",
                    selected = item.supervisorOrder == 4,
                    onClick = {
                        onChange(item.copy(supervisorOrder = 4))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SupervisorOrderButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) SimtaRed else Color(0xFFE0E0E0)
        )
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}