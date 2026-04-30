package com.venus.meditrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.venus.meditrace.ui.navigation.MediTraceNavGraph
import com.venus.meditrace.ui.theme.MediTraceTheme
import com.venus.meditrace.ui.theme.MediMedGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediTraceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MediMedGreen
                ) {
                    val navController = rememberNavController()
                    MediTraceNavGraph(navController = navController)
                }
            }
        }
    }
}