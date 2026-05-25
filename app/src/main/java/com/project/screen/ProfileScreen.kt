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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.navigation.Screen

@Composable
fun ProfileScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsState()

    val roleLabel = when (authState.role) {
        "dosen" -> "Dosen"
        "admin" -> "Admin"
        else -> "Mahasiswa"
    }

    val identityLabel = when (authState.role) {
        "dosen" -> "NIDN"
        else -> "NIM"
    }

    val identityValue = when (authState.role) {
        "dosen" -> authState.nidn ?: "-"
        else -> authState.nim ?: "-"
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA), // Background abu-abu muda
        bottomBar = {
            ProfileBottomNavigation(
                onHomeClick = {
                    when (authState.role) {
                        "dosen" -> navController.navigate(Screen.DosenDashboard.route) {
                            popUpTo(Screen.DosenDashboard.route) { inclusive = true }
                        }
                        else -> navController.navigate(Screen.MahasiswaDashboard.route) {
                            popUpTo(Screen.MahasiswaDashboard.route) { inclusive = true }
                        }
                    }
                },
                onPengajuanClick = {
                    if (authState.role != "dosen") {
                        navController.navigate(Screen.Pengajuan.route) {
                            popUpTo(Screen.MahasiswaDashboard.route)
                        }
                    }
                },
                onBimbinganClick = {
                    if (authState.role != "dosen") {
                        navController.navigate(Screen.Bimbingan.route) {
                            popUpTo(Screen.MahasiswaDashboard.route)
                        }
                    }
                },
                onProfileClick = {
                    // Sudah di halaman Profile
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Profil Akun",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card Avatar & Nama
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F3F4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = authState.name ?: "-",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(SimtaRed.copy(alpha = 0.1f))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = roleLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SimtaRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Card Informasi Detail
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Informasi Pribadi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Nama Lengkap",
                        value = authState.name ?: "-"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = authState.email ?: "-"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.Badge,
                        label = identityLabel,
                        value = identityValue
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.School,
                        label = "Fakultas",
                        value = "Fakultas Ilmu Komputer"
                    )

                    ProfileInfoRow(
                        icon = Icons.Default.School,
                        label = "Jurusan",
                        value = "Informatika"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Logout
            Button(
                onClick = {
                    authViewModel.logout(
                        onSuccess = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SimtaRed
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color.White
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Logout",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(SimtaRed.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SimtaRed,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun ProfileBottomNavigation(
    onHomeClick: () -> Unit,
    onPengajuanClick: () -> Unit,
    onBimbinganClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = SimtaRed,
        unselectedIconColor = Color.White.copy(alpha = 0.7f),
        selectedTextColor = Color.White,
        unselectedTextColor = Color.White.copy(alpha = 0.7f),
        indicatorColor = Color.White
    )

    NavigationBar(
        containerColor = SimtaRed,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onPengajuanClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Article,
                    contentDescription = "Pengajuan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Pengajuan", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onBimbinganClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Bimbingan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Bimbingan", fontSize = 10.sp, fontWeight = FontWeight.Medium)
            }
        )

        NavigationBarItem(
            selected = true, // Posisi aktif karena sedang di halaman Profil
            onClick = onProfileClick,
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text("Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        )
    }
}