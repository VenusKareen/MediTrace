package com.venus.meditrace.ui.screens.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val result  = (uiState as? ScanUiState.Verified)?.result ?: return

    GreenBlobBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MediTraceTopBar(
                showBack = true,
                onBack   = onBack
            )

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
                    modifier   = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                DetailRow("Manufacturer",   result.manufacturer  ?: "N/A")
                DetailRow("Retailer",       result.retailer       ?: "N/A")
                DetailRow("Store Location", result.storeLocation  ?: "N/A")
                DetailRow("Product ID",     result.productId      ?: "N/A")
                DetailRow("Batch",          result.batchNumber    ?: "N/A")
                DetailRow("Name",           result.productName    ?: "N/A")
                DetailRow("Strength",       result.strength       ?: "N/A")
                DetailRow("Expiry Date",    result.expiryDate     ?: "N/A")
                DetailRow("PPB Reg. No.",   result.ppbRegNumber   ?: "N/A")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 7.dp),
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
