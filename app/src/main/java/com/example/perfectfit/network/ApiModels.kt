package com.example.perfectfit.network

import com.google.gson.annotations.SerializedName

// API Request/Response models for network communication

data class ApiOrder(
    @SerializedName("_id")
    val id: String? = null,
    val localId: Int? = null,
    val customerId: Int,
    val customerServerId: String? = null,
    val customerName: String,
    val orderDate: String,
    val orderType: String,
    val estimatedDeliveryDate: String,
    val instructions: String,
    val amount: Double,
    val status: String,
    val lastModified: Long
)

data class ApiCustomer(
    @SerializedName("_id")
    val id: String? = null,
    val localId: Int? = null,
    val firstName: String,
    val lastName: String,
    val address: String,
    val mobile: String,
    val alternateMobile: String,
    val birthDate: String,
    val lastModified: Long
)

data class ApiMeasurement(
    @SerializedName("_id")
    val id: String? = null,
    val localId: Int? = null,
    val customerId: Int,
    val customerServerId: String? = null,
    
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
    val blouseHookOn: String = "",
    
    val lastModified: Long
)

// Sync Request/Response wrappers
data class SyncRequest<T>(
    val data: List<T>,
    val lastSyncTimestamp: Long
)

data class SyncResponse<T>(
    val success: Boolean,
    val data: List<T>? = null,
    val message: String? = null,
    val serverTimestamp: Long
)

data class BatchSyncRequest(
    val customers: List<ApiCustomer> = emptyList(),
    val orders: List<ApiOrder> = emptyList(),
    val measurements: List<ApiMeasurement> = emptyList(),
    val lastSyncTimestamp: Long
)

data class BatchSyncResponse(
    val success: Boolean,
    val customers: List<ApiCustomer>? = null,
    val orders: List<ApiOrder>? = null,
    val measurements: List<ApiMeasurement>? = null,
    val message: String? = null,
    val serverTimestamp: Long
)

