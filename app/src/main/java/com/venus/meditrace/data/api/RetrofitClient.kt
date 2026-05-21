package com.venus.meditrace.data.api

import android.content.Context
import com.venus.meditrace.BuildConfig
import com.venus.meditrace.util.Constants
import com.venus.meditrace.util.SecurePrefs
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client.
 *
 * Production improvements over prototype:
 *  - Auth interceptor automatically attaches the JWT Bearer token to every
 *    request, so individual call sites never handle token injection.
 *  - Logging is gated behind BuildConfig.DEBUG — no request bodies leak in
 *    release builds.
 *  - Timeouts are pulled from Constants so they are changed in one place.
 *  - Requires application [Context] so it can read the token from
 *    EncryptedSharedPreferences via [SecurePrefs].
 *
 * Initialise once in [MediTraceApp.onCreate] via [RetrofitClient.init].
 */
object RetrofitClient {

    private lateinit var _apiService: ApiService

    /** Must be called before any other member is accessed. */
    fun init(context: Context) {
        val appContext = context.applicationContext
        _apiService = buildRetrofit(appContext).create(ApiService::class.java)
    }

    val apiService: ApiService
        get() = _apiService

    // ── Private builders ──────────────────────────────────────────────────

    private fun buildOkHttp(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                // Attach JWT token when available
                val token = SecurePrefs.getString(context, Constants.KEY_AUTH_TOKEN)
                val request = if (!token.isNullOrBlank()) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private fun buildRetrofit(context: Context): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(buildOkHttp(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}