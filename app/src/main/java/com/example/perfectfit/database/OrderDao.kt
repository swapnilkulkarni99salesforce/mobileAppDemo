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
}

