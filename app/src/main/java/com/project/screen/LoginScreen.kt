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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.R
import com.project.auth.AuthViewModel
import com.project.component.SimtaButton
import com.project.core.SimtaRed
import com.project.navigation.Screen

@Composable
fun LoginScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val state by authViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedIn, state.role) {
        if (state.isLoggedIn) {
            when (state.role?.trim()?.lowercase()) {
                "mahasiswa" -> {
                    navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                "dosen" -> {
                    navController.navigate(Screen.DosenDashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                "kaprodi" -> {
                    navController.navigate(Screen.KaprodiDashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                "tu" -> {
                    navController.navigate(Screen.TuDashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                else -> {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SimtaRed,
                            SimtaRed.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = 48.dp,
                        bottomEnd = 48.dp
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_simta),
                    contentDescription = "Logo",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SIMTA",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = 6.sp
            )

            Text(
                text = "Sistem Informasi Tugas Akhir",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                        text = "Selamat Datang!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Silakan masuk ke akun Anda",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            if (state.errorMessage != null) {
                                authViewModel.clearError()
                            }
                        },
                        label = {
                            Text("Alamat Email")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
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

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (state.errorMessage != null) {
                                authViewModel.clearError()
                            }
                        },
                        label = {
                            Text("Kata Sandi")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        isError = state.errorMessage != null,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            }

                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (passwordVisible) {
                                        "Sembunyikan password"
                                    } else {
                                        "Tampilkan password"
                                    },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Lupa Kata Sandi?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SimtaRed,
                            modifier = Modifier.clickable {
                                // TODO: Handle lupa password
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    SimtaButton(
                        text = if (state.isLoading) {
                            "Memproses..."
                        } else {
                            "Masuk"
                        },
                        enabled = !state.isLoading
                    ) {
                        authViewModel.login(
                            email = email.trim(),
                            password = password
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Belum punya akun? ",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = "Daftar di sini",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SimtaRed,
                            modifier = Modifier.clickable {
                                authViewModel.clearError()
                                navController.navigate(Screen.Register.route)
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                authViewModel.clearError()
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = SimtaRed,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Login Gagal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            text = {
                Text(
                    text = state.errorMessage ?: "Terjadi kesalahan",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        authViewModel.clearError()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimtaRed
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Tutup",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}