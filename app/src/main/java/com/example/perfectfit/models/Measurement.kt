package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents body measurements for a customer in the Perfect Fit tailoring application.
 * 
 * This entity stores comprehensive measurements for three garment types: Kurti, Pant, and Blouse.
 * Measurements are linked to customers via a foreign key with CASCADE delete, meaning
 * deleting a customer will automatically delete their measurements.
 * 
 * All measurement fields are stored as strings to accommodate various input formats
 * (e.g., "36", "36.5", "36-37"). Empty strings indicate measurements not yet taken.
 * 
 * @property id Auto-generated local database ID (primary key)
 * @property customerId Foreign key reference to the Customer
 * 
 * Kurti Measurements (19 fields):
 * @property kurtiLength Length of the kurti from shoulder to hem
 * @property fullShoulder Full shoulder width measurement
 * @property upperChestRound Upper chest circumference
 * @property chestRound Chest/bust circumference
 * @property waistRound Waist circumference
 * @property shoulderToApex Distance from shoulder to bust apex
 * @property apexToApex Distance between bust apexes
 * @property shoulderToLowChestLength Length from shoulder to lower chest
 * @property skapLength Skap (dart) length
 * @property skapLengthRound Skap circumference measurement
 * @property hipRound Hip circumference
 * @property frontNeckDeep Front neck depth
 * @property frontNeckWidth Front neck width
 * @property backNeckDeep Back neck depth
 * @property readyShoulder Ready-made shoulder measurement
 * @property sleevesHeightShort Short sleeve length
 * @property sleevesHeightElbow Elbow-length sleeve measurement
 * @property sleevesHeightThreeQuarter Three-quarter sleeve length
 * @property sleevesRound Sleeve circumference at armhole
 * 
 * Pant Measurements (4 fields):
 * @property pantWaist Pant waist circumference
 * @property pantLength Pant length from waist to hem
 * @property pantHip Pant hip circumference
 * @property pantBottom Pant bottom/ankle width
 * 
 * Blouse Measurements (16 fields - separate from Kurti):
 * @property blouseLength Blouse length from shoulder to hem
 * @property blouseFullShoulder Full shoulder width for blouse
 * @property blouseChest Blouse chest/bust circumference
 * @property blouseWaist Blouse waist circumference
 * @property blouseShoulderToApex Distance from shoulder to bust apex
 * @property blouseApexToApex Distance between bust apexes
 * @property blouseBackLength Back length measurement
 * @property blouseFrontNeckDeep Front neck depth
 * @property blouseFrontNeckWidth Front neck width
 * @property blouseBackNeckDeep Back neck depth
 * @property blouseReadyShoulder Ready-made shoulder measurement
 * @property blouseSleevesHeightShort Short sleeve length
 * @property blouseSleevesHeightElbow Elbow-length sleeve measurement
 * @property blouseSleevesHeightThreeQuarter Three-quarter sleeve length
 * @property blouseSleevesRound Sleeve circumference at armhole
 * @property blouseHookOn Hook position: "left" or "right"
 * 
 * @property lastUpdated Timestamp when measurements were last updated (milliseconds)
 * @property serverId MongoDB _id from the remote server (null until first sync)
 * @property lastModified Timestamp for conflict resolution during sync (milliseconds)
 * @property syncStatus Current synchronization state (PENDING/SYNCED/FAILED)
 * 
 * @see [Customer] for the customer these measurements belong to
 */
