package com.project.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.R // Pastikan ini mengarah ke R package project lu
import com.project.core.SimtaRed
import com.project.core.SimtaYellow

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // Background abu-abu muda biar clean
    ) {
        // Konten Atas (Logo & Judul)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 240.dp), // Dikasih jarak bawah biar posisinya pas di tengah area yang kosong, gak ketutup Card bawah
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- TEMPAT LOGO DI SINI ---
            Image(
                painter = painterResource(id = R.drawable.logo_simta), // Ganti dengan nama file logo lu, misal: R.drawable.logo_simta
                contentDescription = "Logo SIMTA",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SIMTA",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = SimtaRed, // Teks SIMTA dibikin merah biar identitasnya kuat
                letterSpacing = 8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sistem Informasi Manajemen Tugas Akhir",
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom Sheet Card (Tombol Mulai)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(240.dp), // Ditinggiin dikit biar lebih proporsional
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = SimtaRed
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "WELCOME!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Kelola pengajuan skripsi dan bimbingan dengan dosen secara mudah dalam satu aplikasi.",
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaYellow
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp) // Tombolnya dibikin lebih tebel biar enak di-klik
                ) {
                    Text(
                        text = "Mulai Sekarang!",
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}