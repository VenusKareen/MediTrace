package com.venus.meditrace.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.theme.MediDarkGreen
import com.venus.meditrace.ui.theme.White

/**
 * App-wide top bar.
 *
 * Production improvements over prototype:
 *  - Removed unused [clip] import.
 *  - Removed redundant 0.dp corner args from [RoundedCornerShape] (default).
 *  - Switched to [Icons.AutoMirrored.Filled.ArrowBack] which respects RTL
 *    layouts automatically (the manifest already declares supportsRtl=true).
 *  - [TextAlign] import moved to a direct reference — no star import required.
 *  - Icon tint references the theme [White] token rather than a hard-coded
 *    Color.White so it responds to theme changes.
 */
@Composable
fun MediTraceTopBar(
    title      : String    = "MEDI-TRACE",
    showBack   : Boolean   = false,
    onBack     : () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    Surface(
        color           = MediDarkGreen,
        shadowElevation = 4.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = if (showBack) onBack else onMenuClick) {
                Icon(
                    imageVector        = if (showBack)
                        Icons.AutoMirrored.Filled.ArrowBack
                    else
                        Icons.Default.Menu,
                    contentDescription = if (showBack) "Back" else "Menu",
                    tint               = White,
                    modifier           = Modifier.size(24.dp)
                )
            }

            Text(
                text       = title,
                color      = White,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.weight(1f)
            )

            // Balancing spacer so title stays centred
            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}