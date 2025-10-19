package com.example.perfectfit.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.perfectfit.MainActivity
import com.example.perfectfit.R
import com.example.perfectfit.models.Order

/**
 * Helper class for managing notifications
 */
object NotificationHelper {
    
    private const val CHANNEL_ID_DAILY_SUMMARY = "daily_summary"
    private const val CHANNEL_ID_DELIVERY_REMINDERS = "delivery_reminders"
    private const val CHANNEL_ID_PAYMENT_REMINDERS = "payment_reminders"
    private const val CHANNEL_ID_OVERDUE_ALERTS = "overdue_alerts"
    private const val CHANNEL_ID_BIRTHDAY_REMINDERS = "birthday_reminders"
    
    private const val NOTIFICATION_ID_DAILY = 1001
    private const val NOTIFICATION_ID_DELIVERY = 2001
    private const val NOTIFICATION_ID_PAYMENT = 3001
    private const val NOTIFICATION_ID_OVERDUE = 4001
    private const val NOTIFICATION_ID_BIRTHDAY = 5001
    
    /**
     * Create all notification channels
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Daily Summary Channel
            val dailyChannel = NotificationChannel(
                CHANNEL_ID_DAILY_SUMMARY,
                "Daily Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily task summary and reminders"
            }
            
            // Delivery Reminders Channel
            val deliveryChannel = NotificationChannel(
                CHANNEL_ID_DELIVERY_REMINDERS,
                "Delivery Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for upcoming deliveries"
            }
            
            // Payment Reminders Channel
            val paymentChannel = NotificationChannel(
                CHANNEL_ID_PAYMENT_REMINDERS,
                "Payment Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for pending payments"
            }
            
            // Overdue Alerts Channel
            val overdueChannel = NotificationChannel(
                CHANNEL_ID_OVERDUE_ALERTS,
                "Overdue Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for overdue orders"
            }
            
            // Birthday Reminders Channel
            val birthdayChannel = NotificationChannel(
                CHANNEL_ID_BIRTHDAY_REMINDERS,
                "Birthday Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Customer birthday reminders"
            }
            
            notificationManager.createNotificationChannels(listOf(
                dailyChannel,
                deliveryChannel,
                paymentChannel,
                overdueChannel,
                birthdayChannel
            ))
        }
    }
    
    /**
     * Show daily summary notification
     */
    fun showDailySummaryNotification(
        context: Context,
        ordersDueToday: Int,
        pendingPayments: Int,
        overdueOrders: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val message = buildString {
            append("Good Morning! Today's tasks:\n")
            if (ordersDueToday > 0) append("• $ordersDueToday orders due for delivery\n")
            if (pendingPayments > 0) append("• $pendingPayments pending payments\n")
            if (overdueOrders > 0) append("• $overdueOrders overdue orders")
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DAILY_SUMMARY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🌅 Daily Summary")
            .setContentText("$ordersDueToday tasks today")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DAILY, notification)
    }
    
    /**
     * Show delivery reminder notification
     */
    fun showDeliveryReminderNotification(
        context: Context,
        orders: List<Order>
    ) {
        if (orders.isEmpty()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val message = buildString {
            append("Orders due tomorrow:\n")
            orders.take(5).forEach { order ->
                append("• ${order.orderId} - ${order.customerName}\n")
            }
            if (orders.size > 5) {
                append("and ${orders.size - 5} more...")
            }
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_DELIVERY_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏰ Deliveries Tomorrow")
            .setContentText("${orders.size} orders due for delivery")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_DELIVERY, notification)
    }
    
    /**
     * Show payment reminder notification
     */
    fun showPaymentReminderNotification(
        context: Context,
        orders: List<Order>,
        totalOutstanding: Double
    ) {
        if (orders.isEmpty()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val message = buildString {
            append("Pending payments:\n")
            orders.take(5).forEach { order ->
                append("• ${order.orderId} - ${order.formattedOutstanding}\n")
            }
            if (orders.size > 5) {
                append("and ${orders.size - 5} more...")
            }
            append("\nTotal: ₹${String.format("%.2f", totalOutstanding)}")
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_PAYMENT_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💰 Payment Collection")
            .setContentText("${orders.size} pending payments")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PAYMENT, notification)
    }
    
    /**
     * Show overdue orders alert
     */
    fun showOverdueOrdersAlert(
        context: Context,
        orders: List<Order>
    ) {
        if (orders.isEmpty()) return
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val message = buildString {
            append("⚠️ Overdue orders:\n")
            orders.take(5).forEach { order ->
                append("• ${order.orderId} - ${order.customerName}\n")
                append("  Due: ${order.estimatedDeliveryDate}\n")
            }
            if (orders.size > 5) {
                append("and ${orders.size - 5} more...")
            }
        }
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_OVERDUE_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🔴 Overdue Orders")
            .setContentText("${orders.size} orders are overdue")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_OVERDUE, notification)
    }
    
    /**
     * Show birthday reminder notification
     */
    fun sendBirthdayReminder(
        context: Context,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BIRTHDAY_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BIRTHDAY, notification)
    }
}

