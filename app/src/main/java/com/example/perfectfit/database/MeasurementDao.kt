package com.example.perfectfit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.Measurement

/**
 * Data Access Object (DAO) for Measurement entity operations.
 * 
 * This interface provides methods for managing customer measurements in the database,
 * including CRUD operations and synchronization queries. Room automatically
 * generates the implementation at compile time.
 * 
 * Note: Each customer typically has only one Measurement record containing all
 * their measurements (kurti, pant, and blouse). Measurements are automatically
 * deleted when their associated customer is deleted due to CASCADE foreign key.
 * 
 * All suspend functions are designed to be called from coroutines to avoid
 * blocking the main thread during database operations.
 */
@Dao
interface MeasurementDao {
    
    // ===== Basic CRUD Operations =====
    
    /**
     * Inserts a new measurement or replaces if already exists.
     * Uses REPLACE strategy to handle conflicts - useful for updating measurements.
     * 
     * @param measurement The measurement to insert
     * @return The row ID of the inserted/updated measurement
     * @throws android.database.sqlite.SQLiteConstraintException if foreign key constraint fails
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: Measurement): Long
    
    /**
     * Updates an existing measurement in the database.
     * The measurement must have a valid ID.
     * 
     * @param measurement The measurement with updated values
     */
    @Update
    suspend fun updateMeasurement(measurement: Measurement)
    
    /**
     * Deletes all measurements for a specific customer.
     * Typically used when resetting customer measurements.
     * 
     * @param customerId The customer's local database ID
     */
    @Query("DELETE FROM measurements WHERE customerId = :customerId")
    suspend fun deleteMeasurementsByCustomerId(customerId: Int)
    
    // ===== Query Operations =====
    
    /**
     * Retrieves measurements for a customer as LiveData.
     * LiveData automatically updates observers when the measurement changes.
     * Returns null if no measurements exist for the customer.
     * 
     * Note: LIMIT 1 is used because each customer should have only one measurement record.
     * 
     * @param customerId The customer's local database ID
     * @return LiveData of the measurement, or null if not found
     */
    @Query("SELECT * FROM measurements WHERE customerId = :customerId LIMIT 1")
    fun getMeasurementByCustomerId(customerId: Int): LiveData<Measurement?>
    
    /**
     * Retrieves measurements for a customer as a suspend function.
     * Use this for one-time reads (e.g., sync operations) rather than observing changes.
     * Returns null if no measurements exist for the customer.
     * 
     * Note: LIMIT 1 is used because each customer should have only one measurement record.
     * 
     * @param customerId The customer's local database ID
     * @return The measurement if found, null otherwise
     */
    @Query("SELECT * FROM measurements WHERE customerId = :customerId LIMIT 1")
    suspend fun getMeasurementByCustomerIdSync(customerId: Int): Measurement?
    
    /**
     * Retrieves all measurements from the database.
     * Useful for bulk operations or analytics.
     * 
     * @return List of all measurements
     */
    @Query("SELECT * FROM measurements")
    suspend fun getAllMeasurements(): List<Measurement>
    
    // ===== Synchronization Queries =====
    
    /**
     * Retrieves all measurements with a specific synchronization status.
     * 
     * @param status The sync status to filter by (PENDING/SYNCED/FAILED)
     * @return List of measurements matching the status
     */
    @Query("SELECT * FROM measurements WHERE syncStatus = :status")
    suspend fun getMeasurementsBySyncStatus(status: String): List<Measurement>
    
    /**
     * Retrieves all measurements that need to be synced to the server.
     * Includes both PENDING (never synced) and FAILED (sync failed) measurements.
     * 
     * @return List of measurements needing sync
     */
    @Query("SELECT * FROM measurements WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'")
    suspend fun getUnsyncedMeasurements(): List<Measurement>
    
    /**
     * Retrieves measurements modified after a specific timestamp.
     * Useful for incremental sync to only fetch changes since last sync.
     * 
     * @param timestamp The timestamp in milliseconds
     * @return List of measurements modified after the timestamp
     */
    @Query("SELECT * FROM measurements WHERE lastModified > :timestamp")
    suspend fun getMeasurementsModifiedAfter(timestamp: Long): List<Measurement>
    
    /**
     * Retrieves a measurement by its server-side MongoDB ID.
     * Used during sync operations to match server records with local records.
     * 
     * @param serverId The MongoDB _id from the server
     * @return The measurement if found, null otherwise
     */
    @Query("SELECT * FROM measurements WHERE serverId = :serverId")
    suspend fun getMeasurementByServerId(serverId: String): Measurement?
    
    /**
     * Updates only the sync status of a measurement.
     * Useful for marking measurements as synced/failed without modifying other fields.
     * 
     * @param measurementId The local database ID
     * @param status The new sync status (PENDING/SYNCED/FAILED)
     */
    @Query("UPDATE measurements SET syncStatus = :status WHERE id = :measurementId")
    suspend fun updateSyncStatus(measurementId: Int, status: String)
    
    /**
     * Updates server synchronization information for a measurement.
     * Called after successful sync to record the server ID and update timestamp.
     * 
     * @param localId The local database ID
     * @param serverId The MongoDB _id from the server
     * @param status The new sync status (typically SYNCED)
     * @param timestamp The current timestamp in milliseconds
     */
    @Query("UPDATE measurements SET serverId = :serverId, syncStatus = :status, lastModified = :timestamp WHERE id = :localId")
    suspend fun updateServerInfo(localId: Int, serverId: String, status: String, timestamp: Long)
}

