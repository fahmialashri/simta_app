package com.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.core.SimtaGreen
import com.project.core.SimtaRed

@Composable
internal fun HeaderSection(
    name: String,
    nidn: String,
    onRefreshClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoxAvatar()

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Halo, Selamat Datang!",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp
            )

            Text(
                text = name,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Text(
                text = "NIDN: $nidn",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp
            )
        }

        IconButton(
            onClick = onRefreshClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = SimtaRed,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun BoxAvatar() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
internal fun SmallInfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = SimtaRed
            )
        }
    }
}

@Composable
internal fun EmptyCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = text,
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun ErrorCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = SimtaRed
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                fontSize = 13.sp,
                color = SimtaRed,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun SuccessCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SimtaGreen
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                fontSize = 13.sp,
                color = SimtaGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}