package com.venus.meditrace.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.data.model.ScanHistoryItem
import com.venus.meditrace.ui.components.MediTraceTopBar
import com.venus.meditrace.ui.theme.*
import com.venus.meditrace.util.Constants
import java.text.SimpleDateFormat
import java.util.*

private val sampleHistory = listOf(
    ScanHistoryItem(
        productName     = "Amoxicillin 500mg",
        storeLocation   = "Nairobi Pharmacy",
        status          = Constants.STATUS_VALID,
        timestampMillis = System.currentTimeMillis() - 86_400_000L,
        batchId         = "AMX-B001"
    ),
    ScanHistoryItem(
        productName     = "Ciprofloxacin 250mg",
        storeLocation   = "City Chemist",
        status          = Constants.STATUS_EXPIRED,
        timestampMillis = System.currentTimeMillis() - 3 * 86_400_000L,
        batchId         = "CIP-B002"
    ),
    ScanHistoryItem(
        productName     = "Penicillin 500mg",
        storeLocation   = "MedPlus Pharmacy",
        status          = Constants.STATUS_VALID,
        timestampMillis = System.currentTimeMillis() - 6 * 86_400_000L,
        batchId         = "PEN-B003"
    ),
)

@Composable
fun ScanHistoryScreen(onBack: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MediMedGreen)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            MediTraceTopBar(
                title    = "SCAN HISTORY",
                showBack = true,
                onBack   = onBack
            )

            if (sampleHistory.isEmpty()) {
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector        = Icons.Default.History,
                            contentDescription = null,
                            tint               = WhiteAlpha40,
                            modifier           = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text       = "No scans yet",
                            color      = WhiteAlpha70,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text     = "Your scan history will appear here",
                            color    = WhiteAlpha40,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier            = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding      = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    items(sampleHistory) { item ->
                        HistoryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: ScanHistoryItem) {
    val statusColor = when (item.status) {
        Constants.STATUS_VALID       -> StatusVerified
        Constants.STATUS_EXPIRED     -> StatusExpired
        Constants.STATUS_COUNTERFEIT -> StatusCounterfeit
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
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MediBlobGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.MedicalServices,
                contentDescription = null,
                tint               = White,
                modifier           = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = statusLabel,
                    color      = statusColor,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text     = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(item.timestampMillis)),
                    color    = WhiteAlpha70,
                    fontSize = 11.sp
                )
            }
        }

        Icon(
            imageVector        = Icons.Default.ChevronRight,
            contentDescription = null,
            tint               = WhiteAlpha70,
            modifier           = Modifier.size(20.dp)
        )
    }
}