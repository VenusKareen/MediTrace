package com.venus.meditrace.data.repository

import android.net.Uri
import com.venus.meditrace.data.api.ApiService
import com.venus.meditrace.data.model.GenericResponse
import com.venus.meditrace.data.model.ReportRequest
import com.venus.meditrace.data.model.VerificationResult
import com.venus.meditrace.util.Constants
import com.venus.meditrace.util.Resource
import timber.log.Timber

/**
 * Single source of truth for product verification and reporting.
 *
 * Production improvements over prototype:
 *  - HTTP error codes are surfaced in [Resource.Error] so the UI can
 *    distinguish a 404 (not found) from a 500 (server error).
 *  - Error bodies are parsed for a server-supplied message before falling
 *    back to a generic string.
 *  - [parseQrCode] validates that neither extracted field is blank, and
 *    logs malformed payloads for debugging.
 *  - All catch blocks log via Timber in debug builds with no PII in release.
 */
class VerificationRepository(private val api: ApiService) {

    // ── Verification ──────────────────────────────────────────────────────

    suspend fun verifyProduct(
        batchId: String,
        signature: String
    ): Resource<VerificationResult> = safeApiCall {
        val response = api.verifyProduct(batchId, signature)
        if (response.isSuccessful && response.body() != null) {
            Resource.Success(response.body()!!)
        } else {
            val serverMsg = response.errorBody()?.string()
                ?.takeIf { it.isNotBlank() }
                ?: "Unable to verify product."
            Resource.Error(serverMsg, code = response.code())
        }
    }

    // ── QR parsing ────────────────────────────────────────────────────────

    /**
     * Extracts (batchId, signature) from a raw QR string.
     *
     * Supported formats:
     *  1. URL  — https://meditrace.app/verify/{batchId}?sig={signature}
     *  2. Colon-delimited — {batchId}:{signature}
     *
     * Returns null if the payload is malformed or either field is blank.
     */
    fun parseQrCode(rawQr: String): Pair<String, String>? {
        if (rawQr.isBlank()) return null
        return try {
            when {
                rawQr.startsWith("http", ignoreCase = true) -> {
                    val uri       = Uri.parse(rawQr)
                    val batchId   = uri.lastPathSegment?.takeIf { it.isNotBlank() }
                        ?: return logAndNull("QR URL missing batchId segment: $rawQr")
                    val signature = uri.getQueryParameter(Constants.QR_SIG_PARAM)
                        ?.takeIf { it.isNotBlank() }
                        ?: return logAndNull("QR URL missing '${Constants.QR_SIG_PARAM}' param: $rawQr")
                    Pair(batchId, signature)
                }
                rawQr.contains(":") -> {
                    val parts = rawQr.split(":", limit = 2)
                    val batchId   = parts.getOrNull(0)?.takeIf { it.isNotBlank() }
                        ?: return logAndNull("Colon-format QR missing batchId: $rawQr")
                    val signature = parts.getOrNull(1)?.takeIf { it.isNotBlank() }
                        ?: return logAndNull("Colon-format QR missing signature: $rawQr")
                    Pair(batchId, signature)
                }
                else -> logAndNull("Unrecognised QR format: $rawQr")
            }
        } catch (e: Exception) {
            Timber.e(e, "parseQrCode threw unexpectedly")
            null
        }
    }

    // ── Reporting ─────────────────────────────────────────────────────────

    suspend fun reportProduct(
        pharmacyName: String,
        location: String,
        medicationName: String,
        batchId: String? = null
    ): Resource<GenericResponse> = safeApiCall {
        val response = api.reportProduct(
            ReportRequest(
                pharmacyName   = pharmacyName,
                location       = location,
                medicationName = medicationName,
                batchId        = batchId
            )
        )
        if (response.isSuccessful && response.body() != null) {
            Resource.Success(response.body()!!)
        } else {
            val serverMsg = response.errorBody()?.string()
                ?.takeIf { it.isNotBlank() }
                ?: "Failed to submit report."
            Resource.Error(serverMsg, code = response.code())
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private suspend fun <T> safeApiCall(
        call: suspend () -> Resource<T>
    ): Resource<T> = try {
        call()
    } catch (e: Exception) {
        Timber.e(e, "API call failed")
        Resource.Error(e.localizedMessage ?: "Network error. Check your connection.")
    }

    private fun logAndNull(msg: String): Nothing? {
        Timber.w(msg)
        return null
    }
}