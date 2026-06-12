// File: data/repository/AuthRepository.kt
package com.venus.meditrace.data.repository

import com.venus.meditrace.data.local.TokenManager
import com.venus.meditrace.data.model.*
import com.venus.meditrace.data.remote.AuthApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) {

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String
    ): AuthResult<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(RegisterRequest(name, email, password, role))
            if (response.isSuccessful) {
                val body = response.body()?.data
                    ?: return@withContext AuthResult.Error("No data returned")
                tokenManager.saveTokens(body.accessToken, body.refreshToken)
                tokenManager.saveUser(body.user.id, body.user.name, body.user.email, body.user.role)
                AuthResult.Success(body.user)
            } else {
                val error = parseError(response.errorBody()?.string())
                AuthResult.Error(error)
            }
        } catch (e: Exception) {
            AuthResult.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun login(email: String, password: String): AuthResult<UserDto> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()?.data
                        ?: return@withContext AuthResult.Error("No data returned")
                    tokenManager.saveTokens(body.accessToken, body.refreshToken)
                    tokenManager.saveUser(body.user.id, body.user.name, body.user.email, body.user.role)
                    AuthResult.Success(body.user)
                } else {
                    val error = parseError(response.errorBody()?.string())
                    AuthResult.Error(error)
                }
            } catch (e: Exception) {
                AuthResult.Error("Network error: ${e.localizedMessage}")
            }
        }

    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            val token = tokenManager.getRefreshToken()
            if (token != null) api.logout(LogoutRequest(token))
        } catch (_: Exception) { }
        tokenManager.clearTokens()
    }

    suspend fun isLoggedIn() = tokenManager.isLoggedIn()

    private fun parseError(body: String?): String {
        return try {
            Gson().fromJson(body, ApiError::class.java).message
        } catch (_: Exception) {
            "Something went wrong"
        }
    }
}