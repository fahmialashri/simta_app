package com.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.project.navigation.AppNavGraph
import com.project.notification.NotificationPermissionGate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                NotificationPermissionGate {
                    AppNavGraph()
                }
            }
        }
    }
}