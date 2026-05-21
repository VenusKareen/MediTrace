package com.venus.meditrace.data.api

import com.venus.meditrace.data.model.GenericResponse
import com.venus.meditrace.data.model.ReportRequest
import com.venus.meditrace.data.model.VerificationResult
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /**
     * Verify a scanned product.
     *
     * @param batchId   The batch identifier extracted from the QR code path.
     * @param signature The HMAC-SHA256 signature from the QR code query param.
     */
    @GET("verify/{batchId}")
    suspend fun verifyProduct(
        @Path("batchId") batchId: String,
        @Query("sig")    signature: String
    ): Response<VerificationResult>

    /**
     * Submit a counterfeit / suspicious product report.
     */
    @POST("report")
    suspend fun reportProduct(
        @Body request: ReportRequest
    ): Response<GenericResponse>
}