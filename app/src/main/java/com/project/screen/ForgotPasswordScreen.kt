package com.project.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.project.R
import com.project.auth.AuthViewModel
import com.project.component.SimtaButton
import com.project.core.SimtaRed

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        authViewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
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
                .height(300.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            SimtaRed,
                            SimtaRed.copy(alpha = 0.82f)
                        )
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = 48.dp,
                        bottomEnd = 48.dp
                    )
                )
        )

        IconButton(
            onClick = {
                authViewModel.clearError()
                navController.popBackStack()
            },
            modifier = Modifier.padding(start = 12.dp, top = 36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Kembali",
                tint = Color.White
            )
        }

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

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Lupa Kata Sandi",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Masukkan email akun SIMTA kamu",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(42.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Reset Password",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Link untuk mengganti password akan dikirim ke email kamu.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(28.dp))

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

                    if (state.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.errorMessage.orEmpty(),
                            color = SimtaRed,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    SimtaButton(
                        text = if (state.isLoading) {
                            "Mengirim..."
                        } else {
                            "Kirim Link Reset"
                        },
                        enabled = !state.isLoading
                    ) {
                        authViewModel.sendPasswordResetEmail(
                            email = email,
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Kembali ke Login",
                        color = SimtaRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            authViewModel.clearError()
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}