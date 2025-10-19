package com.example.perfectfit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.ProductionStage

/**
 * Data Access Object (DAO) for ProductionStage entity operations.
 * 
 * This interface provides methods for managing production stages of orders.
 * Each order has only one active production stage at a time.
 * 
 * All suspend functions are designed to be called from coroutines.
 */
@Dao
interface ProductionStageDao {
    
    // ===== Basic CRUD Operations =====
    
    /**
     * Inserts a new production stage or replaces existing.
     * Uses REPLACE strategy since each order has only one active stage.
     * @param stage The production stage to insert
     * @return The row ID of the inserted/updated stage
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stage: ProductionStage): Long
    
    /**
     * Updates an existing production stage.
     * @param stage The stage with updated values
     */
    @Update
    suspend fun update(stage: ProductionStage)
    
    // ===== Query Operations =====
    
    /**
     * Retrieves the production stage for a specific order.
     * @param orderId The order's local database ID
     * @return The production stage if found, null otherwise
     */
    @Query("SELECT * FROM production_stages WHERE orderId = :orderId LIMIT 1")
    suspend fun getStageByOrderId(orderId: Int): ProductionStage?
    
    /**
     * Retrieves the production stage as LiveData for reactive updates.
     * @param orderId The order's local database ID
     * @return LiveData of the production stage
     */
    @Query("SELECT * FROM production_stages WHERE orderId = :orderId LIMIT 1")
    fun getStageLiveData(orderId: Int): LiveData<ProductionStage?>
    
    /**
     * Retrieves all orders in a specific production stage.
     * Useful for workload visualization and bottleneck identification.
     * @param stageName The production stage name
     * @return List of production stages in that stage
     */
    @Query("SELECT * FROM production_stages WHERE currentStage = :stageName ORDER BY stageStartedAt ASC")
    suspend fun getOrdersInStage(stageName: String): List<ProductionStage>
    
    /**
     * Retrieves all active production stages.
     * Excludes delivered orders.
     * @return List of all active production stages
     */
    @Query("SELECT * FROM production_stages WHERE currentStage != 'DELIVERED' ORDER BY stageStartedAt ASC")
    suspend fun getAllActiveStages(): List<ProductionStage>
    
    /**
     * Retrieves orders that have been in current stage longer than specified hours.
     * Used for identifying slow-moving orders and bottlenecks.
     * @param hoursAgo Timestamp (current time - hours * 3600000)
     * @return List of delayed production stages
     */
    @Query("SELECT * FROM production_stages WHERE stageStartedAt < :hoursAgo AND currentStage != 'DELIVERED' ORDER BY stageStartedAt ASC")
    suspend fun getDelayedOrders(hoursAgo: Long): List<ProductionStage>
    
    /**
     * Updates the current stage for an order.
     * @param orderId The order's local database ID
     * @param newStage The new stage name
     * @param startTime The timestamp when the new stage started
     */
    @Query("UPDATE production_stages SET currentStage = :newStage, stageStartedAt = :startTime WHERE orderId = :orderId")
    suspend fun updateStage(orderId: Int, newStage: String, startTime: Long)
    
    /**
     * Updates the assigned worker for a stage.
     * @param orderId The order's local database ID
     * @param assignedTo The worker/station name
     */
    @Query("UPDATE production_stages SET assignedTo = :assignedTo WHERE orderId = :orderId")
    suspend fun updateAssignment(orderId: Int, assignedTo: String)
    
    /**
     * Counts orders in each production stage.
     * Returns a map of stage name to count.
     */
    @Query("SELECT currentStage, COUNT(*) as count FROM production_stages GROUP BY currentStage")
    suspend fun getStageDistribution(): Map<String, Int>
    
    /**
     * Deletes the production stage for an order.
     * @param orderId The order's local database ID
     */
    @Query("DELETE FROM production_stages WHERE orderId = :orderId")
    suspend fun deleteStage(orderId: Int)
}

