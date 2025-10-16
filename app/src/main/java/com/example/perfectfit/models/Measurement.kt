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
    
    // Blouse Measurements
    val blouseLength: String = "",
    
    // Metadata
    val lastUpdated: Long = System.currentTimeMillis()
)

