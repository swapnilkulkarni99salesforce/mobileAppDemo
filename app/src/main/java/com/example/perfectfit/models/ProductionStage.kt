package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents the current production stage of an order.
 * 
 * This entity tracks which stage an order is currently in and maintains
 * a history of stage transitions for time tracking and bottleneck analysis.
 * 
 * Production Stages (in typical order):
 * 1. PENDING - Order received, not started
 * 2. CUTTING - Fabric cutting in progress
 * 3. STITCHING - Main stitching work
 * 4. FINISHING - Final touches, buttons, hems
 * 5. QUALITY_CHECK - Quality inspection
 * 6. READY - Ready for delivery
 * 7. DELIVERED - Delivered to customer
 * 
 * Stage Management:
 * - Only one active stage per order at a time
 * - Stage transitions are recorded in OrderStageHistory
 * - Time spent in each stage is calculated from history
 * 
 * @property id Auto-generated local database ID
 * @property orderId Foreign key to associated order
 * @property currentStage Current production stage name
 * @property stageStartedAt Timestamp when current stage started
 * @property assignedTo Worker/station assigned to this stage (optional)
 * @property notes Stage-specific notes or instructions
 * @property estimatedCompletionDate Expected completion date for this stage
 * 
 * @see [Order] for associated order
 * @see [OrderStageHistory] for stage transition history
 */
@Entity(
    tableName = "production_stages",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["orderId"], unique = true),  // One active stage per order
        Index(value = ["currentStage"])
    ]
)
data class ProductionStage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val orderId: Int,
    val currentStage: String = STAGE_PENDING,
    val stageStartedAt: Long = System.currentTimeMillis(),
    val assignedTo: String = "",
    val notes: String = "",
    val estimatedCompletionDate: String = ""
) {
    /**
     * Returns the stage display name.
     */
    fun getStageDisplayName(): String {
        return when (currentStage) {
            STAGE_PENDING -> "Pending"
            STAGE_CUTTING -> "Cutting"
            STAGE_STITCHING -> "Stitching"
            STAGE_FINISHING -> "Finishing"
            STAGE_QUALITY_CHECK -> "Quality Check"
            STAGE_READY -> "Ready for Delivery"
            STAGE_DELIVERED -> "Delivered"
            else -> currentStage
        }
    }
    
    /**
     * Returns the stage progress percentage (0-100).
     */
    fun getProgressPercentage(): Int {
        return when (currentStage) {
            STAGE_PENDING -> 0
            STAGE_CUTTING -> 15
            STAGE_STITCHING -> 40
            STAGE_FINISHING -> 70
            STAGE_QUALITY_CHECK -> 85
            STAGE_READY -> 95
            STAGE_DELIVERED -> 100
            else -> 0
        }
    }
    
    /**
     * Checks if order is in a critical stage requiring attention.
     */
    fun isCriticalStage(): Boolean {
        return currentStage == STAGE_STITCHING || currentStage == STAGE_QUALITY_CHECK
    }
    
    /**
     * Calculates time spent in current stage (in hours).
     */
    fun getTimeInCurrentStage(): Float {
        val durationMs = System.currentTimeMillis() - stageStartedAt
        return durationMs / (1000f * 60f * 60f)  // Convert to hours
    }
    
    /**
     * Checks if current stage has exceeded expected duration.
     */
    fun isStageDelayed(expectedHours: Float): Boolean {
        return getTimeInCurrentStage() > expectedHours
    }
    
    companion object {
        // Production stage constants (in order of workflow)
        const val STAGE_PENDING = "PENDING"
        const val STAGE_CUTTING = "CUTTING"
        const val STAGE_STITCHING = "STITCHING"
        const val STAGE_FINISHING = "FINISHING"
        const val STAGE_QUALITY_CHECK = "QUALITY_CHECK"
        const val STAGE_READY = "READY"
        const val STAGE_DELIVERED = "DELIVERED"
        
        // Expected duration for each stage (in hours)
        const val EXPECTED_CUTTING_HOURS = 2f
        const val EXPECTED_STITCHING_HOURS = 8f
        const val EXPECTED_FINISHING_HOURS = 3f
        const val EXPECTED_QC_HOURS = 1f
        
        /**
         * Returns all stages in workflow order.
         */
        fun getAllStages(): List<String> {
            return listOf(
                STAGE_PENDING,
                STAGE_CUTTING,
                STAGE_STITCHING,
                STAGE_FINISHING,
                STAGE_QUALITY_CHECK,
                STAGE_READY,
                STAGE_DELIVERED
            )
        }
        
        /**
         * Returns expected duration for a stage.
         */
        fun getExpectedDuration(stage: String): Float {
            return when (stage) {
                STAGE_CUTTING -> EXPECTED_CUTTING_HOURS
                STAGE_STITCHING -> EXPECTED_STITCHING_HOURS
                STAGE_FINISHING -> EXPECTED_FINISHING_HOURS
                STAGE_QUALITY_CHECK -> EXPECTED_QC_HOURS
                else -> 0f
            }
        }
    }
}

