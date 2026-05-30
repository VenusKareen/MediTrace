package com.venus.meditrace.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.venus.meditrace.viewmodel.ScanUiState
import com.venus.meditrace.viewmodel.ScanViewModel

@Composable
fun ProductDetailsScreen(
    viewModel: ScanViewModel,
    batchId:   String,
    onBack:    () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // If the ViewModel doesn't already have this product loaded (e.g. navigated
    // from scan history), fetch it from the backend using batchId only.
    LaunchedEffect(batchId) {
        viewModel.fetchByBatchId(batchId)
    }

    GreenBlobBackground {
        Column(modifier = Modifier.fillMaxSize()) {

            MediTraceTopBar(
                showBack = true,
                onBack   = onBack
            )

            when (val state = uiState) {

                // ── Loading ───────────────────────────────────────────────
                is ScanUiState.Loading -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color    = MediAccentGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text     = "Verifying product...",
                                color    = WhiteAlpha70,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // ── Verified ──────────────────────────────────────────────
                is ScanUiState.Verified -> {
                    val result = state.result
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Box(
                            modifier         = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MediDarkGreen.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint               = MediAccentGreen,
                                    modifier           = Modifier.size(44.dp)
                                )
                                Icon(
                                    imageVector        = Icons.Default.Person,
                                    contentDescription = null,
                                    tint               = White,
                                    modifier           = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text       = "Medication Verified",
                            color      = White,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign  = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MediDarkGreen)
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text       = "Product Details",
                                color      = White,
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign  = TextAlign.Center,
                                modifier   = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            DetailRow("Manufacturer",      result.manufacturer      ?: "N/A")
                            DetailRow("Retailer",          result.retailer          ?: "N/A")
                            DetailRow("Store Location",    result.storeLocation     ?: "N/A")
                            DetailRow("Product ID",        result.productId         ?: "N/A")
                            DetailRow("Batch",             result.batchNumber       ?: "N/A")
                            DetailRow("Name",              result.productName       ?: "N/A")
                            DetailRow("Active Ingredient", result.activeIngredient  ?: "N/A")
                            DetailRow("Strength",          result.strength          ?: "N/A")
                            DetailRow("Expiry Date",       result.expiryDate        ?: "N/A")
                            DetailRow("PPB Reg. No.",      result.ppbRegNumber      ?: "N/A")
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // ── Not Found ─────────────────────────────────────────────
                is ScanUiState.NotFound -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint               = WhiteAlpha40,
                                modifier           = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text       = "Product Not Found",
                                color      = White,
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text      = "Batch ID: $batchId",
                                color     = WhiteAlpha70,
                                fontSize  = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onBack,
                                colors  = ButtonDefaults.buttonColors(
                                    containerColor = MediDarkGreen
                                )
                            ) {
                                Text("Go Back", color = White)
                            }
                        }
                    }
                }

                // ── Error ─────────────────────────────────────────────────
                is ScanUiState.Error -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint               = StatusExpired,
                                modifier           = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text       = "Something went wrong",
                                color      = White,
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text      = state.message,
                                color     = WhiteAlpha70,
                                fontSize  = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedButton(
                                    onClick = onBack,
                                    colors  = ButtonDefaults.outlinedButtonColors(
                                        contentColor = White
                                    )
                                ) {
                                    Text("Go Back")
                                }
                                Button(
                                    onClick = { viewModel.fetchByBatchId(batchId) },
                                    colors  = ButtonDefaults.buttonColors(
                                        containerColor = MediDarkGreen
                                    )
                                ) {
                                    Text("Retry", color = White)
                                }
                            }
                        }
                    }
                }

                // ── Idle / Scanning ───────────────────────────────────────
                else -> {
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color    = MediAccentGreen,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = "$label:",
            color      = WhiteAlpha70,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier.weight(1.1f)
        )
        Text(
            text     = value,
            color    = White,
            fontSize = 13.sp,
            modifier = Modifier.weight(1.3f)
        )
    }
    HorizontalDivider(color = WhiteAlpha40, thickness = 0.5.dp)
}