package com.example.perfectfit.utils

import com.example.perfectfit.models.Order
import com.example.perfectfit.models.WorkloadConfig
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for workload calculations and analytics
 */
object WorkloadHelper {
    
    data class WorkloadStatus(
        val utilizationPercentage: Int,
        val totalPendingOrders: Int,
        val totalHoursNeeded: Float,
        val availableHoursThisWeek: Float,
        val daysUntilNextSlot: Int,
        val statusLevel: StatusLevel,
        val message: String,
        val canAcceptOrders: Boolean,
        val recommendedCapacity: Int
    )
    
    enum class StatusLevel {
        AVAILABLE,      // < 70% capacity
        BUSY,           // 70-90% capacity
        OVERBOOKED      // > 90% capacity
    }
    
    data class DeliveryAlert(
        val order: Order,
        val daysUntilDelivery: Int,
        val isOverdue: Boolean,
        val alertLevel: AlertLevel
    )
    
    enum class AlertLevel {
        URGENT,         // Due today or overdue
        WARNING,        // Due within 2-3 days
        UPCOMING        // Due within 7 days
    }
    
    /**
     * Calculate current workload status
     */
    fun calculateWorkloadStatus(
        pendingOrders: List<Order>,
        config: WorkloadConfig
    ): WorkloadStatus {
        val totalHoursNeeded = pendingOrders.size * config.timePerOrderHours
        val availableHoursThisWeek = calculateAvailableHoursThisWeek(config)
        
        val utilizationPercentage = if (availableHoursThisWeek > 0) {
            ((totalHoursNeeded / availableHoursThisWeek) * 100).toInt()
        } else {
            100
        }
        
        val statusLevel = when {
            utilizationPercentage < 70 -> StatusLevel.AVAILABLE
            utilizationPercentage < 90 -> StatusLevel.BUSY
            else -> StatusLevel.OVERBOOKED
        }
        
        val message = when (statusLevel) {
            StatusLevel.AVAILABLE -> "You have good capacity available"
            StatusLevel.BUSY -> "You're running at high capacity"
            StatusLevel.OVERBOOKED -> "You're overbooked! Consider extending delivery dates"
        }
        
        val daysUntilNextSlot = calculateDaysUntilNextSlot(totalHoursNeeded, config)
        
        val remainingCapacity = availableHoursThisWeek - totalHoursNeeded
        val recommendedCapacity = if (remainingCapacity > 0 && config.timePerOrderHours > 0) {
            (remainingCapacity / config.timePerOrderHours).toInt()
        } else {
            0
        }
        
        return WorkloadStatus(
            utilizationPercentage = utilizationPercentage,
            totalPendingOrders = pendingOrders.size,
            totalHoursNeeded = totalHoursNeeded,
            availableHoursThisWeek = availableHoursThisWeek,
            daysUntilNextSlot = daysUntilNextSlot,
            statusLevel = statusLevel,
            message = message,
            canAcceptOrders = statusLevel != StatusLevel.OVERBOOKED,
            recommendedCapacity = recommendedCapacity
        )
    }
    
    /**
     * Get delivery alerts for upcoming orders
     */
    fun getDeliveryAlerts(orders: List<Order>): List<DeliveryAlert> {
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        return orders.mapNotNull { order ->
            try {
                val deliveryDate = dateFormat.parse(order.estimatedDeliveryDate)
                deliveryDate?.let {
                    val deliveryCal = Calendar.getInstance().apply { time = it }
                    val diffInMillis = deliveryCal.timeInMillis - today.timeInMillis
                    val daysUntil = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
                    
                    val isOverdue = daysUntil < 0
                    val alertLevel = when {
                        isOverdue || daysUntil == 0 -> AlertLevel.URGENT
                        daysUntil <= 3 -> AlertLevel.WARNING
                        daysUntil <= 7 -> AlertLevel.UPCOMING
                        else -> return@mapNotNull null
                    }
                    
                    DeliveryAlert(
                        order = order,
                        daysUntilDelivery = daysUntil,
                        isOverdue = isOverdue,
                        alertLevel = alertLevel
                    )
                }
            } catch (e: Exception) {
                null
            }
        }.sortedBy { it.daysUntilDelivery }
    }
    
