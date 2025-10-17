package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "measurements",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int,
    
    // Kurti Measurements
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
    
    // Pant Measurements
    val pantWaist: String = "",
    val pantLength: String = "",
    val pantHip: String = "",
    val pantBottom: String = "",
    
    // Blouse Measurements (separate from Kurti)
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
    val blouseHookOn: String = "", // "left" or "right"
    
    // Metadata
    val lastUpdated: Long = System.currentTimeMillis(),
    
    // Sync-related fields
    val serverId: String? = null,
    val lastModified: Long = System.currentTimeMillis(),
    val syncStatus: String = "PENDING"
) {
    companion object {
        const val SYNC_PENDING = "PENDING"
        const val SYNC_SYNCED = "SYNCED"
        const val SYNC_FAILED = "FAILED"
    }
}

