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

@Composable
fun MediTraceTopBar(
    title: String          = "MEDI-TRACE",
    showBack: Boolean      = false,
    onBack: (() -> Unit)?  = null,
    onMenuClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(MediDarkGreen)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Leading icon
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            if (showBack) {
                IconButton(onClick = { onBack?.invoke() }) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint               = White,
                        modifier           = Modifier.size(18.dp)
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

        // Title — always centered
        Text(
            text          = title,
            color         = White,
            fontSize      = 17.sp,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 2.sp,
            textAlign     = TextAlign.Center,
            modifier      = Modifier.align(Alignment.Center)
        )
    }
}