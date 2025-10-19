package com.example.perfectfit.utils

import android.content.Context
import android.widget.Toast
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.models.WorkloadConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * ✨ QUICK WIN 2: Helper for quick capacity adjustment actions
 */
object QuickActionsHelper {
    
    /**
     * Temporarily boost capacity for today
     * Note: This is a temporary boost - resets after recalculation
     */
    suspend fun addExtraHoursToday(
        context: Context,
        extraHours: Float,
        database: AppDatabase
    ): Result<String> {
        return try {
            val config = database.workloadConfigDao().getConfig() ?: WorkloadConfig()
            val today = Calendar.getInstance()
            val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
            
            // Get current hours for today
            val currentHours = config.getHoursForDay(dayOfWeek)
            val newHours = currentHours + extraHours
            
            // Create updated config with extra hours for today
            val updatedConfig = when (dayOfWeek) {
                Calendar.MONDAY -> config.copy(mondayHours = newHours)
                Calendar.TUESDAY -> config.copy(tuesdayHours = newHours)
                Calendar.WEDNESDAY -> config.copy(wednesdayHours = newHours)
                Calendar.THURSDAY -> config.copy(thursdayHours = newHours)
                Calendar.FRIDAY -> config.copy(fridayHours = newHours)
                Calendar.SATURDAY -> config.copy(saturdayHours = newHours)
                Calendar.SUNDAY -> config.copy(sundayHours = newHours)
                else -> config
            }
            
            // Save updated config
            withContext(Dispatchers.IO) {
                database.workloadConfigDao().deleteAllConfigs()
                database.workloadConfigDao().insertConfig(updatedConfig)
            }
            
            val dayName = getDayName(dayOfWeek)
            val message = """
                ⏰ Extra Hours Added!
                
                $dayName: $currentHours h → $newHours h (+$extraHours h)
                
                Your capacity for today has been increased.
                All delivery dates will be recalculated automatically.
            """.trimIndent()
            
            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Calculate impact of adding extra hours
     */
    fun calculateExtraHoursImpact(
        extraHours: Float,
        config: WorkloadConfig,
        pendingOrdersCount: Int
    ): ExtraHoursImpact {
        val ordersPerHour = 1 / config.timePerOrderHours
        val additionalCapacity = (extraHours * ordersPerHour).toInt()
        
        val today = Calendar.getInstance()
        val dayOfWeek = today.get(Calendar.DAY_OF_WEEK)
        val currentHours = config.getHoursForDay(dayOfWeek)
        val newHours = currentHours + extraHours
        
        // Estimate how many days can be reduced from delivery
        val daysReduced = if (additionalCapacity > 0) {
            (additionalCapacity.toFloat() / maxOf(pendingOrdersCount, 1) * 7).toInt().coerceAtMost(3)
        } else {
            0
        }
        
        return ExtraHoursImpact(
            extraHours = extraHours,
            currentHours = currentHours,
            newHours = newHours,
            additionalCapacity = additionalCapacity,
            estimatedDaysReduced = daysReduced
        )
    }
    
    data class ExtraHoursImpact(
        val extraHours: Float,
        val currentHours: Float,
        val newHours: Float,
        val additionalCapacity: Int,
        val estimatedDaysReduced: Int
    )
    
    /**
     * Get quick action suggestions based on current workload
     */
    fun getQuickActionSuggestions(
        utilizationPercentage: Int,
        pendingOrders: Int
    ): List<QuickAction> {
        val suggestions = mutableListOf<QuickAction>()
        
        when {
            utilizationPercentage > 90 -> {
                // Overbooked - suggest ways to free up capacity
                suggestions.add(QuickAction(
                    title = "⏰ Add Extra Hours Today",
                    description = "Work a few extra hours to reduce backlog",
                    actionType = QuickActionType.ADD_HOURS,
                    priority = ActionPriority.HIGH
                ))
                
                suggestions.add(QuickAction(
                    title = "📅 Extend Low Priority Orders",
                    description = "Push back non-urgent orders by 2-3 days",
                    actionType = QuickActionType.RESCHEDULE,
                    priority = ActionPriority.MEDIUM
                ))
                
                suggestions.add(QuickAction(
                    title = "📢 Notify Customers",
                    description = "Send status updates about slight delays",
                    actionType = QuickActionType.NOTIFY,
                    priority = ActionPriority.MEDIUM
                ))
            }
            
            utilizationPercentage > 70 -> {
                // Busy - suggest optimization
                suggestions.add(QuickAction(
                    title = "⚠️ Monitor Capacity",
                    description = "You're at high capacity, avoid taking rush orders",
                    actionType = QuickActionType.WARNING,
                    priority = ActionPriority.MEDIUM
                ))
                
                suggestions.add(QuickAction(
                    title = "⏰ Consider Extra Hours",
                    description = "Optional: Add 2-3 extra hours for buffer",
                    actionType = QuickActionType.ADD_HOURS,
                    priority = ActionPriority.LOW
                ))
            }
            
            else -> {
                // Good capacity - positive suggestions
                suggestions.add(QuickAction(
                    title = "✅ Great Capacity!",
                    description = "You can accept ${(100 - utilizationPercentage) / 10} more orders comfortably",
                    actionType = QuickActionType.INFO,
                    priority = ActionPriority.LOW
                ))
            }
        }
        
        return suggestions
    }
    
    data class QuickAction(
        val title: String,
        val description: String,
        val actionType: QuickActionType,
        val priority: ActionPriority
    )
    
    enum class QuickActionType {
        ADD_HOURS,
        RESCHEDULE,
        NOTIFY,
        WARNING,
        INFO
    }
    
    enum class ActionPriority {
        HIGH,
        MEDIUM,
        LOW
    }
    
    /**
     * Get day name from Calendar day of week
     */
    private fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Unknown"
        }
    }
    
    /**
     * Format impact message
     */
    fun formatImpactMessage(impact: ExtraHoursImpact): String {
        return buildString {
            append("📊 Impact Preview\n\n")
            append("Current hours today: ${impact.currentHours}h\n")
            append("New hours today: ${impact.newHours}h (+${impact.extraHours}h)\n\n")
            append("Additional capacity: ${impact.additionalCapacity} orders\n")
            if (impact.estimatedDaysReduced > 0) {
                append("Estimated days reduced: ~${impact.estimatedDaysReduced} days\n\n")
                append("✨ This will help clear your backlog faster!")
            } else {
                append("\n💡 This will give you breathing room for rush orders")
            }
        }
    }
}

