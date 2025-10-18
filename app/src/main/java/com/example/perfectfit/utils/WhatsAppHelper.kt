package com.example.perfectfit.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import com.example.perfectfit.models.Order

/**
 * Utility class for WhatsApp integration
 */
object WhatsAppHelper {
    
    /**
     * Check if WhatsApp is installed
     */
    fun isWhatsAppInstalled(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * Send WhatsApp message to a phone number
     */
    fun sendMessage(context: Context, phoneNumber: String, message: String) {
        try {
            if (!isWhatsAppInstalled(context)) {
                Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Clean phone number (remove spaces, dashes, etc.)
            val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}")
                setPackage("com.whatsapp")
            }
            
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Send order confirmation message
     */
    fun sendOrderConfirmation(context: Context, order: Order, phoneNumber: String) {
        val message = buildString {
            append("🎉 *Order Confirmed*\n\n")
            append("Dear ${order.customerName},\n\n")
            append("Your order has been confirmed!\n\n")
            append("*Order Details:*\n")
            append("Order ID: ${order.orderId}\n")
            append("Order Type: ${order.orderType}\n")
            append("Amount: ${order.formattedAmount}\n")
            append("Estimated Delivery: ${order.estimatedDeliveryDate}\n\n")
            if (order.advancePayment > 0) {
                append("Advance Paid: ₹${String.format("%.2f", order.advancePayment)}\n")
                append("Balance: ${order.formattedOutstanding}\n\n")
            }
            append("Thank you for your order! 🙏\n")
            append("We'll notify you when it's ready for pickup.")
        }
        
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send delivery reminder
     */
    fun sendDeliveryReminder(context: Context, order: Order, phoneNumber: String) {
        val message = buildString {
            append("⏰ *Delivery Reminder*\n\n")
            append("Dear ${order.customerName},\n\n")
            append("This is a reminder that your order ${order.orderId} is scheduled for delivery on ${order.estimatedDeliveryDate}.\n\n")
            if (order.outstandingAmount > 0) {
                append("*Payment Due:* ${order.formattedOutstanding}\n\n")
            }
            append("Thank you! 🙏")
        }
        
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send order ready notification
     */
    fun sendOrderReadyMessage(context: Context, order: Order, phoneNumber: String) {
        val message = buildString {
            append("✅ *Order Ready for Pickup*\n\n")
            append("Dear ${order.customerName},\n\n")
            append("Great news! Your order ${order.orderId} is ready for pickup! 🎉\n\n")
            append("*Order Type:* ${order.orderType}\n")
            if (order.outstandingAmount > 0) {
                append("*Amount Due:* ${order.formattedOutstanding}\n\n")
            } else {
                append("*Payment:* ✅ Paid\n\n")
            }
            append("Please collect at your convenience.\n\n")
            append("Thank you! 🙏")
        }
        
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send payment reminder
     */
    fun sendPaymentReminder(context: Context, order: Order, phoneNumber: String) {
        val message = buildString {
            append("💰 *Payment Reminder*\n\n")
            append("Dear ${order.customerName},\n\n")
            append("This is a friendly reminder for the pending payment on your order ${order.orderId}.\n\n")
            append("*Total Amount:* ${order.formattedAmount}\n")
            append("*Paid:* ${order.formattedTotalPaid}\n")
            append("*Balance Due:* ${order.formattedOutstanding}\n\n")
            append("Please make the payment at your earliest convenience.\n\n")
            append("Thank you! 🙏")
        }
        
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send order completed thank you message
     */
    fun sendOrderCompletedMessage(context: Context, order: Order, phoneNumber: String) {
        val message = buildString {
            append("🌟 *Thank You!*\n\n")
            append("Dear ${order.customerName},\n\n")
            append("Thank you for choosing us! We hope you're delighted with your ${order.orderType}! 😊\n\n")
            append("Your satisfaction is our priority. We look forward to serving you again!\n\n")
            append("Please feel free to share your feedback or refer us to friends and family.\n\n")
            append("Warm regards! 🙏")
        }
        
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Send custom message
     */
    fun sendCustomMessage(context: Context, phoneNumber: String, customerName: String, customMessage: String) {
        val message = buildString {
            append("Dear $customerName,\n\n")
            append(customMessage)
            append("\n\nThank you! 🙏")
        }
        
        sendMessage(context, phoneNumber, message)
    }
    
    /**
     * Get order status message template
     */
    fun getOrderStatusMessage(order: Order): String {
        return when (order.status.lowercase()) {
            "pending" -> "Your order ${order.orderId} is pending and will be started soon."
            "in progress" -> "Your order ${order.orderId} is currently in progress. We're working on it! 👷"
            "completed" -> "Your order ${order.orderId} is completed and ready for pickup! ✅"
            else -> "Your order ${order.orderId} status: ${order.status}"
        }
    }
}

