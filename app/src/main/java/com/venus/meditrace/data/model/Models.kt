package com.venus.meditrace.data.model

import com.google.gson.annotations.SerializedName

data class VerificationResult(
    @SerializedName("status")            val status: String,
    @SerializedName("product_name")      val productName: String?,
    @SerializedName("manufacturer")      val manufacturer: String?,
    @SerializedName("retailer")          val retailer: String?,
    @SerializedName("store_location")    val storeLocation: String?,
    @SerializedName("product_id")        val productId: String?,
    @SerializedName("batch_number")      val batchNumber: String?,
    @SerializedName("active_ingredient") val activeIngredient: String?,
    @SerializedName("strength")          val strength: String?,
    @SerializedName("expiry_date")       val expiryDate: String?,
    @SerializedName("ppb_reg_number")    val ppbRegNumber: String?,
    @SerializedName("message")           val message: String?
)

data class ReportRequest(
    @SerializedName("pharmacy_name")   val pharmacyName: String,
    @SerializedName("location")        val location: String,
    @SerializedName("medication_name") val medicationName: String,
    @SerializedName("batch_id")        val batchId: String? = null
)

data class GenericResponse(
    @SerializedName("message") val message: String,
    @SerializedName("success") val success: Boolean = true
)

data class ScanHistoryItem(
    val productName: String,
    val storeLocation: String,
    val status: String,
    val timestamp: String,
    val batchId: String
)