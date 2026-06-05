package com.project.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.core.SimtaRed
import com.project.navigation.Screen

enum class MahasiswaBottomNavItem {
    HOME,
    PENGAJUAN,
    BIMBINGAN,
    PROFILE
}

@Composable
fun MahasiswaBottomNavigation(
    navController: NavHostController,
    selectedItem: MahasiswaBottomNavItem
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
            .clip(
                RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            )
    ) {
        NavigationBarItem(
            selected = selectedItem == MahasiswaBottomNavItem.HOME,
            onClick = {
                navController.navigate(Screen.MahasiswaDashboard.route) {
                    popUpTo(Screen.MahasiswaDashboard.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = if (selectedItem == MahasiswaBottomNavItem.HOME) 11.sp else 10.sp,
                    fontWeight = if (selectedItem == MahasiswaBottomNavItem.HOME) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
                )
            }
        )

        NavigationBarItem(
            selected = selectedItem == MahasiswaBottomNavItem.PENGAJUAN,
            onClick = {
                navController.navigate(Screen.Pengajuan.route) {
                    popUpTo(Screen.MahasiswaDashboard.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Article,
                    contentDescription = "Pengajuan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Pengajuan",
                    fontSize = if (selectedItem == MahasiswaBottomNavItem.PENGAJUAN) 11.sp else 10.sp,
                    fontWeight = if (selectedItem == MahasiswaBottomNavItem.PENGAJUAN) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
                )
            }
        )

        NavigationBarItem(
            selected = selectedItem == MahasiswaBottomNavItem.BIMBINGAN,
            onClick = {
                navController.navigate(Screen.Bimbingan.route) {
                    popUpTo(Screen.MahasiswaDashboard.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription = "Bimbingan",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Bimbingan",
                    fontSize = if (selectedItem == MahasiswaBottomNavItem.BIMBINGAN) 11.sp else 10.sp,
                    fontWeight = if (selectedItem == MahasiswaBottomNavItem.BIMBINGAN) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
                )
            }
        )

        NavigationBarItem(
            selected = selectedItem == MahasiswaBottomNavItem.PROFILE,
            onClick = {
                navController.navigate(Screen.Profile.route) {
                    popUpTo(Screen.MahasiswaDashboard.route) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            },
            colors = navItemColors,
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profil",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Profil",
                    fontSize = if (selectedItem == MahasiswaBottomNavItem.PROFILE) 11.sp else 10.sp,
                    fontWeight = if (selectedItem == MahasiswaBottomNavItem.PROFILE) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
                )
            }
        )
    }
}