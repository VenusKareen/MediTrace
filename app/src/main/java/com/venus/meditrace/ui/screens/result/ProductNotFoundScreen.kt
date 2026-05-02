package com.venus.meditrace.ui.screens.result

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

            // ── Illustration — warning icon + person ──────────────────────
            Box(
                modifier         = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                // Person figure
                Icon(
                    imageVector        = Icons.Default.Person,
                    contentDescription = null,
                    tint               = White.copy(alpha = 0.6f),
                    modifier           = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomCenter)
                )
                // Warning exclamation
                Icon(
                    imageVector        = Icons.Default.ErrorOutline,
                    contentDescription = "Not Found",
                    tint               = ErrorRed,
                    modifier           = Modifier
                        .size(72.dp)
                        .align(Alignment.TopCenter)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── "Product Not Found" label ─────────────────────────────────
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
                lineHeight = 22.sp,
                modifier  = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // ── Report Counterfeit button ─────────────────────────────────
            Button(
                onClick  = onReportCounterfeit,
                colors   = ButtonDefaults.buttonColors(containerColor = MediDarkGreen),
                shape    = RoundedCornerShape(50.dp),
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