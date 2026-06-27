package com.project.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GuidanceReminderSection(
    daysSinceLastGuidance: Int?,
    onScheduleClick: () -> Unit = {}
) {

    val days = daysSinceLastGuidance ?: 0

    val level = when {
        days >= 21 -> 3
        days >= 14 -> 2
        days >= 7 -> 1
        else -> 0
    }

    val title: String
    val message: String
    val color: Color
    val progress: Float

    when (level) {

        3 -> {
            title = "Peringatan Terakhir"
            message =
                "Sudah $days hari sejak bimbingan terakhir. Segera lakukan bimbingan agar progres TA tidak terhambat."
            color = Color(0xFFD32F2F)
            progress = 1f
        }

        2 -> {
            title = "Pengingat Kedua"
            message =
                "Sudah $days hari belum melakukan bimbingan. Jangan sampai progres skripsi tertunda."
            color = Color(0xFFFF9800)
            progress = 0.66f
        }

        1 -> {
            title = "Pengingat Pertama"
            message =
                "Sudah $days hari sejak bimbingan terakhir. Yuk segera hubungi dosen pembimbing."
            color = Color(0xFFFFC107)
            progress = 0.33f
        }

        else -> {
            title = "Bimbingan Aman"
            message =
                "Kamu masih aktif melakukan bimbingan. Pertahankan progres penyusunan TA."
            color = Color(0xFF4CAF50)
            progress = 0f
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
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
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = color
                    )

                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {

                    Text(
                        text = "Pengingat Bimbingan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Text(
                        text = title,
                        color = color,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                }

            }

            Spacer(modifier = Modifier.height(20.dp))

            if (days > 0) {

                Text(
                    text = "$days Hari",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Sejak bimbingan terakhir",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

            }

            Spacer(modifier = Modifier.height(18.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = color
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = message,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .clickable {
                        onScheduleClick()
                    }
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