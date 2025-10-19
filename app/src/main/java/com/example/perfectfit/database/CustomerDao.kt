package com.example.perfectfit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.Customer

/**
 * Data Access Object (DAO) for Customer entity operations.
 * 
 * This interface provides methods for all CRUD operations on the customers table,
 * including synchronization-related queries. Room automatically generates
 * the implementation at compile time.
 * 
 * All suspend functions are designed to be called from coroutines to avoid
 * blocking the main thread during database operations.
 */
@Dao
interface CustomerDao {
    
    // ===== Basic CRUD Operations =====
    
    /**
     * Inserts a new customer or replaces if already exists.
     * Uses REPLACE strategy to handle conflicts with unique constraints.
     * 
     * @param customer The customer to insert
     * @return The row ID of the inserted/updated customer
     * @throws android.database.sqlite.SQLiteConstraintException if unique constraint is violated
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long
    
    /**
     * Updates an existing customer in the database.
     * The customer must have a valid ID.
     * 
     * @param customer The customer with updated values
     */
    @Update
    suspend fun updateCustomer(customer: Customer)
    
    /**
     * Deletes a customer from the database.
     * Due to CASCADE delete on foreign keys, this will also delete:
     * - All orders for this customer
     * - All measurements for this customer
     * 
     * @param customer The customer to delete
     */
    @Delete
    suspend fun deleteCustomer(customer: Customer)
    
    /**
     * Deletes all customers from the database.
     * This is a destructive operation - use with caution!
     * Will CASCADE delete all orders and measurements.
     */
    @Query("DELETE FROM customers")
    suspend fun deleteAllCustomers()
    
    // ===== Query Operations =====
    
    /**
     * Retrieves all customers as LiveData, sorted alphabetically by first name.
     * LiveData automatically updates observers when the database changes.
     * 
     * @return LiveData list of customers, updated automatically on changes
     */
    @Query("SELECT * FROM customers ORDER BY firstName ASC")
    fun getAllCustomers(): LiveData<List<Customer>>
    
    /**
     * Retrieves all customers as a suspend list, sorted alphabetically by first name.
     * Use this when you need a one-time snapshot of customers (e.g., for sync operations).
     * 
     * @return List of all customers
     */
    @Query("SELECT * FROM customers ORDER BY firstName ASC")
    suspend fun getAllCustomersList(): List<Customer>
    
    /**
     * Retrieves a single customer by their local database ID.
     * 
     * @param customerId The local database ID
     * @return The customer if found, null otherwise
     */
    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: Int): Customer?
    
    /**
     * Retrieves a customer by composite unique key (firstName + lastName + mobile).
     * This is useful for detecting duplicate customers before insertion.
     * 
     * @param firstName Customer's first name
     * @param lastName Customer's last name
     * @param mobile Customer's mobile number
     * @return The customer if found, null otherwise
     */
    @Query("SELECT * FROM customers WHERE firstName = :firstName AND lastName = :lastName AND mobile = :mobile LIMIT 1")
    suspend fun getCustomerByCompositeKey(firstName: String, lastName: String, mobile: String): Customer?
    
    // ===== Synchronization Queries =====
    
    /**
     * Retrieves all customers with a specific synchronization status.
     * 
     * @param status The sync status to filter by (PENDING/SYNCED/FAILED)
     * @return List of customers matching the status
     */
    @Query("SELECT * FROM customers WHERE syncStatus = :status")
    suspend fun getCustomersBySyncStatus(status: String): List<Customer>
    
    /**
     * Retrieves all customers that need to be synced to the server.
     * Includes both PENDING (never synced) and FAILED (sync failed) customers.
     * 
     * @return List of customers needing sync
     */
    @Query("SELECT * FROM customers WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'")
    suspend fun getUnsyncedCustomers(): List<Customer>
    
    /**
     * Retrieves customers modified after a specific timestamp.
     * Useful for incremental sync to only fetch changes since last sync.
     * 
     * @param timestamp The timestamp in milliseconds
     * @return List of customers modified after the timestamp
     */
    @Query("SELECT * FROM customers WHERE lastModified > :timestamp")
    suspend fun getCustomersModifiedAfter(timestamp: Long): List<Customer>
    
    /**
     * Retrieves a customer by their server-side MongoDB ID.
     * Used during sync operations to match server records with local records.
     * 
     * @param serverId The MongoDB _id from the server
     * @return The customer if found, null otherwise
     */
    @Query("SELECT * FROM customers WHERE serverId = :serverId")
    suspend fun getCustomerByServerId(serverId: String): Customer?
    
    /**
     * Updates only the sync status of a customer.
     * Useful for marking customers as synced/failed without modifying other fields.
     * 
     * @param customerId The local database ID
     * @param status The new sync status (PENDING/SYNCED/FAILED)
     */
    @Query("UPDATE customers SET syncStatus = :status WHERE id = :customerId")
    suspend fun updateSyncStatus(customerId: Int, status: String)
    
    /**
     * Updates server synchronization information for a customer.
     * Called after successful sync to record the server ID and update timestamp.
     * 
     * @param localId The local database ID
     * @param serverId The MongoDB _id from the server
     * @param status The new sync status (typically SYNCED)
     * @param timestamp The current timestamp in milliseconds
     */
    @Query("UPDATE customers SET serverId = :serverId, syncStatus = :status, lastModified = :timestamp WHERE id = :localId")
    suspend fun updateServerInfo(localId: Int, serverId: String, status: String, timestamp: Long)
}

