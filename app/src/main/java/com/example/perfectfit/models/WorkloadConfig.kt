package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workload_config")
data class WorkloadConfig(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timePerOrderHours: Float = 2.0f, // Time to complete a single order in hours
    val mondayHours: Float = 8.0f,
    val tuesdayHours: Float = 8.0f,
    val wednesdayHours: Float = 8.0f,
    val thursdayHours: Float = 8.0f,
    val fridayHours: Float = 8.0f,
    val saturdayHours: Float = 4.0f,
    val sundayHours: Float = 0.0f,
    
    // ✨ NEW: Realistic estimation settings
    val bufferDays: Int = 2,                // Add buffer days to all estimates
    val productivityFactor: Float = 0.85f,   // Assume 85% productive time
    val weekendReduction: Float = 0.8f       // Weekend hours are 80% as productive
) {
    // Helper function to get working hours for a specific day
    fun getHoursForDay(dayOfWeek: Int): Float {
        return when (dayOfWeek) {
            1 -> sundayHours // Calendar.SUNDAY = 1
            2 -> mondayHours // Calendar.MONDAY = 2
            3 -> tuesdayHours
            4 -> wednesdayHours
            5 -> thursdayHours
            6 -> fridayHours
            7 -> saturdayHours // Calendar.SATURDAY = 7
            else -> 0.0f
        }
    }
    
    // ✨ NEW: Get realistic working hours (with weekend reduction)
    fun getRealisticHoursForDay(dayOfWeek: Int): Float {
        val baseHours = getHoursForDay(dayOfWeek)
        // Apply weekend reduction for Saturday and Sunday
        return if (dayOfWeek == 1 || dayOfWeek == 7) {
            baseHours * weekendReduction
        } else {
            baseHours
        }
    }
    
    // Calculate total weekly hours
    val totalWeeklyHours: Float
        get() = mondayHours + tuesdayHours + wednesdayHours + 
                thursdayHours + fridayHours + saturdayHours + sundayHours
}

