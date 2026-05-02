package com.venus.meditrace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.venus.meditrace.data.api.RetrofitClient
import com.venus.meditrace.data.repository.VerificationRepository
import com.venus.meditrace.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportUiState(
    val isLoading: Boolean    = false,
    val isSuccess: Boolean    = false,
    val errorMessage: String? = null
)

class ReportViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = VerificationRepository(RetrofitClient.apiService)

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun submitReport(
        pharmacyName: String,
        location: String,
        medicationName: String,
        batchId: String? = null
    ) {
        if (pharmacyName.isBlank() || location.isBlank() || medicationName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repo.reportProduct(pharmacyName, location, medicationName, batchId)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
                Resource.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}