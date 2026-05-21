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
 * Returned by GET /verify/{batchId}?sig=...
 *
 * Made Parcelable so it can be passed directly through the navigation
 * back-stack without requiring a shared ViewModel.
 */
@Parcelize
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
) : Parcelable

/** Body for POST /report */
data class ReportRequest(
    @SerializedName("pharmacy_name")   val pharmacyName: String,
    @SerializedName("location")        val location: String,
    @SerializedName("medication_name") val medicationName: String,
    @SerializedName("batch_id")        val batchId: String? = null
)

/** Generic API acknowledgement */
data class GenericResponse(
    @SerializedName("message") val message: String,
    @SerializedName("success") val success: Boolean = true
)

// ─────────────────────────────────────────────────────────────────────────────
// Local persistence model (Room)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Persisted scan-history record stored in the local Room database.
 *
 * [id] uses auto-generation so inserts never require a caller-supplied key.
 * [timestampMillis] stores epoch-millis for reliable sorting; format for
 * display in the UI layer, not here.
 */
@Entity(tableName = "scan_history")
data class ScanHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val storeLocation: String,
    val status: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val batchId: String
)