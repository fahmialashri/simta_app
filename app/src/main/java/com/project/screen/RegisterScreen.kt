package com.project.screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.R
import com.project.auth.AuthViewModel
import com.project.component.SimtaButton
import com.project.core.SimtaRed
import com.project.navigation.Screen

@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val state by authViewModel.uiState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn, state.role) {
        if (state.isLoggedIn) {
            when (state.role) {
                "dosen" -> navController.navigate(Screen.DosenDashboard.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }

                else -> navController.navigate(Screen.MahasiswaDashboard.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Dekorasi Header Melengkung
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SimtaRed, SimtaRed.copy(alpha = 0.8f))
                    ),
                    shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Scroll ditaruh di sini biar seluruh layar bisa di-scroll
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            // Logo / Icon Aplikasi
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_simta),
                    contentDescription = "Logo SIMTA",
                    modifier = Modifier.size(60.dp) // Disesuaikan ukurannya biar pas di dalam kotak
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Judul Aplikasi
            Text(
                text = "Daftar Akun",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = 2.sp
            )

            Text(
                text = "Sistem Informasi Tugas Akhir",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Floating Card Form Register
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mahasiswa Baru",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Akun dosen hanya dibuat oleh admin kampus",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Field Nama Lengkap
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            if (state.errorMessage != null) authViewModel.clearError()
                        },
                        label = { Text("Nama Lengkap") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        isError = state.errorMessage != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SimtaRed,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = SimtaRed,
                            errorBorderColor = SimtaRed,
                            errorLabelColor = SimtaRed
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (state.errorMessage != null) authViewModel.clearError()
                        },
                        label = { Text("Alamat Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = state.errorMessage != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SimtaRed,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = SimtaRed,
                            errorBorderColor = SimtaRed,
                            errorLabelColor = SimtaRed
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field NIM
                    OutlinedTextField(
                        value = nim,
                        onValueChange = {
                            nim = it
                            if (state.errorMessage != null) authViewModel.clearError()
                        },
                        label = { Text("NIM Mahasiswa") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = state.errorMessage != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SimtaRed,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = SimtaRed,
                            errorBorderColor = SimtaRed,
                            errorLabelColor = SimtaRed
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Field Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (state.errorMessage != null) authViewModel.clearError()
                        },
                        label = { Text("Kata Sandi") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = state.errorMessage != null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SimtaRed,
                            unfocusedBorderColor = Color.LightGray,
                            focusedLabelColor = SimtaRed,
                            errorBorderColor = SimtaRed,
                            errorLabelColor = SimtaRed
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SimtaButton(
                        text = if (state.isLoading) "Memproses..." else "Daftar Sekarang",
                        enabled = !state.isLoading
                    ) {
                        authViewModel.registerMahasiswa(
                            fullName = fullName.trim(),
                            email = email.trim(),
                            password = password,
                            nim = nim.trim()
                        )
                    }

                    // Tampilan Error Message sebaris (opsional, karena udah ada alert dialog)
                    // Tapi dipertahankan biar konsisten kayak login
                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.errorMessage!!,
                                color = SimtaRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Link Balik ke Login
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sudah punya akun? ",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Masuk di sini",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SimtaRed,
                            modifier = Modifier.clickable {
                                authViewModel.clearError()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Padding bawah biar nggak mentok banget pas di scroll
        }
    }

    // Alert Dialog pop-up kalau Register gagal
    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { authViewModel.clearError() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = SimtaRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pendaftaran Gagal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            text = {
                Text(
                    text = state.errorMessage!!,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { authViewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(containerColor = SimtaRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Tutup", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}