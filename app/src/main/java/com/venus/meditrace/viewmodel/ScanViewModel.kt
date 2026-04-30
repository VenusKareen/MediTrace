package com.venus.meditrace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.venus.meditrace.data.api.RetrofitClient
import com.venus.meditrace.data.model.VerificationResult
import com.venus.meditrace.data.repository.VerificationRepository
import com.venus.meditrace.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ScanUiState {
    object Idle        : ScanUiState()
    object Scanning    : ScanUiState()   // camera active, waiting for QR
    object Loading     : ScanUiState()   // API call in progress
    data class Verified(val result: VerificationResult) : ScanUiState()
    data class NotFound(val rawQr: String)              : ScanUiState()
    data class Error(val message: String)               : ScanUiState()
}

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = VerificationRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    // Debounce: ignore duplicate QR reads
    private var lastScannedQr: String? = null

    fun startScanning() {
        _uiState.update { ScanUiState.Scanning }
    }

    /**
     * Triggered by the CameraX QR analyzer when a QR code is detected.
     */
    fun onQrDetected(rawQr: String) {
        if (rawQr == lastScannedQr) return
        lastScannedQr = rawQr

        val parsed = repo.parseQrCode(rawQr)

        if (parsed == null) {
            // QR was readable but not a MediTrace format
            _uiState.update { ScanUiState.NotFound(rawQr) }
            return
        }

        val (batchId, signature) = parsed
        viewModelScope.launch {
            _uiState.update { ScanUiState.Loading }
            when (val result = repo.verifyProduct(batchId, signature)) {
                is Resource.Success -> _uiState.update { ScanUiState.Verified(result.data) }
                is Resource.Error   -> _uiState.update { ScanUiState.NotFound(rawQr) }
                Resource.Loading    -> Unit
            }
        }
    }

    fun reset() {
        lastScannedQr = null
        _uiState.update { ScanUiState.Idle }
    }
}