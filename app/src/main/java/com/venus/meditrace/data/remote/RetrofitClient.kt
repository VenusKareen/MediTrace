// File: data/remote/RetrofitClient.kt
package com.venus.meditrace.data.remote

import com.venus.meditrace.BuildConfig
import com.venus.meditrace.data.local.TokenManager
import com.venus.meditrace.data.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    fun create(tokenManager: TokenManager): AuthApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = runBlocking { tokenManager.getAccessToken() }
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .authenticator(TokenAuthenticator(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}

class TokenAuthenticator(private val tokenManager: TokenManager) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null

        val refreshToken = runBlocking { tokenManager.getRefreshToken() } ?: return null

        return try {
            val api = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthApiService::class.java)

            val result = runBlocking { api.refreshToken(RefreshTokenRequest(refreshToken)) }

            if (result.isSuccessful) {
                val tokens = result.body()?.data ?: return null
                runBlocking { tokenManager.saveTokens(tokens.accessToken, tokens.refreshToken) }
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokens.accessToken}")
                    .build()
            } else {
                runBlocking { tokenManager.clearTokens() }
                null
            }
        } catch (e: Exception) {
            runBlocking { tokenManager.clearTokens() }
            null
        }
    }
}