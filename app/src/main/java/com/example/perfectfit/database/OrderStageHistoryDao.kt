package com.example.perfectfit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.OrderStageHistory

/**
 * Data Access Object (DAO) for OrderStageHistory entity operations.
 * 
 * This interface provides methods for managing stage transition history,
 * enabling time tracking and bottleneck analysis.
 * 
 * All suspend functions are designed to be called from coroutines.
 */
@Dao
interface OrderStageHistoryDao {
    
    // ===== Basic CRUD Operations =====
    
    /**
     * Inserts a new stage history entry.
     * @param history The history entry to insert
     * @return The row ID of the inserted entry
     */
    @Insert
    suspend fun insert(history: OrderStageHistory): Long
    
    /**
     * Updates an existing stage history entry.
     * Typically used to mark a stage as completed.
     * @param history The history entry with updated values
     */
    @Update
    suspend fun update(history: OrderStageHistory)
    
    // ===== Query Operations =====
    
    /**
     * Retrieves complete stage history for an order.
     * @param orderId The order's local database ID
     * @return List of stage history entries in chronological order
     */
    @Query("SELECT * FROM order_stage_history WHERE orderId = :orderId ORDER BY stageStartedAt ASC")
    suspend fun getHistoryByOrderId(orderId: Int): List<OrderStageHistory>
    
    /**
     * Retrieves the currently active (incomplete) stage for an order.
     * @param orderId The order's local database ID
     * @return The incomplete stage history entry, or null if all completed
     */
    @Query("SELECT * FROM order_stage_history WHERE orderId = :orderId AND stageCompletedAt IS NULL LIMIT 1")
    suspend fun getActiveStageHistory(orderId: Int): OrderStageHistory?
    
    /**
     * Retrieves completed stage history for an order.
     * @param orderId The order's local database ID
     * @return List of completed stage history entries
     */
    @Query("SELECT * FROM order_stage_history WHERE orderId = :orderId AND stageCompletedAt IS NOT NULL ORDER BY stageStartedAt ASC")
    suspend fun getCompletedHistory(orderId: Int): List<OrderStageHistory>
    
    /**
     * Retrieves all history entries for a specific stage across all orders.
     * Useful for analyzing average time spent in each stage.
     * @param stageName The production stage name
     * @return List of history entries for that stage
     */
    @Query("SELECT * FROM order_stage_history WHERE stageName = :stageName AND stageCompletedAt IS NOT NULL")
    suspend fun getHistoryByStage(stageName: String): List<OrderStageHistory>
    
    /**
     * Calculates average duration for a stage across all completed instances.
     * Returns duration in milliseconds.
     * @param stageName The production stage name
     * @return Average duration in milliseconds, or null if no data
     */
    @Query("SELECT AVG(stageCompletedAt - stageStartedAt) FROM order_stage_history WHERE stageName = :stageName AND stageCompletedAt IS NOT NULL")
    suspend fun getAverageStageDuration(stageName: String): Long?
    
    /**
     * Retrieves history entries for stages that took longer than expected.
     * Used for bottleneck identification.
     * @param stageName The production stage name
     * @param maxDurationMs Maximum expected duration in milliseconds
     * @return List of delayed stage entries
     */
    @Query("SELECT * FROM order_stage_history WHERE stageName = :stageName AND stageCompletedAt IS NOT NULL AND (stageCompletedAt - stageStartedAt) > :maxDurationMs ORDER BY (stageCompletedAt - stageStartedAt) DESC")
    suspend fun getDelayedStages(stageName: String, maxDurationMs: Long): List<OrderStageHistory>
    
    /**
     * Retrieves orders currently in a stage for longer than specified time.
     * Used for identifying slow-moving orders.
     * @param startedBefore Timestamp threshold (orders started before this time)
     * @return List of long-running stage entries
     */
    @Query("SELECT * FROM order_stage_history WHERE stageCompletedAt IS NULL AND stageStartedAt < :startedBefore ORDER BY stageStartedAt ASC")
    suspend fun getSlowMovingOrders(startedBefore: Long): List<OrderStageHistory>
    
    /**
     * Marks a stage as completed.
     * @param historyId The history entry ID
     * @param completedAt The completion timestamp
     */
    @Query("UPDATE order_stage_history SET stageCompletedAt = :completedAt WHERE id = :historyId")
    suspend fun markStageCompleted(historyId: Int, completedAt: Long)
    
    /**
     * Retrieves total time spent across all stages for an order.
     * Returns sum of all completed stage durations in milliseconds.
     * @param orderId The order's local database ID
     * @return Total duration in milliseconds
     */
    @Query("SELECT SUM(stageCompletedAt - stageStartedAt) FROM order_stage_history WHERE orderId = :orderId AND stageCompletedAt IS NOT NULL")
    suspend fun getTotalOrderDuration(orderId: Int): Long?
    
    /**
     * Deletes all history entries for an order.
     * @param orderId The order's local database ID
     */
    @Query("DELETE FROM order_stage_history WHERE orderId = :orderId")
    suspend fun deleteHistoryForOrder(orderId: Int)
}

