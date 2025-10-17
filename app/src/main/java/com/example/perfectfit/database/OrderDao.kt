package com.example.perfectfit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.Order

@Dao
interface OrderDao {
    
    @Insert
    suspend fun insert(order: Order): Long
    
    @Update
    suspend fun update(order: Order)
    
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    suspend fun getAllOrders(): List<Order>
    
    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY orderDate DESC")
    suspend fun getOrdersByCustomerId(customerId: Int): List<Order>
    
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): Order?
    
    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Int)
    
    // Sync-related queries
    
    @Query("SELECT * FROM orders WHERE syncStatus = :status")
    suspend fun getOrdersBySyncStatus(status: String): List<Order>
    
    @Query("SELECT * FROM orders WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'")
    suspend fun getUnsyncedOrders(): List<Order>
    
    @Query("SELECT * FROM orders WHERE lastModified > :timestamp")
    suspend fun getOrdersModifiedAfter(timestamp: Long): List<Order>
    
    @Query("UPDATE orders SET syncStatus = :status WHERE id = :orderId")
    suspend fun updateSyncStatus(orderId: Int, status: String)
    
    @Query("UPDATE orders SET serverId = :serverId, syncStatus = :status, lastModified = :timestamp WHERE id = :localId")
    suspend fun updateServerInfo(localId: Int, serverId: String, status: String, timestamp: Long)
    
    @Query("SELECT * FROM orders WHERE serverId = :serverId")
    suspend fun getOrderByServerId(serverId: String): Order?
}

