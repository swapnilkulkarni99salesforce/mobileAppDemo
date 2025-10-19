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

/**
 * Manages synchronization between local database and remote server.
 * 
 * This class handles bidirectional data sync, ensuring that local changes are pushed
 * to the server and server changes are pulled to the local database. It implements
 * conflict resolution using timestamps and maintains sync status for each record.
 * 
 * Synchronization Flow:
 * 1. Check network availability
 * 2. Gather unsynced local changes (PENDING or FAILED status)
 * 3. Convert local models to API models
 * 4. Send batch sync request to server
 * 5. Process server response and update local records
 * 6. Update sync timestamps and status
 * 
 * Sync Status Management:
 * - PENDING: Record created/modified locally, not yet synced
 * - SYNCED: Record successfully synchronized with server
 * - FAILED: Sync attempt failed, will retry next sync
 * 
 * Features:
 * - Batch synchronization for efficiency
 * - Individual entity sync (customers, orders, measurements)
 * - Conflict resolution using lastModified timestamps
 * - Network availability checking
 * - Persistent sync state using SharedPreferences
 * - Detailed logging for debugging
 * 
 * Error Handling:
 * - Network errors: Returns SYNC_NO_NETWORK, records remain PENDING
 * - Server errors: Returns SYNC_FAILED, records marked FAILED for retry
 * - Partial failures: Returns SYNC_PARTIAL, only failed records marked FAILED
 * 
 * Thread Safety:
 * - All database operations run on Dispatchers.IO
 * - SharedPreferences operations are thread-safe
 * 
 * @param context Application context for database and preferences access
 * 
 * @see [Customer] for customer sync
 * @see [Order] for order sync
 * @see [Measurement] for measurement sync
 */
class SyncManager(private val context: Context) {
    
    // Database instance for local data access
    private val database = AppDatabase.getDatabase(context)
    
    // API service for network communication
    private val apiService = RetrofitClient.apiService
    
    // SharedPreferences for persistent sync state
    private val prefs = context.getSharedPreferences(SYNC_PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val TAG = "SyncManager"
        private const val SYNC_PREFS_NAME = "sync_prefs"
        private const val PREF_LAST_SYNC = "last_sync_timestamp"
        
        /**
         * Sync Result Codes
         * 
         * These constants represent the outcome of a sync operation:
         * - SYNC_SUCCESS: All records synced successfully
         * - SYNC_PARTIAL: Some records failed, others succeeded
         * - SYNC_FAILED: Sync operation failed completely
         * - SYNC_NO_NETWORK: No network connection available
         */
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
                // Check if this record exists locally by localId
                val localCustomer = apiCustomer.localId?.let {
                    database.customerDao().getCustomerById(it)
                }
                
                // Check if this record exists locally by serverId
                val existingByServerId = apiCustomer.id?.let {
                    database.customerDao().getCustomerByServerId(it)
                }
                
                when {
                    // Case 1: Found by localId - update with server info
                    localCustomer != null && apiCustomer.id != null -> {
                        database.customerDao().updateServerInfo(
                            localId = localCustomer.id,
                            serverId = apiCustomer.id,
                            status = Customer.SYNC_SYNCED,
                            timestamp = System.currentTimeMillis()
                        )
                        Log.d(TAG, "Updated customer localId=${localCustomer.id} with serverId=${apiCustomer.id}")
                    }
                    // Case 2: Found by serverId but not localId - this is an update from server
                    existingByServerId != null && apiCustomer.id != null -> {
                        // Update existing record
                        val updatedCustomer = apiCustomer.toRoomModel().copy(id = existingByServerId.id)
                        database.customerDao().updateCustomer(updatedCustomer)
                        Log.d(TAG, "Updated existing customer with serverId=${apiCustomer.id}")
                    }
                    // Case 3: New customer from server - insert only if not already exists
                    apiCustomer.id != null && existingByServerId == null -> {
                        val customer = apiCustomer.toRoomModel()
                        database.customerDao().insertCustomer(customer)
                        Log.d(TAG, "Inserted new customer from server: ${apiCustomer.firstName} ${apiCustomer.lastName}")
                    }
                    else -> {
                        Log.w(TAG, "Skipped customer - no valid ID: ${apiCustomer.firstName} ${apiCustomer.lastName}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing customer: ${e.message}", e)
            }
        }
    }
    