    /**
     * Calculate available working hours for the current week
     */
    private fun calculateAvailableHoursThisWeek(config: WorkloadConfig): Float {
        val today = Calendar.getInstance()
        val endOfWeek = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            add(Calendar.WEEK_OF_YEAR, 1)
        }
        
        var totalHours = 0f
        val currentDate = today.clone() as Calendar
        
        while (currentDate.before(endOfWeek)) {
            val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
            totalHours += config.getHoursForDay(dayOfWeek)
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return totalHours
    }
    
    /**
     * Calculate days until next available slot
     */
    private fun calculateDaysUntilNextSlot(
        currentWorkloadHours: Float,
        config: WorkloadConfig
    ): Int {
        if (currentWorkloadHours <= 0) return 0
        
        var hoursRemaining = currentWorkloadHours
        val currentDate = Calendar.getInstance()
        var daysChecked = 0
        val maxDays = 30
        
        while (hoursRemaining > 0 && daysChecked < maxDays) {
            val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
            val workingHoursToday = config.getHoursForDay(dayOfWeek)
            hoursRemaining -= workingHoursToday
            
            if (hoursRemaining > 0) {
                currentDate.add(Calendar.DAY_OF_MONTH, 1)
                daysChecked++
            }
        }
        
        return daysChecked
    }
    
