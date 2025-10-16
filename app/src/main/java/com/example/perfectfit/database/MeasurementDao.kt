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
}

