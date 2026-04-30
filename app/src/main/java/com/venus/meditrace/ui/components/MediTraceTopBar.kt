package com.venus.meditrace.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.theme.MediDarkGreen
import com.venus.meditrace.ui.theme.White

/**
 * The pill-shaped "MEDI-TRACE" top bar from the Figma.
 *
 * @param showBack  true → show back arrow (Product / Report screens)
 *                  false → show hamburger menu (Home screen)
 * @param title     custom title override (e.g. "Report Suspicious Product")
 */
@Composable
fun MediTraceTopBar(
    showBack: Boolean  = false,
    title: String      = "MEDI-TRACE",
    onBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(MediDarkGreen)
            .padding(horizontal = 8.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Leading icon
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
        ) {
            if (showBack) {
                IconButton(onClick = { onBack?.invoke() }) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint               = White,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(onClick = { onMenuClick?.invoke() }) {
                    Icon(
                        imageVector        = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint               = White,
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Centered title
        Text(
            text       = title,
            color      = White,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            letterSpacing = 1.5.sp,
            modifier   = Modifier.align(Alignment.Center)
        )
    }
}