@Entity(
    tableName = "measurements",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE  // Delete measurements when customer is deleted
        )
    ],
    indices = [
        Index("customerId")  // Index for efficient customer measurement lookups
    ]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val customerId: Int,
    
    // ===== Kurti Measurements (19 fields) =====
    val kurtiLength: String = "",
    val fullShoulder: String = "",
    val upperChestRound: String = "",
    val chestRound: String = "",
    val waistRound: String = "",
    val shoulderToApex: String = "",
    val apexToApex: String = "",
    val shoulderToLowChestLength: String = "",
    val skapLength: String = "",
    val skapLengthRound: String = "",
    val hipRound: String = "",
    val frontNeckDeep: String = "",
    val frontNeckWidth: String = "",
    val backNeckDeep: String = "",
    val readyShoulder: String = "",
    val sleevesHeightShort: String = "",
    val sleevesHeightElbow: String = "",
    val sleevesHeightThreeQuarter: String = "",
    val sleevesRound: String = "",
    
    // ===== Pant Measurements (4 fields) =====
    val pantWaist: String = "",
    val pantLength: String = "",
    val pantHip: String = "",
    val pantBottom: String = "",
    
    // ===== Blouse Measurements (16 fields - separate from Kurti) =====
    val blouseLength: String = "",
    val blouseFullShoulder: String = "",
    val blouseChest: String = "",
    val blouseWaist: String = "",
    val blouseShoulderToApex: String = "",
    val blouseApexToApex: String = "",
    val blouseBackLength: String = "",
    val blouseFrontNeckDeep: String = "",
    val blouseFrontNeckWidth: String = "",
    val blouseBackNeckDeep: String = "",
    val blouseReadyShoulder: String = "",
    val blouseSleevesHeightShort: String = "",
    val blouseSleevesHeightElbow: String = "",
    val blouseSleevesHeightThreeQuarter: String = "",
    val blouseSleevesRound: String = "",
    val blouseHookOn: String = "",  // "left" or "right"
    
    // ===== Metadata =====
    val lastUpdated: Long = System.currentTimeMillis(),
    
    // ===== Sync-related fields =====
    val serverId: String? = null,
    val lastModified: Long = System.currentTimeMillis(),
    val syncStatus: String = SYNC_PENDING
) {
    /**
     * Checks if kurti measurements have been entered.
     * @return true if any kurti measurement field is not empty
     */
    fun hasKurtiMeasurements(): Boolean {
        return kurtiLength.isNotEmpty() || fullShoulder.isNotEmpty() ||
               upperChestRound.isNotEmpty() || chestRound.isNotEmpty() ||
               waistRound.isNotEmpty()
    }
    
    /**
     * Checks if pant measurements have been entered.
     * @return true if any pant measurement field is not empty
     */
    fun hasPantMeasurements(): Boolean {
        return pantWaist.isNotEmpty() || pantLength.isNotEmpty() ||
               pantHip.isNotEmpty() || pantBottom.isNotEmpty()
    }
    
    /**
     * Checks if blouse measurements have been entered.
     * @return true if any blouse measurement field is not empty
     */
    fun hasBlouseMeasurements(): Boolean {
        return blouseLength.isNotEmpty() || blouseFullShoulder.isNotEmpty() ||
               blouseChest.isNotEmpty() || blouseWaist.isNotEmpty()
    }
    
    /**
     * Checks if any measurements have been recorded.
     * @return true if any measurement category has data
     */
    fun hasAnyMeasurements(): Boolean {
        return hasKurtiMeasurements() || hasPantMeasurements() || hasBlouseMeasurements()
    }
    
    /**
     * Calculates the completeness percentage for kurti measurements.
     * @return Percentage (0-100) of kurti fields that are filled
     */
    fun kurtiCompletenessPercentage(): Int {
        val totalKurtiFields = 19
        val filledFields = listOf(
            kurtiLength, fullShoulder, upperChestRound, chestRound, waistRound,
            shoulderToApex, apexToApex, shoulderToLowChestLength, skapLength,
            skapLengthRound, hipRound, frontNeckDeep, frontNeckWidth,
            backNeckDeep, readyShoulder, sleevesHeightShort, sleevesHeightElbow,
            sleevesHeightThreeQuarter, sleevesRound
        ).count { it.isNotEmpty() }
        
        return (filledFields * 100) / totalKurtiFields
    }
    
    /**
     * Returns a copy of this measurement with updated lastModified timestamp.
     * Useful when making modifications that need to trigger a sync.
     */
    fun withUpdatedTimestamp(): Measurement {
        return copy(
            lastUpdated = System.currentTimeMillis(),
            lastModified = System.currentTimeMillis(),
            syncStatus = SYNC_PENDING
        )
    }
    
    companion object {
        // Synchronization status constants
        const val SYNC_PENDING = "PENDING"  // Waiting to be synced to server
        const val SYNC_SYNCED = "SYNCED"    // Successfully synced with server
        const val SYNC_FAILED = "FAILED"    // Sync attempt failed, will retry
        
        // Hook position constants for blouse
        const val HOOK_LEFT = "left"
        const val HOOK_RIGHT = "right"
    }
}

