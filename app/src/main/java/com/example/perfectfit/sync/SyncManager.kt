package com.example.perfectfit.sync

import android.content.Context
import android.util.Log
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.models.Customer
import com.example.perfectfit.models.Measurement
import com.example.perfectfit.models.Order
import com.example.perfectfit.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncManager(private val context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val apiService = RetrofitClient.apiService
    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val TAG = "SyncManager"
        private const val PREF_LAST_SYNC = "last_sync_timestamp"
        
        // Sync result codes
        const val SYNC_SUCCESS = 0
        const val SYNC_PARTIAL = 1
        const val SYNC_FAILED = 2
        const val SYNC_NO_NETWORK = 3
    }
    
    /**
     * Get the last successful sync timestamp
     */
    fun getLastSyncTimestamp(): Long {
        return prefs.getLong(PREF_LAST_SYNC, 0L)
    }
    
    /**
     * Save the last sync timestamp
     */
    private fun saveLastSyncTimestamp(timestamp: Long) {
        prefs.edit().putLong(PREF_LAST_SYNC, timestamp).apply()
    }
    
    /**
     * Main sync method - syncs all data with the server
     * Returns: Sync result code
     */
    suspend fun syncAll(): Int = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting full sync...")
            
            // Check network connectivity
            if (!isNetworkAvailable()) {
                Log.w(TAG, "No network available")
                return@withContext SYNC_NO_NETWORK
            }
            
            // Get unsynced data from local database
            val unsyncedCustomers = database.customerDao().getUnsyncedCustomers()
            val unsyncedOrders = database.orderDao().getUnsyncedOrders()
            val unsyncedMeasurements = database.measurementDao().getUnsyncedMeasurements()
            
            Log.d(TAG, "Found ${unsyncedCustomers.size} unsynced customers")
            Log.d(TAG, "Found ${unsyncedOrders.size} unsynced orders")
            Log.d(TAG, "Found ${unsyncedMeasurements.size} unsynced measurements")
            
            // Convert to API models
            val apiCustomers = unsyncedCustomers.map { it.toApiModel() }
            val apiOrders = unsyncedOrders.map { it.toApiModel() }
            val apiMeasurements = unsyncedMeasurements.map { it.toApiModel() }
            
            // Create batch sync request
            val request = BatchSyncRequest(
                customers = apiCustomers,
                orders = apiOrders,
                measurements = apiMeasurements,
                lastSyncTimestamp = getLastSyncTimestamp()
            )
            
            // Send to server
            val response = apiService.batchSync(request)
            
            if (response.isSuccessful && response.body() != null) {
                val syncResponse = response.body()!!
                
                if (syncResponse.success) {
                    Log.d(TAG, "Sync successful")
                    
                    // Process server responses
                    syncResponse.customers?.let { processCustomerSyncResponse(it) }
                    syncResponse.orders?.let { processOrderSyncResponse(it) }
                    syncResponse.measurements?.let { processMeasurementSyncResponse(it) }
                    
                    // Save sync timestamp
                    saveLastSyncTimestamp(syncResponse.serverTimestamp)
                    
                    return@withContext SYNC_SUCCESS
                } else {
                    Log.e(TAG, "Sync failed: ${syncResponse.message}")
                    return@withContext SYNC_PARTIAL
                }
            } else {
                Log.e(TAG, "Sync request failed: ${response.code()}")
                return@withContext SYNC_FAILED
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Sync error: ${e.message}", e)
            return@withContext SYNC_FAILED
        }
    }
    
    /**
     * Process customer sync responses from server
     */
    private suspend fun processCustomerSyncResponse(apiCustomers: List<ApiCustomer>) {
        for (apiCustomer in apiCustomers) {
            try {
                val localCustomer = apiCustomer.localId?.let {
                    database.customerDao().getCustomerById(it)
                }
                
                if (localCustomer != null && apiCustomer.id != null) {
                    // Update local record with server ID
                    database.customerDao().updateServerInfo(
                        localId = localCustomer.id,
                        serverId = apiCustomer.id,
                        status = Customer.SYNC_SYNCED,
                        timestamp = System.currentTimeMillis()
                    )
                } else if (apiCustomer.id != null) {
                    // New customer from server - insert locally
                    val customer = apiCustomer.toRoomModel()
                    database.customerDao().insertCustomer(customer)
                }
                
                Log.d(TAG, "Processed customer: ${apiCustomer.firstName} ${apiCustomer.lastName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing customer: ${e.message}")
            }
        }
    }
    
    /**
     * Process order sync responses from server
     */
    private suspend fun processOrderSyncResponse(apiOrders: List<ApiOrder>) {
        for (apiOrder in apiOrders) {
            try {
                val localOrder = apiOrder.localId?.let {
                    database.orderDao().getOrderById(it)
                }
                
                if (localOrder != null && apiOrder.id != null) {
                    // Update local record with server ID
                    database.orderDao().updateServerInfo(
                        localId = localOrder.id,
                        serverId = apiOrder.id,
                        status = Order.SYNC_SYNCED,
                        timestamp = System.currentTimeMillis()
                    )
                } else if (apiOrder.id != null) {
                    // New order from server - insert locally
                    val order = apiOrder.toRoomModel()
                    database.orderDao().insert(order)
                }
                
                Log.d(TAG, "Processed order: ${apiOrder.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing order: ${e.message}")
            }
        }
    }
    
    /**
     * Process measurement sync responses from server
     */
    private suspend fun processMeasurementSyncResponse(apiMeasurements: List<ApiMeasurement>) {
        for (apiMeasurement in apiMeasurements) {
            try {
                val localMeasurement = apiMeasurement.localId?.let {
                    database.measurementDao().getMeasurementByCustomerIdSync(it)
                }
                
                if (localMeasurement != null && apiMeasurement.id != null) {
                    // Update local record with server ID
                    database.measurementDao().updateServerInfo(
                        localId = localMeasurement.id,
                        serverId = apiMeasurement.id,
                        status = Measurement.SYNC_SYNCED,
                        timestamp = System.currentTimeMillis()
                    )
                } else if (apiMeasurement.id != null) {
                    // New measurement from server - insert locally
                    val measurement = apiMeasurement.toRoomModel()
                    database.measurementDao().insertMeasurement(measurement)
                }
                
                Log.d(TAG, "Processed measurement: ${apiMeasurement.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error processing measurement: ${e.message}")
            }
        }
    }
    
    /**
     * Check if network is available
     */
    private fun isNetworkAvailable(): Boolean {
        // Simple check - you can enhance this with ConnectivityManager
        return try {
            val command = "ping -c 1 google.com"
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Sync only orders
     */
    suspend fun syncOrders(): Boolean = withContext(Dispatchers.IO) {
        try {
            val unsyncedOrders = database.orderDao().getUnsyncedOrders()
            val apiOrders = unsyncedOrders.map { it.toApiModel() }
            
            val request = SyncRequest(
                data = apiOrders,
                lastSyncTimestamp = getLastSyncTimestamp()
            )
            
            val response = apiService.syncOrders(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { processOrderSyncResponse(it) }
                return@withContext true
            }
            
            false
        } catch (e: Exception) {
            Log.e(TAG, "Order sync failed: ${e.message}")
            false
        }
    }
    
    /**
     * Sync only customers
     */
    suspend fun syncCustomers(): Boolean = withContext(Dispatchers.IO) {
        try {
            val unsyncedCustomers = database.customerDao().getUnsyncedCustomers()
            val apiCustomers = unsyncedCustomers.map { it.toApiModel() }
            
            val request = SyncRequest(
                data = apiCustomers,
                lastSyncTimestamp = getLastSyncTimestamp()
            )
            
            val response = apiService.syncCustomers(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { processCustomerSyncResponse(it) }
                return@withContext true
            }
            
            false
        } catch (e: Exception) {
            Log.e(TAG, "Customer sync failed: ${e.message}")
            false
        }
    }
}

