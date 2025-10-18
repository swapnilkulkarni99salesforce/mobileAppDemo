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
     * Calculate estimated delivery date for a new order
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
}

