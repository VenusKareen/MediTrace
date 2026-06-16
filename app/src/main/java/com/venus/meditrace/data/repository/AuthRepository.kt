package com.venus.meditrace.data.repository

import android.content.Context
import com.venus.meditrace.data.local.TokenManager
import com.venus.meditrace.data.model.*
import com.venus.meditrace.data.remote.AuthApiService
import com.venus.meditrace.util.Constants
import com.venus.meditrace.util.SecurePrefs
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository(
    private val api:          AuthApiService,
    private val tokenManager: TokenManager,
    private val context:      Context          // needed to write SecurePrefs
) {

    suspend fun register(
        name:     String,
        email:    String,
        password: String,
        role:     String
    ): AuthResult<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.register(RegisterRequest(name, email, password, role))
            if (response.isSuccessful) {
                val body = response.body()?.data
                    ?: return@withContext AuthResult.Error("No data returned")
                persistSession(body)
                AuthResult.Success(body.user)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            AuthResult.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun login(
        email:    String,
        password: String
    ): AuthResult<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()?.data
                    ?: return@withContext AuthResult.Error("No data returned")
                persistSession(body)
                AuthResult.Success(body.user)
            } else {
                AuthResult.Error(parseError(response.errorBody()?.string()))
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

        // Clear both storage systems
        tokenManager.clearTokens()
        SecurePrefs.clearAll(context)
    }

    suspend fun isLoggedIn() = tokenManager.isLoggedIn()

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Saves session data to BOTH storage systems so they stay in sync:
     * - TokenManager (DataStore)  → used by data/remote/RetrofitClient (auth token refresh)
     * - SecurePrefs (EncryptedSharedPreferences) → read by data/api/RetrofitClient
     *   interceptor to attach Bearer token to every ApiService call (verify, report, etc.)
     */
    private suspend fun persistSession(body: AuthTokens) {
        // DataStore — for session check + token refresh flow
        tokenManager.saveTokens(body.accessToken, body.refreshToken)
        tokenManager.saveUser(body.user.id, body.user.name, body.user.email, body.user.role)

        // SecurePrefs — for RetrofitClient.apiService auth interceptor
        SecurePrefs.putString(context, Constants.KEY_AUTH_TOKEN, body.accessToken)
        SecurePrefs.putString(context, Constants.KEY_USER_ID,    body.user.id)
        SecurePrefs.putString(context, Constants.KEY_USER_NAME,  body.user.name)
        SecurePrefs.putString(context, Constants.KEY_USER_EMAIL, body.user.email)
        SecurePrefs.putString(context, Constants.KEY_USER_ROLE,  body.user.role)
    }

    private fun parseError(body: String?): String = try {
        Gson().fromJson(body, ApiError::class.java).message
    } catch (_: Exception) {
        "Something went wrong"
    }
}