package com.venus.meditrace.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.venus.meditrace.data.model.ScanHistoryItem
import com.venus.meditrace.ui.components.GreenBlobBackground
import com.venus.meditrace.ui.components.MediTraceTopBar
import com.venus.meditrace.ui.navigation.Screen
import com.venus.meditrace.ui.theme.*
import com.venus.meditrace.util.Constants
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Sample history — replace with Room DAO flow in production ─────────────
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
fun HomeScreen(
    navController: NavController,
    onScanClick:   () -> Unit,
    isLoggedIn:    Boolean = false,
    onLoginClick:  () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope       = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState     = drawerState,
        gesturesEnabled = true,
        drawerContent   = {
            ModalDrawerSheet(drawerContainerColor = MediDarkGreen) {

                Spacer(modifier = Modifier.height(48.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Icon(
                        imageVector        = Icons.Default.HealthAndSafety,
                        contentDescription = null,
                        tint               = MediAccentGreen,
                        modifier           = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Medi-Trace",
                        color      = White,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Antibiotic Verification",
                        color    = WhiteAlpha70,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = WhiteAlpha40, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // ── Home — already on home, just close drawer ─────────────
                DrawerItem(Icons.Default.Home, "Home") {
                    scope.launch { drawerState.close() }
                }

                // ── Scan Product ──────────────────────────────────────────
                DrawerItem(Icons.Default.QrCodeScanner, "Scan Product") {
                    scope.launch { drawerState.close() }
                    onScanClick()
                }

                // ── Scan History ──────────────────────────────────────────
                DrawerItem(Icons.Default.History, "Scan History") {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.ScanHistory.route)
                }

                // ── Report Product ────────────────────────────────────────
                DrawerItem(Icons.Default.Report, "Report Product") {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.ReportProduct.route)
                }

                // ── About ─────────────────────────────────────────────────
                DrawerItem(Icons.Default.Info, "About") {
                    scope.launch { drawerState.close() }
                    navController.navigate(Screen.About.route)
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = WhiteAlpha40, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // ── Pharmacist sign-in entry point ────────────────────────
                if (isLoggedIn) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector        = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint               = MediAccentGreen,
                            modifier           = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Signed in as Pharmacist", color = WhiteAlpha70, fontSize = 13.sp)
                    }
                } else {
                    DrawerItem(Icons.Default.Login, "Pharmacist Sign In") {
                        scope.launch { drawerState.close() }
                        onLoginClick()
                    }
                }
            }
        }
    ) {
        GreenBlobBackground {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MediMedGreen)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

                    MediTraceTopBar(
                        showBack    = false,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text       = "Recent Scans",
                            color      = White,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier   = Modifier.padding(start = 20.dp, bottom = 12.dp)
                        )

                        LazyColumn(
                            modifier            = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding      = PaddingValues(bottom = 80.dp)
                        ) {
                            items(sampleHistory) { item ->
                                HistoryCard(
                                    item    = item,
                                    onClick = {
                                        navController.navigate(
                                            Screen.ProductDetails.createRoute(item.batchId)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

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
                        contentDescription = "Scan",
                        modifier           = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────

private fun formatTimestamp(millis: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))

@Composable
private fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon     = { Icon(imageVector = icon, contentDescription = label, tint = White) },
        label    = { Text(label, color = White, fontSize = 15.sp) },
        selected = false,
        onClick  = onClick,
        colors   = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}

@Composable
private fun HistoryCard(item: ScanHistoryItem, onClick: () -> Unit) {
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
            .clickable { onClick() }
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
                    text     = formatTimestamp(item.timestampMillis),
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