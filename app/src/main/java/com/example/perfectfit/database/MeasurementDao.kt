package com.example.perfectfit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.Measurement

@Dao
interface MeasurementDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: Measurement): Long
    
    @Update
    suspend fun updateMeasurement(measurement: Measurement)
    
    @Query("SELECT * FROM measurements WHERE customerId = :customerId LIMIT 1")
    fun getMeasurementByCustomerId(customerId: Int): LiveData<Measurement?>
    
    @Query("SELECT * FROM measurements WHERE customerId = :customerId LIMIT 1")
    suspend fun getMeasurementByCustomerIdSync(customerId: Int): Measurement?
    
    @Query("DELETE FROM measurements WHERE customerId = :customerId")
    suspend fun deleteMeasurementsByCustomerId(customerId: Int)
    
    // Sync-related queries
    
    @Query("SELECT * FROM measurements WHERE syncStatus = :status")
    suspend fun getMeasurementsBySyncStatus(status: String): List<Measurement>
    
    @Query("SELECT * FROM measurements WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED'")
    suspend fun getUnsyncedMeasurements(): List<Measurement>
    
    @Query("SELECT * FROM measurements WHERE lastModified > :timestamp")
    suspend fun getMeasurementsModifiedAfter(timestamp: Long): List<Measurement>
    
    @Query("UPDATE measurements SET syncStatus = :status WHERE id = :measurementId")
    suspend fun updateSyncStatus(measurementId: Int, status: String)
    
    @Query("UPDATE measurements SET serverId = :serverId, syncStatus = :status, lastModified = :timestamp WHERE id = :localId")
    suspend fun updateServerInfo(localId: Int, serverId: String, status: String, timestamp: Long)
    
    @Query("SELECT * FROM measurements WHERE serverId = :serverId")
    suspend fun getMeasurementByServerId(serverId: String): Measurement?
    
    @Query("SELECT * FROM measurements")
    suspend fun getAllMeasurements(): List<Measurement>
}

