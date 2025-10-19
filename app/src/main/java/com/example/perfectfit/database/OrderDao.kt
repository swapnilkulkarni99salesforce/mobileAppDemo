package com.example.perfectfit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.Order

/**
 * Data Access Object (DAO) for Order entity operations.
 * 
 * This interface provides methods for managing orders in the database,
 * including CRUD operations and synchronization queries. Room automatically
 * generates the implementation at compile time.
 * 
 * All suspend functions are designed to be called from coroutines to avoid
 * blocking the main thread during database operations.
 * 
 * Note: Orders are automatically deleted when their associated customer is deleted
 * due to the CASCADE foreign key constraint.
 */
@Dao
interface OrderDao {
    
    // ===== Basic CRUD Operations =====
    
    /**
     * Inserts a new order into the database.
     * 
     * @param order The order to insert
     * @return The row ID of the inserted order
     * @throws android.database.sqlite.SQLiteConstraintException if foreign key constraint fails
     */
    @Insert
    suspend fun insert(order: Order): Long
    
    /**
     * Updates an existing order in the database.
     * The order must have a valid ID.
     * 
     * @param order The order with updated values
     */
    @Update
    suspend fun update(order: Order)
    
    /**
     * Deletes an order by its ID.
     * 
     * @param orderId The ID of the order to delete
     */
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Int)
    
    // ===== Query Operations =====
    
    /**
     * Retrieves all orders, sorted by order date in descending order (newest first).
     * 
     * @return List of all orders
     */
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    suspend fun getAllOrders(): List<Order>
    
    /**
     * Retrieves all orders for a specific customer, sorted by order date descending.
     * 
     * @param customerId The customer's local database ID
     * @return List of orders for the customer
     */
    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY orderDate DESC")
    suspend fun getOrdersByCustomerId(customerId: Int): List<Order>
    
    /**
     * Retrieves a single order by its ID.
     * 
     * @param orderId The order's local database ID
     * @return The order if found, null otherwise
     */
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): Order?
    
    // ===== Synchronization Queries =====
    
    /**
     * Retrieves all orders with a specific synchronization status.
     * 
     * @param status The sync status to filter by (PENDING/SYNCED/FAILED)
     * @return List of orders matching the status
     */
    @Query("SELECT * FROM orders WHERE syncStatus = :status")
    suspend fun getOrdersBySyncStatus(status: String): List<Order>
    
    /**
     * Retrieves all orders that need to be synced to the server.
     * Includes both PENDING (never synced) and FAILED (sync failed) orders.
     * 
     * @return List of orders needing sync
     */
    @Query("SELECT * FROM orders WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'")
    suspend fun getUnsyncedOrders(): List<Order>
    
    /**
     * Retrieves orders modified after a specific timestamp.
     * Useful for incremental sync to only fetch changes since last sync.
     * 
     * @param timestamp The timestamp in milliseconds
     * @return List of orders modified after the timestamp
     */
    @Query("SELECT * FROM orders WHERE lastModified > :timestamp")
    suspend fun getOrdersModifiedAfter(timestamp: Long): List<Order>
    
    /**
     * Retrieves an order by its server-side MongoDB ID.
     * Used during sync operations to match server records with local records.
     * 
     * @param serverId The MongoDB _id from the server
     * @return The order if found, null otherwise
     */
    @Query("SELECT * FROM orders WHERE serverId = :serverId")
    suspend fun getOrderByServerId(serverId: String): Order?
    
    /**
     * Updates only the sync status of an order.
     * Useful for marking orders as synced/failed without modifying other fields.
     * 
     * @param orderId The local database ID
     * @param status The new sync status (PENDING/SYNCED/FAILED)
     */
    @Query("UPDATE orders SET syncStatus = :status WHERE id = :orderId")
    suspend fun updateSyncStatus(orderId: Int, status: String)
    
    /**
     * Updates server synchronization information for an order.
     * Called after successful sync to record the server ID and update timestamp.
     * 
     * @param localId The local database ID
     * @param serverId The MongoDB _id from the server
     * @param status The new sync status (typically SYNCED)
     * @param timestamp The current timestamp in milliseconds
     */
    @Query("UPDATE orders SET serverId = :serverId, syncStatus = :status, lastModified = :timestamp WHERE id = :localId")
    suspend fun updateServerInfo(localId: Int, serverId: String, status: String, timestamp: Long)
}

