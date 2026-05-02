package com.venus.meditrace.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

// ── Sample history data (replaced by real data when backend is live) ───────
private val sampleHistory = listOf(
    ScanHistoryItem("Amoxicillin 500mg",    "Nairobi Pharmacy",  Constants.STATUS_VALID,        "2025-05-01", "AMX-B001"),
    ScanHistoryItem("Ciprofloxacin 250mg",  "City Chemist",      Constants.STATUS_EXPIRED,      "2025-04-28", "CIP-B002"),
    ScanHistoryItem("Penicillin 500mg",     "MedPlus Pharmacy",  Constants.STATUS_VALID,        "2025-04-25", "PEN-B003"),
)

@Composable
fun HomeScreen(onScanClick: () -> Unit) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    // ── Navigation Drawer (hamburger menu) ────────────────────────────────
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MediDarkGreen
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Drawer header
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint               = MediAccentGreen,
                        modifier           = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text       = "Medi-Trace",
                        color      = White,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text     = "Antibiotic Verification",
                        color    = WhiteAlpha70,
                        fontSize = 13.sp
                    )
                }

                HorizontalDivider(color = WhiteAlpha40, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Drawer items
                DrawerMenuItem(
                    icon  = Icons.Default.Home,
                    label = "Home",
                    onClick = { scope.launch { drawerState.close() } }
                )
                DrawerMenuItem(
                    icon  = Icons.Default.QrCodeScanner,
                    label = "Scan Product",
                    onClick = {
                        scope.launch { drawerState.close() }
                        onScanClick()
                    }
                )
                DrawerMenuItem(
                    icon  = Icons.Default.History,
                    label = "Scan History",
                    onClick = { scope.launch { drawerState.close() } }
                )
                DrawerMenuItem(
                    icon  = Icons.Default.Report,
                    label = "Report Product",
                    onClick = { scope.launch { drawerState.close() } }
                )
                DrawerMenuItem(
                    icon  = Icons.Default.Info,
                    label = "About",
                    onClick = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        // ── Main screen content ───────────────────────────────────────────
        GreenBlobBackground {
            Box(modifier = Modifier.fillMaxSize()) {

                Column(modifier = Modifier.fillMaxSize()) {

                    // Top Bar — hamburger opens drawer
                    MediTraceTopBar(
                        showBack    = false,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // "History" section
                    Text(
                        text       = "History",
                        color      = White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier            = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(sampleHistory) { item ->
                            HistoryCard(item)
                        }
                    }
                }

                // ── Camera FAB ────────────────────────────────────────────
                FloatingActionButton(
                    onClick        = onScanClick,
                    containerColor = MediDarkGreen,
                    contentColor   = White,
                    shape          = CircleShape,
                    modifier       = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 32.dp)
                        .size(58.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.CameraAlt,
                        contentDescription = "Scan QR Code",
                        modifier           = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

// ── Drawer menu item ──────────────────────────────────────────────────────
@Composable
private fun DrawerMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon   = {
            Icon(imageVector = icon, contentDescription = label, tint = White)
        },
        label  = {
            Text(text = label, color = White, fontSize = 15.sp)
        },
        selected = false,
        onClick  = onClick,
        colors   = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}

// ── History card ──────────────────────────────────────────────────────────
@Composable
private fun HistoryCard(item: ScanHistoryItem) {
    val statusColor = when (item.status) {
        Constants.STATUS_VALID        -> StatusVerified
        Constants.STATUS_EXPIRED      -> StatusExpired
        Constants.STATUS_COUNTERFEIT  -> StatusCounterfeit
        else                          -> WhiteAlpha70
    }
    val statusLabel = when (item.status) {
        Constants.STATUS_VALID        -> "Verified"
        Constants.STATUS_EXPIRED      -> "Expired"
        Constants.STATUS_COUNTERFEIT  -> "Counterfeit"
        else                          -> "Unknown"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MediDarkGreen)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon box
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
            Text(
                text       = statusLabel,
                color      = statusColor,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}