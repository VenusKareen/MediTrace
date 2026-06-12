// ─── Data Models ──────────────────────────────────────────────────────────────
// File: data/model/AuthModels.kt

package com.venus.meditrace.data.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "patient"          // "patient" or "pharmacist"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshTokenRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class LogoutRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String
)

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthTokens?
)

data class RefreshResponse(
    val success: Boolean,
    val data: TokenPair?
)

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)

data class ApiError(
    val success: Boolean,
    val message: String
)