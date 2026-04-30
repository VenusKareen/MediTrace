package com.venus.meditrace.ui.screens.result

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.components.GreenBlobBackground
import com.venus.meditrace.ui.components.MediTraceTopBar
import com.venus.meditrace.ui.theme.*

@Composable
fun ProductNotFoundScreen(
    onReportCounterfeit: () -> Unit,
    onBack: () -> Unit
) {
    GreenBlobBackground {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top Bar ──────────────────────────────────────────────────
            MediTraceTopBar(showBack = true, onBack = onBack)

            Spacer(modifier = Modifier.height(32.dp))

            // ── Warning illustration ──────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(110.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Warning,
                    contentDescription = "Not Found",
                    tint               = ErrorRed,
                    modifier           = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── "Product Not Found" ───────────────────────────────────────
            Text(
                text       = "Product Not Found",
                color      = ErrorRed,
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Description ───────────────────────────────────────────────
            Text(
                text      = "Sorry, this product could not be verified in the\n" +
                        "Medi-Trace database.\n\n" +
                        "Ensure the QR code is clearly visible or manually\n" +
                        "enter the batch number.",
                color     = White,
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Report Counterfeit Button ─────────────────────────────────
            Button(
                onClick = onReportCounterfeit,
                colors  = ButtonDefaults.buttonColors(containerColor = MediDarkGreen),
                shape   = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .padding(bottom = 48.dp)
                    .height(50.dp)
                    .widthIn(min = 220.dp)
            ) {
                Text(
                    text       = "Report Counterfeit",
                    color      = White,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}