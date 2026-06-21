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
import com.project.R
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
        delay(1500)
        authViewModel.checkSession()
    }

    LaunchedEffect(state.isLoading, state.isLoggedIn, state.role) {
        if (!state.isLoading) {
            if (state.isLoggedIn) {
                val destination = when (state.role?.trim()?.lowercase()) {
                    "mahasiswa" -> Screen.MahasiswaDashboard.route
                    "dosen" -> Screen.DosenDashboard.route
                    "kaprodi" -> Screen.KaprodiDashboard.route
                    "tu" -> Screen.TuDashboard.route
                    else -> Screen.Login.route
                }

                navController.navigate(destination) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
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
        Image(
            painter = painterResource(id = R.drawable.logo_simta),
            contentDescription = "Logo SIMTA",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SIMTA",
            color = SimtaRed,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 8.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        CircularProgressIndicator(
            color = SimtaRed,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp)
        )
    }
}