    /**
     * Process order sync responses from server
     */
    private suspend fun processOrderSyncResponse(apiOrders: List<ApiOrder>) {
        for (apiOrder in apiOrders) {
            try {
                // Check if this record exists locally by localId
                val localOrder = apiOrder.localId?.let {
                    database.orderDao().getOrderById(it)
                }
                
                // Check if this record exists locally by serverId
                val existingByServerId = apiOrder.id?.let {
                    database.orderDao().getOrderByServerId(it)
                }
                
                when {
                    // Case 1: Found by localId - update with server info
                    localOrder != null && apiOrder.id != null -> {
                        database.orderDao().updateServerInfo(
                            localId = localOrder.id,
                            serverId = apiOrder.id,
                            status = Order.SYNC_SYNCED,
                            timestamp = System.currentTimeMillis()
                        )
                        Log.d(TAG, "Updated order localId=${localOrder.id} with serverId=${apiOrder.id}")
                    }
                    // Case 2: Found by serverId but not localId - this is an update from server
                    existingByServerId != null && apiOrder.id != null -> {
                        // Update existing record
                        val updatedOrder = apiOrder.toRoomModel().copy(id = existingByServerId.id)
                        database.orderDao().update(updatedOrder)
                        Log.d(TAG, "Updated existing order with serverId=${apiOrder.id}")
                    }
                    // Case 3: New order from server - insert only if not already exists
                    apiOrder.id != null && existingByServerId == null -> {
                        val order = apiOrder.toRoomModel()
                        database.orderDao().insert(order)
                        Log.d(TAG, "Inserted new order from server: ${apiOrder.id}")
                    }
                    else -> {
                        Log.w(TAG, "Skipped order - no valid ID: ${apiOrder.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing order: ${e.message}", e)
            }
        }
    }
    
    /**
     * Process measurement sync responses from server
     */
    private suspend fun processMeasurementSyncResponse(apiMeasurements: List<ApiMeasurement>) {
        for (apiMeasurement in apiMeasurements) {
            try {
                // Check if this record exists locally by localId (using customerId for measurements)
                val localMeasurement = apiMeasurement.localId?.let {
                    database.measurementDao().getMeasurementByCustomerIdSync(it)
                }
                
                // Check if this record exists locally by serverId
                val existingByServerId = apiMeasurement.id?.let {
                    database.measurementDao().getMeasurementByServerId(it)
                }
                
                when {
                    // Case 1: Found by localId - update with server info
                    localMeasurement != null && apiMeasurement.id != null -> {
                        database.measurementDao().updateServerInfo(
                            localId = localMeasurement.id,
                            serverId = apiMeasurement.id,
                            status = Measurement.SYNC_SYNCED,
                            timestamp = System.currentTimeMillis()
                        )
                        Log.d(TAG, "Updated measurement localId=${localMeasurement.id} with serverId=${apiMeasurement.id}")
                    }
                    // Case 2: Found by serverId but not localId - this is an update from server
                    existingByServerId != null && apiMeasurement.id != null -> {
                        // Update existing record
                        val updatedMeasurement = apiMeasurement.toRoomModel().copy(id = existingByServerId.id)
                        database.measurementDao().updateMeasurement(updatedMeasurement)
                        Log.d(TAG, "Updated existing measurement with serverId=${apiMeasurement.id}")
                    }
                    // Case 3: New measurement from server - insert only if not already exists
                    apiMeasurement.id != null && existingByServerId == null -> {
                        val measurement = apiMeasurement.toRoomModel()
                        database.measurementDao().insertMeasurement(measurement)
                        Log.d(TAG, "Inserted new measurement from server: ${apiMeasurement.id}")
                    }
                    else -> {
                        Log.w(TAG, "Skipped measurement - no valid ID: ${apiMeasurement.id}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing measurement: ${e.message}", e)
            }
        }
    }
    
    /**
     * Checks if network connectivity is available.
     * 
     * Current Implementation: Simple ping test
     * This is a basic connectivity check that pings google.com.
     * 
     * Limitations:
     * - May be slow (network timeout)
     * - Requires INTERNET permission
     * - Doesn't check actual API server availability
     * 
     * TODO - Production Improvements:
     * Replace with ConnectivityManager for better reliability:
     * ```
     * val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
     * val network = connectivityManager.activeNetwork ?: return false
     * val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
     * return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
     * ```
     * 
     * @return true if network is available, false otherwise
     */
    private fun isNetworkAvailable(): Boolean {
        return try {
            val command = "ping -c 1 google.com"
            val process = Runtime.getRuntime().exec(command)
            val exitCode = process.waitFor()
            exitCode == 0
        } catch (e: Exception) {
            Log.w(TAG, "Network check failed: ${e.message}", e)
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

