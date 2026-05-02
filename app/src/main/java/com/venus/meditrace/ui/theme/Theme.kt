package com.venus.meditrace.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MediTraceLightColors = lightColorScheme(
    primary          = MediDarkGreen,
    onPrimary        = White,
    background       = MediMedGreen,
    onBackground     = White,
    surface          = White,
    onSurface        = MediDarkGreen,
    error            = ErrorRed,
    onError          = White
)

@Composable
fun MediTraceTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MediDarkGreen.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = MediTraceLightColors,
        typography  = MediTraceTypography,
        content     = content
    )
}