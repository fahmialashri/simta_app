package com.project.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.R // Pastikan ini mengarah ke R package project lu
import com.project.auth.AuthViewModel
import com.project.core.SimtaRed
import com.project.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val state by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        delay(1500) // Gua tambahin delay dikit jadi 1.5 detik biar logonya sempet kelihatan cakep
        authViewModel.checkSession()
    }

    LaunchedEffect(state.isLoading, state.isLoggedIn, state.role) {
        if (!state.isLoading) {
            if (state.isLoggedIn) {
                when (state.role) {
                    "dosen" -> navController.navigate(Screen.DosenDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }

                    else -> navController.navigate(Screen.MahasiswaDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            } else {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- TEMPAT LOGO DI SINI ---
        // Ganti R.drawable.ic_launcher_foreground dengan nama file logo lu di folder drawable
        Image(
            painter = painterResource(id = R.drawable.logo_simta), // Contoh: R.drawable.logo_simta
            contentDescription = "Logo SIMTA",
            modifier = Modifier.size(120.dp) // Ukuran logo bisa lu gede/kecilin di sini
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SIMTA",
            color = SimtaRed,
            fontSize = 32.sp, // Gua gedein dikit biar lebih proporsional sama logo
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 8.sp
        )

        Spacer(modifier = Modifier.height(48.dp)) // Jarak agak jauh ke loading indicator biar rapi

        CircularProgressIndicator(
            color = SimtaRed,
            strokeWidth = 3.dp, // Bikin garis loadingnya agak tipis biar kelihatan lebih modern
            modifier = Modifier.size(36.dp)
        )
    }
}