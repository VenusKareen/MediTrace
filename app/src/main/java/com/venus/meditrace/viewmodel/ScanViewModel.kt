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

// ── UI State ──────────────────────────────────────────────────────────────────

sealed class ScanUiState {
    object Idle     : ScanUiState()
    object Scanning : ScanUiState()
    object Loading  : ScanUiState()
    data class Verified(val result: VerificationResult, val batchId: String) : ScanUiState()
    data class NotFound(val rawQr: String = "")                              : ScanUiState()
    data class Error(val message: String)                                    : ScanUiState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = VerificationRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private var lastScannedQr: String? = null

    // ── Scanning control ──────────────────────────────────────────────────────

    fun startScanning() {
        lastScannedQr = null
        _uiState.update { ScanUiState.Scanning }
    }

    fun resetToIdle() {
        lastScannedQr = null
        _uiState.update { ScanUiState.Idle }
    }

    // ── QR decode → network verify ────────────────────────────────────────────

    fun onQrCodeScanned(rawQr: String) {
        if (rawQr == lastScannedQr) return
        lastScannedQr = rawQr

        val parsed = repo.parseQrCode(rawQr)
        if (parsed == null) {
            Timber.w("onQrCodeScanned: invalid QR format — $rawQr")
            _uiState.update { ScanUiState.NotFound(rawQr) }
            return
        }

        val (batchId, signature) = parsed
        verifyProduct(batchId, signature)
    }

    private fun verifyProduct(batchId: String, signature: String) {
        viewModelScope.launch {
            _uiState.update { ScanUiState.Loading }

            when (val result = repo.verifyProduct(batchId = batchId, signature = signature)) {
                is Resource.Success<*> -> {
                    Timber.d("verifyProduct: success for batchId=$batchId")
                    _uiState.update { ScanUiState.Verified(result.data as VerificationResult, batchId) }
                }
                is Resource.Error -> {
                    Timber.e("verifyProduct: failed [${result.code}] — ${result.message}")
                    if (result.code == 404) {
                        _uiState.update { ScanUiState.NotFound(batchId) }
                    } else {
                        _uiState.update { ScanUiState.Error(result.message ?: "Verification failed.") }
                    }
                }
                is Resource.Loading -> Unit
                is Resource.Idle    -> Unit
            }
        }
    }

    // ── History detail lookup ─────────────────────────────────────────────────

    /**
     * Fetches a product by batchId for the history detail screen.
     * Uses GET products/{batchId} — no signature required.
     * Skips the network call if this product is already loaded.
     */
    fun fetchByBatchId(batchId: String) {
        val current = _uiState.value
        if (current is ScanUiState.Verified && current.batchId == batchId) return

        viewModelScope.launch {
            _uiState.update { ScanUiState.Loading }

            when (val result = repo.getProductByBatchId(batchId)) {
                is Resource.Success<*> -> {
                    Timber.d("fetchByBatchId: success for batchId=$batchId")
                    _uiState.update { ScanUiState.Verified(result.data as VerificationResult, batchId) }
                }
                is Resource.Error -> {
                    Timber.e("fetchByBatchId: failed [${result.code}] — ${result.message}")
                    if (result.code == 404) {
                        _uiState.update { ScanUiState.NotFound(batchId) }
                    } else {
                        _uiState.update { ScanUiState.Error(result.message ?: "Could not load product.") }
                    }
                }
                is Resource.Loading -> Unit
                is Resource.Idle    -> Unit
            }
        }
    }

    // ── Debug helpers (debug builds only) ────────────────────────────────────

    fun simulateVerified(result: VerificationResult, batchId: String) {
        check(BuildConfig.DEBUG) { "simulateVerified is only available in debug builds" }
        _uiState.update { ScanUiState.Verified(result, batchId) }
    }

    fun simulateNotFound() {
        check(BuildConfig.DEBUG) { "simulateNotFound is only available in debug builds" }
        _uiState.update { ScanUiState.NotFound("DEMO-QR") }
    }
}