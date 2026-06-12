package com.venus.meditrace.data.remote

import com.venus.meditrace.data.model.AuthResponse
import com.venus.meditrace.data.model.LoginRequest
import com.venus.meditrace.data.model.LogoutRequest
import com.venus.meditrace.data.model.RefreshResponse
import com.venus.meditrace.data.model.RefreshTokenRequest
import com.venus.meditrace.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshResponse>

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<Unit>

    @GET("auth/me")
    suspend fun getMe(): Response<AuthResponse>
}