package com.venus.meditrace.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.venus.meditrace.data.model.UserDto
import com.venus.meditrace.data.repository.AuthRepository
import com.venus.meditrace.data.repository.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: UserDto) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = repository.login(email, password)) {
                is AuthResult.Success -> AuthUiState.Success(result.data)
                is AuthResult.Error   -> AuthUiState.Error(result.message)
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = repository.register(name, email, password, role)) {
                is AuthResult.Success -> AuthUiState.Success(result.data)
                is AuthResult.Error   -> AuthUiState.Error(result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch { repository.logout() }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}