    /**
     * Calculate estimated delivery date for a new order (OPTIMISTIC)
     */
    fun calculateDeliveryDate(
        pendingOrdersCount: Int,
        config: WorkloadConfig,
        startDate: Calendar = Calendar.getInstance()
    ): Calendar {
        val totalOrdersToComplete = pendingOrdersCount + 1
        val totalHoursNeeded = totalOrdersToComplete * config.timePerOrderHours
        
        val currentDate = startDate.clone() as Calendar
        var hoursRemaining = totalHoursNeeded
        var daysChecked = 0
        val maxDaysToCheck = 365
        
        while (hoursRemaining > 0 && daysChecked < maxDaysToCheck) {
            val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
            val workingHoursToday = config.getHoursForDay(dayOfWeek)
            hoursRemaining -= workingHoursToday
            
            if (hoursRemaining > 0) {
                currentDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            daysChecked++
        }
        
        return currentDate
    }
    
    /**
     * ✨ NEW: Calculate REALISTIC delivery date with buffers
     * Accounts for:
     * - Productivity factor (people aren't 100% productive)
     * - Weekend reduction (weekends are less productive)
     * - Buffer days (contingency for unexpected issues)
     */
    fun calculateRealisticDeliveryDate(
        pendingOrdersCount: Int,
        config: WorkloadConfig,
        startDate: Calendar = Calendar.getInstance()
    ): Calendar {
        val totalOrdersToComplete = pendingOrdersCount + 1
        var totalHoursNeeded = totalOrdersToComplete * config.timePerOrderHours
        
        // Apply productivity factor (account for breaks, interruptions, etc.)
        totalHoursNeeded /= config.productivityFactor
        
        val currentDate = startDate.clone() as Calendar
        var hoursRemaining = totalHoursNeeded
        var daysChecked = 0
        val maxDaysToCheck = 365
        
        while (hoursRemaining > 0 && daysChecked < maxDaysToCheck) {
            val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
            // Use realistic hours (with weekend reduction)
            val workingHoursToday = config.getRealisticHoursForDay(dayOfWeek)
            hoursRemaining -= workingHoursToday
            
            if (hoursRemaining > 0) {
                currentDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            daysChecked++
        }
        
        // Add buffer days for contingency
        currentDate.add(Calendar.DAY_OF_MONTH, config.bufferDays)
        
        return currentDate
    }
    
    /**
     * ✨ NEW: Get both optimistic and realistic estimates
     */
    data class DeliveryEstimates(
        val optimisticDate: Calendar,
        val realisticDate: Calendar,
        val recommendedDate: Calendar,  // Same as realistic
        val daysDifference: Int,
        val confidenceLevel: String
    )
    
    fun calculateDeliveryEstimates(
        pendingOrdersCount: Int,
        config: WorkloadConfig
    ): DeliveryEstimates {
        val optimistic = calculateDeliveryDate(pendingOrdersCount, config)
        val realistic = calculateRealisticDeliveryDate(pendingOrdersCount, config)
        
        val diffInMillis = realistic.timeInMillis - optimistic.timeInMillis
        val daysDiff = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        
        val confidence = when {
            pendingOrdersCount < 5 -> "High"
            pendingOrdersCount < 10 -> "Medium"
            else -> "Low (Consider extending dates)"
        }
        
        return DeliveryEstimates(
            optimisticDate = optimistic,
            realisticDate = realistic,
            recommendedDate = realistic,
            daysDifference = daysDiff,
            confidenceLevel = confidence
        )
    }
    
    /**
     * Get workload summary text
     */
    fun getWorkloadSummaryText(status: WorkloadStatus): String {
        return buildString {
            append("📊 Workload Status\n")
            append("${getStatusEmoji(status.statusLevel)} ${status.utilizationPercentage}% capacity\n")
            append("📦 ${status.totalPendingOrders} pending orders\n")
            append("⏰ ${String.format("%.1f", status.totalHoursNeeded)} hours workload\n")
            if (status.recommendedCapacity > 0) {
                append("✅ Can accept ${status.recommendedCapacity} more orders this week")
            } else {
                append("⚠️ At full capacity - consider extending delivery dates")
            }
        }
    }
    
    /**
     * Get status emoji based on level
     */
    fun getStatusEmoji(level: StatusLevel): String {
        return when (level) {
            StatusLevel.AVAILABLE -> "🟢"
            StatusLevel.BUSY -> "🟡"
            StatusLevel.OVERBOOKED -> "🔴"
        }
    }
    
    /**
     * Get alert emoji based on level
     */
    fun getAlertEmoji(level: AlertLevel): String {
        return when (level) {
            AlertLevel.URGENT -> "🔴"
            AlertLevel.WARNING -> "🟠"
            AlertLevel.UPCOMING -> "🔵"
        }
    }
    
    /**
     * Format delivery alert message
     */
    fun formatDeliveryAlertMessage(alert: DeliveryAlert): String {
        return when {
            alert.isOverdue -> "${getAlertEmoji(alert.alertLevel)} OVERDUE: ${alert.order.customerName} - Order ${alert.order.orderId}"
            alert.daysUntilDelivery == 0 -> "${getAlertEmoji(alert.alertLevel)} DUE TODAY: ${alert.order.customerName} - Order ${alert.order.orderId}"
            alert.daysUntilDelivery == 1 -> "${getAlertEmoji(alert.alertLevel)} DUE TOMORROW: ${alert.order.customerName} - Order ${alert.order.orderId}"
            else -> "${getAlertEmoji(alert.alertLevel)} Due in ${alert.daysUntilDelivery} days: ${alert.order.customerName} - Order ${alert.order.orderId}"
        }
    }
    
    /**
     * ✨ QUICK WIN: Get confidence level based on pending orders
     */
    enum class ConfidenceLevel {
        HIGH,      // < 5 orders
        MEDIUM,    // 5-10 orders
        LOW        // > 10 orders
    }
    
    fun getConfidenceLevel(pendingOrders: Int): ConfidenceLevel {
        return when {
            pendingOrders < 5 -> ConfidenceLevel.HIGH
            pendingOrders < 10 -> ConfidenceLevel.MEDIUM
            else -> ConfidenceLevel.LOW
        }
    }
    
    /**
     * ✨ QUICK WIN: Get confidence emoji
     */
    fun getConfidenceEmoji(level: ConfidenceLevel): String {
        return when (level) {
            ConfidenceLevel.HIGH -> "🟢"
            ConfidenceLevel.MEDIUM -> "🟡"
            ConfidenceLevel.LOW -> "🔴"
        }
    }
    
    /**
     * ✨ QUICK WIN: Get confidence text
     */
    fun getConfidenceText(level: ConfidenceLevel): String {
        return when (level) {
            ConfidenceLevel.HIGH -> "High Confidence"
            ConfidenceLevel.MEDIUM -> "Medium Confidence"
            ConfidenceLevel.LOW -> "Low Confidence - Consider extending dates"
        }
    }
    
    /**
     * ✨ QUICK WIN: Calculate weekly capacity for next N weeks
     */
    data class WeeklyCapacity(
        val weekNumber: Int,
        val weekStartDate: Calendar,
        val weekEndDate: Calendar,
        val totalAvailableHours: Float,
        val allocatedHours: Float,
        val utilizationPercentage: Int,
        val orderCount: Int,
        val statusLevel: StatusLevel,
        val isCurrentWeek: Boolean
    )
    
    fun calculateMultiWeekCapacity(
        allOrders: List<Order>,
        config: WorkloadConfig,
        weeksAhead: Int = 4
    ): List<WeeklyCapacity> {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val weeklyData = mutableListOf<WeeklyCapacity>()
        val today = Calendar.getInstance()
        
        for (weekIndex in 0 until weeksAhead) {
            val weekStart = Calendar.getInstance().apply {
                add(Calendar.WEEK_OF_YEAR, weekIndex)
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            
            val weekEnd = weekStart.clone() as Calendar
            weekEnd.add(Calendar.DAY_OF_MONTH, 6)
            
            // Calculate available hours for this week
            var availableHours = 0f
            val tempDate = weekStart.clone() as Calendar
            
            while (!tempDate.after(weekEnd)) {
                val dayOfWeek = tempDate.get(Calendar.DAY_OF_WEEK)
                availableHours += config.getRealisticHoursForDay(dayOfWeek)
                tempDate.add(Calendar.DAY_OF_MONTH, 1)
            }
            
            // Find orders with delivery dates in this week
            val ordersThisWeek = allOrders.filter { order ->
                try {
                    val orderDate = dateFormat.parse(order.estimatedDeliveryDate)
                    orderDate?.let {
                        val orderCal = Calendar.getInstance().apply { time = it }
                        !orderCal.before(weekStart) && !orderCal.after(weekEnd)
                    } ?: false
                } catch (e: Exception) {
                    false
                }
            }.filter { order ->
                order.status.equals("Pending", ignoreCase = true) ||
                order.status.equals("In Progress", ignoreCase = true)
            }
            
            val allocatedHours = ordersThisWeek.size * config.timePerOrderHours
            val utilization = if (availableHours > 0) {
                ((allocatedHours / availableHours) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
            
            val statusLevel = when {
                utilization < 70 -> StatusLevel.AVAILABLE
                utilization < 90 -> StatusLevel.BUSY
                else -> StatusLevel.OVERBOOKED
            }
            
            val isCurrentWeek = weekIndex == 0
            
            weeklyData.add(WeeklyCapacity(
                weekNumber = weekIndex + 1,
                weekStartDate = weekStart,
                weekEndDate = weekEnd,
                totalAvailableHours = availableHours,
                allocatedHours = allocatedHours,
                utilizationPercentage = utilization,
                orderCount = ordersThisWeek.size,
                statusLevel = statusLevel,
                isCurrentWeek = isCurrentWeek
            ))
        }
        
        return weeklyData
    }
    
    /**
     * ✨ QUICK WIN: Format weekly capacity summary
     */
    fun formatWeeklySummary(weeklyCapacity: List<WeeklyCapacity>): String {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        
        return buildString {
            append("📅 4-Week Capacity Outlook\n\n")
            
            weeklyCapacity.forEachIndexed { index, week ->
                val startStr = dateFormat.format(week.weekStartDate.time)
                val endStr = dateFormat.format(week.weekEndDate.time)
                val emoji = getStatusEmoji(week.statusLevel)
                val current = if (week.isCurrentWeek) " (THIS WEEK)" else ""
                
                append("Week ${week.weekNumber}$current ($startStr-$endStr)\n")
                append("$emoji ${week.utilizationPercentage}% | ")
                append("${week.orderCount} orders | ")
                append("${String.format("%.1f", week.allocatedHours)}h / ${String.format("%.1f", week.totalAvailableHours)}h")
                
                when (week.statusLevel) {
                    StatusLevel.AVAILABLE -> append(" ✨ Good availability")
                    StatusLevel.BUSY -> append(" ⚠️ High capacity")
                    StatusLevel.OVERBOOKED -> append(" 🚨 OVERBOOKED")
                }
                
                if (index < weeklyCapacity.size - 1) append("\n\n")
            }
            
            // Add recommendation
            val bestWeek = weeklyCapacity.minByOrNull { it.utilizationPercentage }
            bestWeek?.let {
                append("\n\n💡 Best time for new orders: Week ${it.weekNumber}")
            }
        }
    }
}

