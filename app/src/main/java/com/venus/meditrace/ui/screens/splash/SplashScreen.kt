package com.venus.meditrace.ui.screens.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.venus.meditrace.ui.components.GreenBlobBackground
import com.venus.meditrace.ui.navigation.Screen
import com.venus.meditrace.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnim by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue   = if (startAnim) 1f else 0f,
        animationSpec = tween(1200),
        label         = "splashFade"
    )
    val scale by animateFloatAsState(
        targetValue   = if (startAnim) 1f else 0.6f,
        animationSpec = tween(1200),
        label         = "splashScale"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(4500)
        navController.navigate(Screen.Onboarding.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    GreenBlobBackground {
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .background(MediMedGreen),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(scale)
                        .alpha(alpha)
                        .clip(CircleShape)
                        .background(MediDarkGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Filled.HealthAndSafety,
                        contentDescription = "MediTrace",
                        tint               = White,
                        modifier           = Modifier.size(80.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text       = "MediTrace",
                    color      = White,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.alpha(alpha)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text     = "Authenticate. Verify. Trust.",
                    color    = WhiteAlpha70,
                    fontSize = 14.sp,
                    modifier = Modifier.alpha(alpha)
                )
            }
        }
    }
}