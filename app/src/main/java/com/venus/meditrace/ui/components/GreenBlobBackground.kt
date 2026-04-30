package com.venus.meditrace.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.venus.meditrace.ui.theme.MediBlobGreen
import com.venus.meditrace.ui.theme.MediMedGreen

/**
 * The green blob / organic shape background seen across all Figma screens.
 * Draws two large soft circles (blobs) to replicate the design.
 */
@Composable
fun GreenBlobBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MediMedGreen)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Bottom-left blob
            drawCircle(
                color  = MediBlobGreen.copy(alpha = 0.55f),
                radius = size.width * 0.55f,
                center = Offset(x = -size.width * 0.15f, y = size.height * 0.80f)
            )
            // Bottom-right blob (slightly smaller)
            drawCircle(
                color  = MediBlobGreen.copy(alpha = 0.40f),
                radius = size.width * 0.42f,
                center = Offset(x = size.width * 0.95f, y = size.height * 0.92f)
            )
            // Top-right subtle blob
            drawCircle(
                color  = MediBlobGreen.copy(alpha = 0.25f),
                radius = size.width * 0.35f,
                center = Offset(x = size.width * 1.05f, y = size.height * 0.12f)
            )
        }
        content()
    }
}