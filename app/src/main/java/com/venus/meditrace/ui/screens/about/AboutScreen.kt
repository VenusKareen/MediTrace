package com.venus.meditrace.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.components.MediTraceTopBar
import com.venus.meditrace.ui.theme.*

@Composable
fun AboutScreen(onBack: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MediMedGreen)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            MediTraceTopBar(
                title    = "ABOUT",
                showBack = true,
                onBack   = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ── App icon + name ───────────────────────────────────────
                Icon(
                    imageVector        = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint               = MediAccentGreen,
                    modifier           = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text       = "Medi-Trace",
                    color      = White,
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text     = "Antibiotic Verification",
                    color    = WhiteAlpha70,
                    fontSize = 14.sp
                )
                Text(
                    text     = "Version 1.0.0",
                    color    = WhiteAlpha40,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ── Mission statement ─────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MediDarkGreen, RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        text       = "MediTrace helps patients and healthcare providers verify the authenticity of antibiotic medications by scanning QR codes on packaging — fighting counterfeit drugs in the supply chain.",
                        color      = WhiteAlpha70,
                        fontSize   = 14.sp,
                        textAlign  = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Features ──────────────────────────────────────────────
                Text(
                    text       = "Features",
                    color      = White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                FeatureRow(
                    icon  = Icons.Default.QrCodeScanner,
                    title = "QR Code Scanning",
                    desc  = "Instantly scan medication packaging to verify authenticity"
                )
                FeatureRow(
                    icon  = Icons.Default.VerifiedUser,
                    title = "Real-Time Verification",
                    desc  = "Connected to a secure database of registered medications"
                )
                FeatureRow(
                    icon  = Icons.Default.Report,
                    title = "Report Counterfeits",
                    desc  = "Flag suspicious products to protect others"
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text       = "Built to protect patients.\nMade in Kenya 🇰🇪",
                    color      = WhiteAlpha40,
                    fontSize   = 12.sp,
                    textAlign  = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(MediDarkGreen, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MediAccentGreen,
            modifier           = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text       = title,
                color      = White,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text     = desc,
                color    = WhiteAlpha70,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}