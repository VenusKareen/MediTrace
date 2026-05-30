package com.venus.meditrace.data.repository
import com.venus.meditrace.data.model.GenericResponse
import com.venus.meditrace.data.model.ReportRequest
import android.net.Uri
import com.venus.meditrace.data.api.ApiService
import com.venus.meditrace.data.model.VerificationResult
import com.venus.meditrace.util.Constants
import com.venus.meditrace.util.Resource
import timber.log.Timber

class VerificationRepository(private val api: ApiService) {

    // ── Verification ──────────────────────────────────────────────────────

    suspend fun verifyProduct(
        batchId:   String,
        signature: String
    ): Resource<VerificationResult> = safeApiCall {
        val response = api.verifyProduct(batchId, signature)
        if (response.isSuccessful && response.body() != null) {
            Resource.Success(response.body()!!.flatten())
        } else {
            val serverMsg = response.errorBody()?.string()
                ?.takeIf { it.isNotBlank() }
                ?: "Unable to verify product."
            Resource.Error(serverMsg, code = response.code())
        }
    }

    // ── Product lookup by batchId (no signature) ──────────────────────────

    suspend fun getProductByBatchId(
        batchId: String
    ): Resource<VerificationResult> = safeApiCall {
        val response = api.getProductByBatchId(batchId)
        if (response.isSuccessful && response.body() != null) {
            Resource.Success(response.body()!!.flatten())
        } else {
            val serverMsg = response.errorBody()?.string()
                ?.takeIf { it.isNotBlank() }
                ?: "Product not found."
            Resource.Error(serverMsg, code = response.code())
        }
    }

    // ── QR parsing ────────────────────────────────────────────────────────

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
                    val parts     = rawQr.split(":", limit = 2)
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
        pharmacyName:   String,
        location:       String,
        medicationName: String,
        batchId:        String? = null
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