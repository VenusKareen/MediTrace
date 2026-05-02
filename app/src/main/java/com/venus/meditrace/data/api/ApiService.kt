package com.venus.meditrace.data.api

import com.venus.meditrace.data.model.GenericResponse
import com.venus.meditrace.data.model.ReportRequest
import com.venus.meditrace.data.model.VerificationResult
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("verify/{batchId}")
    suspend fun verifyProduct(
        @Path("batchId") batchId: String,
        @Query("sig")    signature: String
    ): Response<VerificationResult>

    @POST("report")
    suspend fun reportProduct(
        @Body request: ReportRequest
    ): Response<GenericResponse>
}