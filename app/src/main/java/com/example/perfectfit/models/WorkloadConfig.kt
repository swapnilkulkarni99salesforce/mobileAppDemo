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
    val sundayHours: Float = 0.0f
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
    
    // Calculate total weekly hours
    val totalWeeklyHours: Float
        get() = mondayHours + tuesdayHours + wednesdayHours + 
                thursdayHours + fridayHours + saturdayHours + sundayHours
}

