package com.venus.meditrace.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.data.model.ScanHistoryItem
import com.venus.meditrace.ui.components.GreenBlobBackground
import com.venus.meditrace.ui.components.MediTraceTopBar
import com.venus.meditrace.ui.theme.*
import com.venus.meditrace.util.Constants

// ── Placeholder history data ───────────────────────────────────────────────
// In production these come from local Room DB or a ViewModel.
private val sampleHistory = listOf(
    ScanHistoryItem("Amoxicillin 500mg", "Nairobi Pharmacy", Constants.STATUS_VALID,     "2025-05-01", "AMX-B001"),
    ScanHistoryItem("Ciprofloxacin 250mg", "City Chemist",   Constants.STATUS_EXPIRED,   "2025-04-28", "CIP-B002"),
    ScanHistoryItem("Penicillin 500mg",  "MedPlus Pharmacy", Constants.STATUS_VALID,     "2025-04-25", "PEN-B003"),
)

@Composable
fun HomeScreen(onScanClick: () -> Unit) {
    GreenBlobBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────────────────
            MediTraceTopBar(showBack = false)

            // ── Body ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                // "History" label
                Text(
                    text       = "History",
                    color      = White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )

                // History list
                LazyColumn(
                    modifier           = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sampleHistory) { item ->
                        HistoryItemCard(item)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // ── Camera FAB (bottom right) ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 20.dp, bottom = 28.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick           = onScanClick,
                containerColor    = MediDarkGreen,
                contentColor      = White,
                shape             = CircleShape,
                modifier          = Modifier.size(58.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Camera,
                    contentDescription = "Scan QR Code",
                    modifier           = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ── History Item Card ─────────────────────────────────────────────────────

@Composable
private fun HistoryItemCard(item: ScanHistoryItem) {
    val statusColor = when (item.status) {
        Constants.STATUS_VALID       -> MediAccentGreen
        Constants.STATUS_EXPIRED     -> Color(0xFFFFA726)
        Constants.STATUS_COUNTERFEIT -> ErrorRed
        else                         -> WhiteAlpha70
    }

    val statusLabel = when (item.status) {
        Constants.STATUS_VALID       -> "Verified"
        Constants.STATUS_EXPIRED     -> "Expired"
        Constants.STATUS_COUNTERFEIT -> "Counterfeit"
        else                         -> "Unknown"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MediDarkGreen)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product icon placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MediBlobGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.MedicalServices,
                contentDescription = null,
                tint               = White,
                modifier           = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Text details
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = item.productName,
                color      = White,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = item.storeLocation,
                color    = WhiteAlpha70,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text       = statusLabel,
                color      = statusColor,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}