package com.venus.meditrace.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// ─────────────────────────────────────────────────────────────────────────────
// Network response models
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Unified model for both API responses:
 *
 * 1. GET verify/{batchId}?sig={sig}
 *    { success, result, message, batch: { batch_number, product_name, ... } }
 *
 * 2. GET products/{batchId}
 *    { success, product: { batch_number, product_name, ... } }
 *
 * Call .flatten() after parsing to get a consistent display-ready object.
 */
@Parcelize
data class VerificationResult(
    // verify endpoint: "result" = "valid" / "expired" / "counterfeit"
    @SerializedName("result")            val status:           String?     = null,
    @SerializedName("message")           val message:          String?     = null,

    // verify endpoint wraps data in "batch"
    @SerializedName("batch")             val batch:            BatchInfo?  = null,

    // products endpoint wraps data in "product"
    @SerializedName("product")           val product:          ProductInfo? = null,

    // flat fields (present in some responses directly)
    @SerializedName("product_name")      val productName:      String?     = null,
    @SerializedName("active_ingredient") val activeIngredient: String?     = null,
    @SerializedName("strength")          val strength:         String?     = null,
    @SerializedName("manufacturer_name") val manufacturer:     String?     = null,
    @SerializedName("ppb_reg_number")    val ppbRegNumber:     String?     = null,
    @SerializedName("retailer")          val retailer:         String?     = null,
    @SerializedName("store_location")    val storeLocation:    String?     = null,
    @SerializedName("expiry_date")       val expiryDate:       String?     = null,
    @SerializedName("product_id")        val productId:        String?     = null,
    @SerializedName("batch_number")      val batchNumber:      String?     = null,
) : Parcelable {

    /**
     * Normalises both API response shapes into a flat display-ready object.
     * Always call this in the repository before returning to the ViewModel.
     */
    fun flatten(): VerificationResult {
        val b = batch
        val p = product
        return VerificationResult(
            status          = status          ?: if (p != null) "valid" else null,
            message         = message,
            productName     = productName     ?: b?.productName     ?: p?.productName,
            activeIngredient= activeIngredient?: b?.activeIngredient?: p?.activeIngredient,
            strength        = strength        ?: b?.strength        ?: p?.strength,
            manufacturer    = manufacturer    ?: b?.manufacturer    ?: p?.manufacturer,
            ppbRegNumber    = ppbRegNumber    ?: b?.ppbRegNumber    ?: p?.ppbRegNumber,
            retailer        = retailer        ?: b?.retailer        ?: p?.retailer,
            storeLocation   = storeLocation   ?: b?.storeLocation   ?: p?.storeLocation,
            expiryDate      = expiryDate      ?: b?.expiryDate      ?: p?.expiryDate,
            productId       = productId       ?: b?.productId       ?: p?.productId,
            batchNumber     = batchNumber     ?: b?.batchNumber     ?: p?.batchNumber,
        )
    }
}

@Parcelize
data class BatchInfo(
    @SerializedName("batch_number")      val batchNumber:      String? = null,
    @SerializedName("product_name")      val productName:      String? = null,
    @SerializedName("active_ingredient") val activeIngredient: String? = null,
    @SerializedName("strength")          val strength:         String? = null,
    @SerializedName("manufacturer_name") val manufacturer:     String? = null,
    @SerializedName("ppb_reg_number")    val ppbRegNumber:     String? = null,
    @SerializedName("retailer")          val retailer:         String? = null,
    @SerializedName("store_location")    val storeLocation:    String? = null,
    @SerializedName("expiry_date")       val expiryDate:       String? = null,
    @SerializedName("product_id")        val productId:        String? = null,
) : Parcelable

@Parcelize
data class ProductInfo(
    @SerializedName("batch_number")      val batchNumber:      String? = null,
    @SerializedName("product_name")      val productName:      String? = null,
    @SerializedName("active_ingredient") val activeIngredient: String? = null,
    @SerializedName("strength")          val strength:         String? = null,
    @SerializedName("manufacturer")      val manufacturer:     String? = null,
    @SerializedName("ppb_reg_number")    val ppbRegNumber:     String? = null,
    @SerializedName("retailer")          val retailer:         String? = null,
    @SerializedName("store_location")    val storeLocation:    String? = null,
    @SerializedName("expiry_date")       val expiryDate:       String? = null,
    @SerializedName("product_id")        val productId:        String? = null,
) : Parcelable

/** Body for POST /report */
data class ReportRequest(
    @SerializedName("pharmacy_name")   val pharmacyName:   String,
    @SerializedName("location")        val location:       String,
    @SerializedName("medication_name") val medicationName: String,
    @SerializedName("batch_id")        val batchId:        String? = null
)

/** Generic API acknowledgement */
data class GenericResponse(
    @SerializedName("message") val message: String,
    @SerializedName("success") val success: Boolean = true
)

// ─────────────────────────────────────────────────────────────────────────────
// Local persistence model (Room)
// ─────────────────────────────────────────────────────────────────────────────

@Entity(tableName = "scan_history")
data class ScanHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName:    String,
    val storeLocation:  String,
    val status:         String,
    val timestampMillis:Long = System.currentTimeMillis(),
    val batchId:        String
)