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
}

