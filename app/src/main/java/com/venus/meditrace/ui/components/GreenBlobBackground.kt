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
 * Full-screen background with three soft circular blobs drawn on a solid
 * [MediMedGreen] canvas.
 *
 * No changes required for production — the composable is stateless and
 * purely decorative. Blob positions are expressed as fractions of the
 * canvas size so they scale to any screen without modification.
 */
@Composable
fun GreenBlobBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MediMedGreen)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Bottom-left large blob
            drawCircle(
                color  = MediBlobGreen.copy(alpha = 0.55f),
                radius = size.width * 0.60f,
                center = Offset(x = -size.width * 0.15f, y = size.height * 0.82f)
            )
            // Bottom-right blob
            drawCircle(
                color  = MediBlobGreen.copy(alpha = 0.40f),
                radius = size.width * 0.45f,
                center = Offset(x = size.width * 0.95f, y = size.height * 0.90f)
            )
            // Top-right subtle blob
            drawCircle(
                color  = MediBlobGreen.copy(alpha = 0.20f),
                radius = size.width * 0.32f,
                center = Offset(x = size.width * 1.08f, y = size.height * 0.10f)
            )
        }
        content()
    }
}