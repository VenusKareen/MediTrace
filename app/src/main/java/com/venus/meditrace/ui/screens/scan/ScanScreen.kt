package com.venus.meditrace.ui.screens.scan

import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.venus.meditrace.data.model.VerificationResult
import com.venus.meditrace.ui.theme.*
import com.venus.meditrace.viewmodel.ScanUiState
import com.venus.meditrace.viewmodel.ScanViewModel
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    onVerified: () -> Unit,
    onNotFound: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    // React to state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is ScanUiState.Verified -> onVerified()
            is ScanUiState.NotFound -> onNotFound()
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint               = MediDarkGreen,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Text(
                text       = "Scanning Product",
                fontSize   = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MediDarkGreen,
                modifier   = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Camera viewfinder ─────────────────────────────────────────────
        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFBDBDBD), RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            if (cameraPermission.status.isGranted) {
                CameraPreview(onQrDetected = { viewModel.onQrDetected(it) })
                // Green alignment guide
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .border(3.dp, MediAccentGreen, RoundedCornerShape(12.dp))
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier.padding(16.dp)
                ) {
                    Text(
                        text      = "Camera permission is required to scan QR codes",
                        color     = TextGray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { cameraPermission.launchPermissionRequest() },
                        colors  = ButtonDefaults.buttonColors(containerColor = MediDarkGreen)
                    ) { Text("Grant Permission", color = White) }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Instruction text ──────────────────────────────────────────────
        Text(
            text      = "Point your camera to the QR Code on the product",
            fontSize  = 13.sp,
            color     = TextGray,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(horizontal = 48.dp)
        )

        // ── Loading indicator ─────────────────────────────────────────────
        if (uiState is ScanUiState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = MediDarkGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Verifying product...", color = TextGray, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── DEMO buttons — lets you see all screens without a real QR ─────
        // Remove these once the backend is live
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text      = "── Demo Navigation ──",
                color     = TextGray,
                fontSize  = 11.sp,
                textAlign = TextAlign.Center
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Simulate a VALID scan result
                Button(
                    onClick = {
                        viewModel.setMockResult(
                            VerificationResult(
                                status          = "VALID",
                                productName     = "Amoxicillin 500mg",
                                manufacturer    = "PharmaCo Kenya Ltd.",
                                retailer        = "Nairobi Pharmacy",
                                storeLocation   = "Westlands, Nairobi",
                                productId       = "PC-AMX-001",
                                batchNumber     = "AMX-B001-2025",
                                activeIngredient = "Amoxicillin Trihydrate",
                                strength        = "500mg",
                                expiryDate      = "2027-06-30",
                                ppbRegNumber    = "PPB/NOM/2021/001",
                                message         = null
                            )
                        )
                    },
                    colors   = ButtonDefaults.buttonColors(containerColor = MediAccentGreen),
                    shape    = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) { Text("✓ Valid", color = White, fontSize = 12.sp) }

                // Simulate NOT FOUND
                Button(
                    onClick  = { viewModel.setNotFound() },
                    colors   = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape    = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) { Text("✗ Not Found", color = White, fontSize = 12.sp) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Green circle button (Figma decorative element) ─────────────────
        Box(
            modifier         = Modifier
                .padding(bottom = 36.dp)
                .size(60.dp)
                .clip(CircleShape)
                .background(MediDarkGreen),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MediBlobGreen)
            )
        }
    }
}

// ── CameraX Preview ───────────────────────────────────────────────────────
@Composable
private fun CameraPreview(onQrDetected: (String) -> Unit) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) { onDispose { executor.shutdown() } }

    AndroidView(
        factory = { ctx ->
            val previewView          = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview        = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(executor, QrCodeAnalyzer(onQrDetected)) }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    Log.e("ScanScreen", "Camera bind failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

// ── ZXing QR analyzer ─────────────────────────────────────────────────────
private class QrCodeAnalyzer(
    private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    override fun analyze(image: ImageProxy) {
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes              = ByteArray(buffer.remaining()).also { buffer.get(it) }
        val source             = PlanarYUVLuminanceSource(
            bytes, image.width, image.height, 0, 0, image.width, image.height, false
        )
        try {
            onQrDetected(reader.decode(BinaryBitmap(HybridBinarizer(source))).text)
        } catch (_: NotFoundException) {
            // No QR in this frame — continue
        } finally {
            image.close()
        }
    }
}