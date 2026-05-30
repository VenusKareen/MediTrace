package com.venus.meditrace.data.api

import com.venus.meditrace.data.model.GenericResponse
import com.venus.meditrace.data.model.ReportRequest
import com.venus.meditrace.data.model.VerificationResult
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    /**
     * Verify a scanned product.
     * GET verify/{batchId}?sig={signature}
     */
    @GET("verify/{batchId}")
    suspend fun verifyProduct(
        @Path("batchId") batchId:   String,
        @Query("sig")    signature: String
    ): Response<VerificationResult>

    /**
     * Fetch a product by batchId only — no signature required.
     * Used by the history detail screen.
     * GET products/{batchId}
     */
    @GET("products/{batchId}")
    suspend fun getProductByBatchId(
        @Path("batchId") batchId: String
    ): Response<VerificationResult>

    /**
     * Submit a counterfeit / suspicious product report.
     */
    @POST("report")
    suspend fun reportProduct(
        @Body request: ReportRequest
    ): Response<GenericResponse>
}