package com.venus.meditrace.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.theme.*

@Composable
fun MediTraceTopBar(
    title      : String    = "MEDI-TRACE",
    showBack   : Boolean   = false,
    onBack     : () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    Surface(
        color  = MediDarkGreen,
        shape  = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = if (showBack) onBack else onMenuClick
            ) {
                Icon(
                    imageVector        = if (showBack) Icons.Default.ArrowBack
                    else           Icons.Default.Menu,
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
                modifier   = Modifier.weight(1f),
                textAlign  = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.size(48.dp))
        }
    }
}