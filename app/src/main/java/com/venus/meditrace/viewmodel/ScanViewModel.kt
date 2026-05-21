package com.venus.meditrace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.venus.meditrace.BuildConfig
import com.venus.meditrace.data.api.RetrofitClient
import com.venus.meditrace.data.model.VerificationResult
import com.venus.meditrace.data.repository.VerificationRepository
import com.venus.meditrace.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class ScanUiState {
    object Idle     : ScanUiState()
    object Scanning : ScanUiState()
    object Loading  : ScanUiState()
    data class Verified(val result: VerificationResult, val batchId: String) : ScanUiState()
    data class NotFound(val rawQr: String = "")                              : ScanUiState()
    data class Error(val message: String)                                    : ScanUiState()
}

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = VerificationRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var lastScannedQr: String? = null

    fun startScanning() {
        _uiState.update { ScanUiState.Scanning }
    }

    /**
     * Called by [QrCodeAnalyzer] when a real QR frame is decoded.
     * Deduplicates back-to-back identical frames before hitting the network.
     */
    fun onQrDetected(rawQr: String) {
        if (rawQr == lastScannedQr) return
        lastScannedQr = rawQr

        val parsed = repo.parseQrCode(rawQr)
        if (parsed == null) {
            Timber.w("QR parse failed for: $rawQr")
            _uiState.update { ScanUiState.NotFound(rawQr) }
            return
        }

        val (batchId, signature) = parsed
        viewModelScope.launch {
            _uiState.update { ScanUiState.Loading }
            when (val result = repo.verifyProduct(batchId, signature)) {
                is Resource.Success -> {
                    _uiState.update { ScanUiState.Verified(result.data, batchId) }
                }
                is Resource.Error -> {
                    Timber.e("Verification failed [${result.code}]: ${result.message}")
                    _uiState.update { ScanUiState.NotFound(rawQr) }
                }
                is Resource.Loading -> Unit
                is Resource.Idle    -> Unit
            }
        }
    }

    fun reset() {
        lastScannedQr = null
        _uiState.update { ScanUiState.Idle }
    }

    // ── Debug/demo helpers — compiled out in release builds ───────────────

    /** Simulates a successful VALID scan. Only available in debug builds. */
    fun setMockResult(result: VerificationResult) {
        check(BuildConfig.DEBUG) { "setMockResult is only available in debug builds" }
        _uiState.update { ScanUiState.Verified(result, batchId = "DEMO-BATCH") }
    }

    /** Simulates a NOT FOUND result. Only available in debug builds. */
    fun setNotFound() {
        check(BuildConfig.DEBUG) { "setNotFound is only available in debug builds" }
        _uiState.update { ScanUiState.NotFound("DEMO-QR") }
    }
}