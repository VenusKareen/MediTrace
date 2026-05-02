package com.venus.meditrace.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.components.GreenBlobBackground
import com.venus.meditrace.ui.theme.*

// ── Data model for each onboarding page ───────────────────────────────────
data class OnboardingData(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val pages = listOf(
    OnboardingData(
        icon        = Icons.Default.DocumentScanner,
        title       = "Ensure Your Medicines Are Authentic",
        description = "Medi-Trace helps you verify the authenticity of your medicines " +
                "before use, protecting you from counterfeit antibiotics that " +
                "could harm your health."
    ),
    OnboardingData(
        icon        = Icons.Default.PhoneAndroid,
        title       = "Scan and Track with Medi-Trace",
        description = "Simply scan the QR code on any antibiotic packaging to instantly " +
                "verify its authenticity, batch number, manufacturer and expiry date."
    ),
    OnboardingData(
        icon        = Icons.Default.VerifiedUser,
        title       = "Seamless Pharmaceutical Verification",
        description = "Our system connects directly to the official Pharmacy and Poisons " +
                "Board database to give you accurate, real-time verification results."
    )
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {

    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = pages.size

    GreenBlobBackground {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            // ── Animated page content ─────────────────────────────────────
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                },
                label = "onboarding_page"
            ) { page ->
                OnboardingPage(data = pages[page])
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Dot indicators ────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(totalPages) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 10.dp else 7.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentPage) White
                                else WhiteAlpha40
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Navigation buttons ────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // "Back" button — hidden on first page
                if (currentPage > 0) {
                    OutlinedButton(
                        onClick = { currentPage-- },
                        shape   = RoundedCornerShape(50.dp),
                        colors  = ButtonDefaults.outlinedButtonColors(
                            contentColor = White
                        ),
                        border  = androidx.compose.foundation.BorderStroke(
                            1.dp, WhiteAlpha70
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(
                            text       = "Back",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // "Next" / "Finish" button
                Button(
                    onClick = {
                        if (currentPage < totalPages - 1) {
                            currentPage++
                        } else {
                            onFinished()
                        }
                    },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MediDarkGreen
                    ),
                    shape   = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text       = if (currentPage == totalPages - 1) "Finish" else "Next",
                        color      = White,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// ── Single onboarding page content ────────────────────────────────────────
@Composable
private fun OnboardingPage(data: OnboardingData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.fillMaxWidth()
    ) {
        // Illustration box
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MediDarkGreen.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = data.icon,
                contentDescription = null,
                tint               = White,
                modifier           = Modifier.size(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text       = data.title,
            color      = White,
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text      = data.description,
            color     = WhiteAlpha70,
            fontSize  = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}