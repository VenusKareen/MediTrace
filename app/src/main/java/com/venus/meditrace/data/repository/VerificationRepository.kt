package com.venus.meditrace.data.repository

import android.net.Uri
import com.venus.meditrace.data.api.ApiService
import com.venus.meditrace.data.model.GenericResponse
import com.venus.meditrace.data.model.ReportRequest
import com.venus.meditrace.data.model.VerificationResult
import com.venus.meditrace.util.Resource

class VerificationRepository(private val api: ApiService) {

    /**
     * Verify product using batchId + HMAC signature from scanned QR code.
     */
    suspend fun verifyProduct(batchId: String, signature: String): Resource<VerificationResult> {
        return try {
            val response = api.verifyProduct(batchId, signature)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Unable to verify product. Please try again.")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage ?: "Check your connection."}")
        }
    }

    /**
     * Parse a raw QR string into (batchId, signature).
     *
     * Supports two formats:
     *   1. Full URL  — https://meditrace.ke/api/verify/{batchId}?sig={signature}
     *   2. Compact   — {batchId}:{signature}
     *
     * Returns null if the QR cannot be parsed (→ show Not Found screen).
     */
    fun parseQrCode(rawQr: String): Pair<String, String>? {
        return try {
            when {
                rawQr.startsWith("http") -> {
                    val uri       = Uri.parse(rawQr)
                    val batchId   = uri.lastPathSegment ?: return null
                    val signature = uri.getQueryParameter("sig") ?: return null
                    Pair(batchId, signature)
                }
                rawQr.contains(":") -> {
                    val parts = rawQr.split(":", limit = 2)
                    if (parts.size == 2) Pair(parts[0], parts[1]) else null
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Report a suspicious / counterfeit product.
     */
    suspend fun reportProduct(
        pharmacyName: String,
        location: String,
        medicationName: String,
        batchId: String? = null
    ): Resource<GenericResponse> {
        return try {
            val response = api.reportProduct(
                ReportRequest(
                    pharmacyName   = pharmacyName.trim(),
                    location       = location.trim(),
                    medicationName = medicationName.trim(),
                    batchId        = batchId
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to submit report. Please try again.")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage}")
        }
    }
}