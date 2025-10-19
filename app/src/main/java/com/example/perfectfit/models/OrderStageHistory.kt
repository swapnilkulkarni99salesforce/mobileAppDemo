package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a historical record of stage transitions for an order.
 * 
 * This entity maintains a complete audit trail of all stage changes,
 * enabling time tracking, bottleneck identification, and performance analysis.
 * 
 * Use Cases:
 * - Time tracking per production stage
 * - Identifying slow-moving orders
 * - Bottleneck analysis in production workflow
 * - Performance metrics per worker/station
 * - Predictive delivery date calculations
 * 
 * Time Tracking:
 * - stageStartedAt: When the stage began
 * - stageCompletedAt: When the stage ended (null if still in progress)
 * - Duration = stageCompletedAt - stageStartedAt
 * 
 * @property id Auto-generated local database ID
 * @property orderId Foreign key to associated order
 * @property stageName Name of the production stage
 * @property stageStartedAt Timestamp when stage started
 * @property stageCompletedAt Timestamp when stage completed (null if in progress)
 * @property assignedTo Worker/station assigned to this stage
 * @property notes Stage-specific notes or observations
 * 
 * @see [Order] for associated order
 * @see [ProductionStage] for current stage tracking
 */
@Entity(
    tableName = "order_stage_history",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["stageName"]),
        Index(value = ["stageStartedAt"])
    ]
)
data class OrderStageHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val orderId: Int,
    val stageName: String,
    val stageStartedAt: Long,
    val stageCompletedAt: Long? = null,
    val assignedTo: String = "",
    val notes: String = ""
) {
    /**
     * Checks if this stage is still in progress.
     */
    fun isInProgress(): Boolean = stageCompletedAt == null
    
    /**
     * Calculates the duration spent in this stage (in hours).
     * Returns null if stage is still in progress.
     */
    fun getDurationHours(): Float? {
        val endTime = stageCompletedAt ?: return null
        val durationMs = endTime - stageStartedAt
        return durationMs / (1000f * 60f * 60f)  // Convert to hours
    }
    
    /**
     * Calculates the current duration if still in progress, or final duration if completed.
     */
    fun getCurrentOrFinalDuration(): Float {
        val endTime = stageCompletedAt ?: System.currentTimeMillis()
        val durationMs = endTime - stageStartedAt
        return durationMs / (1000f * 60f * 60f)
    }
    
    /**
     * Checks if this stage took longer than expected.
     */
    fun isDelayed(): Boolean {
        val duration = getDurationHours() ?: getCurrentOrFinalDuration()
        val expected = ProductionStage.getExpectedDuration(stageName)
        return duration > expected
    }
    
    /**
     * Marks the stage as completed.
     */
    fun complete(): OrderStageHistory {
        return copy(stageCompletedAt = System.currentTimeMillis())
    }
    
    companion object {
        /**
         * Creates a new stage history entry for a stage that's starting.
         */
        fun startStage(orderId: Int, stageName: String, assignedTo: String = ""): OrderStageHistory {
            return OrderStageHistory(
                orderId = orderId,
                stageName = stageName,
                stageStartedAt = System.currentTimeMillis(),
                assignedTo = assignedTo
            )
        }
    }
}

