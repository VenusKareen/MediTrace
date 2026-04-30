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

    // React to state changes from ViewModel
    LaunchedEffect(uiState) {
        when (uiState) {
            is ScanUiState.Verified -> onVerified()
            is ScanUiState.NotFound -> onNotFound()
            else -> Unit
        }
    }

    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    // Request permission if not granted
    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    // ── Layout — light background as per Figma Scan screen ────────────────
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Top bar (simple — no pill shape on scan screen per Figma) ──────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint               = MediDarkGreen,
                    modifier           = Modifier.size(22.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        // ── Camera viewfinder ─────────────────────────────────────────────
        if (cameraPermission.status.isGranted) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MediDarkGreen, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                CameraPreview(onQrDetected = { rawQr -> viewModel.onQrDetected(rawQr) })

                // QR alignment guide overlay
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .border(3.dp, MediAccentGreen, RoundedCornerShape(12.dp))
                )
            }
        } else {
            // Permission denied state
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Camera permission required", color = TextGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { cameraPermission.launchPermissionRequest() },
                        colors  = ButtonDefaults.buttonColors(containerColor = MediDarkGreen)
                    ) { Text("Grant Permission") }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Instruction text ──────────────────────────────────────────────
        Text(
            text       = "Point your camera to the QR Code on the product",
            fontSize   = 14.sp,
            color      = TextGray,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.padding(horizontal = 40.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // ── Loading indicator (while API call in progress) ─────────────────
        if (uiState is ScanUiState.Loading) {
            CircularProgressIndicator(color = MediDarkGreen)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Green circular button (Figma bottom element) ───────────────────
        Box(
            modifier            = Modifier
                .padding(bottom = 40.dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(MediDarkGreen),
            contentAlignment    = Alignment.Center
        ) {
            // Decorative inner circle — visual-only per Figma
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MediBlobGreen)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── CameraX preview + ZXing QR analyzer ──────────────────────────────────

@Composable
private fun CameraPreview(onQrDetected: (String) -> Unit) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executor, QrCodeAnalyzer(onQrDetected))
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    Log.e("ScanScreen", "CameraX binding failed", e)
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
        val width              = image.width
        val height             = image.height
        val source             = PlanarYUVLuminanceSource(bytes, width, height, 0, 0, width, height, false)
        val bitmap             = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = reader.decode(bitmap)
            onQrDetected(result.text)
        } catch (_: NotFoundException) {
            // No QR found in this frame — silently continue
        } finally {
            image.close()
        }
    }
}