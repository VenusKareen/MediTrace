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
import com.venus.meditrace.ui.theme.MediMedGreen
import com.venus.meditrace.ui.theme.MediTraceTheme

/**
 * Single-activity host for the Compose navigation graph.
 *
 * No changes to structure required for production; the activity is
 * intentionally thin — all logic lives in ViewModels and the nav graph.
 *
 * Edge-to-edge is enabled so status-bar padding is handled by
 * [MediTraceTopBar.statusBarsPadding] rather than by the window insets.
 */
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
                    MediTraceNavGraph(navController = rememberNavController())
                }
            }
        }
    }
}