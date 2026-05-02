package com.venus.meditrace.ui.screens.splash

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.components.GreenBlobBackground
import com.venus.meditrace.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    // Auto-navigate after 2.5 seconds
    LaunchedEffect(Unit) {
        delay(2500)
        onSplashComplete()
    }

    // Logo scale animation
    val scale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue   = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessLow
            )
        )
    }

    GreenBlobBackground {
        Box(
            modifier            = Modifier.fillMaxSize(),
            contentAlignment    = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // ── Black circle logo (matches Figma) ─────────────────────
                Box(
                    modifier = Modifier
                        .scale(scale.value)
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0D1F14)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Shield icon — stands in for the Figma logo SVG
                        Icon(
                            imageVector        = Icons.Default.HealthAndSafety,
                            contentDescription = "MediTrace Logo",
                            tint               = MediAccentGreen,
                            modifier           = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // "MediTrace" split text matching Figma
                        Row {
                            Text(
                                text       = "Medi",
                                color      = White,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text       = "Trace",
                                color      = MediAccentGreen,
                                fontSize   = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}