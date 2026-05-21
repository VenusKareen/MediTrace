package com.venus.meditrace.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.venus.meditrace.ui.components.GreenBlobBackground
import com.venus.meditrace.ui.theme.*
import com.venus.meditrace.util.Constants
import com.venus.meditrace.util.SecurePrefs

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
    val context     = LocalContext.current
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages  = pages.size

    GreenBlobBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MediMedGreen)
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                AnimatedContent(
                    targetState    = currentPage,
                    transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    },
                    label    = "onboarding_page",
                    modifier = Modifier.weight(1f)
                ) { page ->
                    Box(
                        modifier         = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        OnboardingPage(data = pages[page])
                    }
                }

                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    modifier              = Modifier.padding(vertical = 20.dp)
                ) {
                    repeat(totalPages) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentPage) 10.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentPage) White else WhiteAlpha40
                                )
                        )
                    }
                }

                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (currentPage > 0) {
                        OutlinedButton(
                            onClick  = { currentPage-- },
                            shape    = RoundedCornerShape(50.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = White),
                            border   = BorderStroke(1.dp, WhiteAlpha70),
                            modifier = Modifier.weight(1f).height(48.dp)
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

                    Button(
                        onClick = {
                            if (currentPage < totalPages - 1) {
                                currentPage++
                            } else {
                                // Mark onboarding complete so SplashScreen
                                // routes directly to Home on next launch
                                SecurePrefs.putBoolean(
                                    context,
                                    Constants.KEY_ONBOARDING_DONE,
                                    true
                                )
                                onFinished()
                            }
                        },
                        colors   = ButtonDefaults.buttonColors(containerColor = MediDarkGreen),
                        shape    = RoundedCornerShape(50.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
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
}

@Composable
private fun OnboardingPage(data: OnboardingData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier            = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(MediDarkGreen.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = data.icon,
                contentDescription = null,
                tint               = White,
                modifier           = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text       = data.title,
            color      = White,
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text       = data.description,
            color      = WhiteAlpha70,
            fontSize   = 14.sp,
            textAlign  = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}