package com.venus.meditrace.data.api

import com.venus.meditrace.data.model.GenericResponse
import com.venus.meditrace.data.model.ReportRequest
import com.venus.meditrace.data.model.VerificationResult
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /**
     * Verify antibiotic product.
     * Called after QR is scanned — batchId and sig are extracted from the QR URL.
     * No authentication required.
     */
    @GET("verify/{batchId}")
    suspend fun verifyProduct(
        @Path("batchId") batchId: String,
        @Query("sig")    signature: String
    ): Response<VerificationResult>

    /**
     * Report a suspicious / counterfeit product.
     * No authentication required — consumer-facing.
     */
    @POST("report")
    suspend fun reportProduct(
        @Body request: ReportRequest
    ): Response<GenericResponse>
}