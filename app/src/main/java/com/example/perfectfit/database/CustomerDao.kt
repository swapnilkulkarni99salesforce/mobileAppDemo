package com.example.perfectfit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.Customer

@Dao
interface CustomerDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long
    
    @Update
    suspend fun updateCustomer(customer: Customer)
    
    @Delete
    suspend fun deleteCustomer(customer: Customer)
    
    @Query("SELECT * FROM customers ORDER BY firstName ASC")
    fun getAllCustomers(): LiveData<List<Customer>>
    
    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: Int): Customer?
    
    @Query("DELETE FROM customers")
    suspend fun deleteAllCustomers()
    
    // Sync-related queries
    
    @Query("SELECT * FROM customers WHERE syncStatus = :status")
    suspend fun getCustomersBySyncStatus(status: String): List<Customer>
    
    @Query("SELECT * FROM customers WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'")
    suspend fun getUnsyncedCustomers(): List<Customer>
    
    @Query("SELECT * FROM customers WHERE lastModified > :timestamp")
    suspend fun getCustomersModifiedAfter(timestamp: Long): List<Customer>
    
    @Query("UPDATE customers SET syncStatus = :status WHERE id = :customerId")
    suspend fun updateSyncStatus(customerId: Int, status: String)
    
    @Query("UPDATE customers SET serverId = :serverId, syncStatus = :status, lastModified = :timestamp WHERE id = :localId")
    suspend fun updateServerInfo(localId: Int, serverId: String, status: String, timestamp: Long)
    
    @Query("SELECT * FROM customers WHERE serverId = :serverId")
    suspend fun getCustomerByServerId(serverId: String): Customer?
    
    @Query("SELECT * FROM customers ORDER BY firstName ASC")
    suspend fun getAllCustomersList(): List<Customer>
    
    @Query("SELECT * FROM customers WHERE firstName = :firstName AND lastName = :lastName AND mobile = :mobile LIMIT 1")
    suspend fun getCustomerByCompositeKey(firstName: String, lastName: String, mobile: String): Customer?
}

