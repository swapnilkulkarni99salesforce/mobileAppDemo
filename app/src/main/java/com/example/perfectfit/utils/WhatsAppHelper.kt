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
                // Fallback: Show options to share via other apps
                showShareOptions(context, phoneNumber, message)
                return
            }
            
            // Clean phone number (remove spaces, dashes, parentheses, etc.)
            var cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
            
            // If number doesn't start with +, try adding country code
            // Note: You may need to adjust country code based on your location
            if (!cleanNumber.startsWith("+")) {
                // For India, add +91 (adjust this for your country)
                cleanNumber = "+91$cleanNumber"
            }
            
            // Try method 1: Using WhatsApp API URL (web-based)
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}")
                }
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // If web API fails, try direct intent
            }
            
            // Try method 2: Using direct WhatsApp intent
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    setPackage("com.whatsapp")
                    putExtra(Intent.EXTRA_TEXT, "$message\n\nPhone: $phoneNumber")
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // If both methods fail, show share dialog
                showShareOptions(context, phoneNumber, message)
            }
            
        } catch (e: Exception) {
            Toast.makeText(context, "Opening WhatsApp... If it doesn't work, try SMS", Toast.LENGTH_LONG).show()
            // Fallback to share dialog
            showShareOptions(context, phoneNumber, message)
        }
    }
    
    /**
     * Show share options when WhatsApp is not available
     */
    private fun showShareOptions(context: Context, phoneNumber: String, message: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$message\n\nCustomer: $phoneNumber")
            type = "text/plain"
        }
        
        val chooserIntent = Intent.createChooser(shareIntent, "Share via")
        context.startActivity(chooserIntent)
    }
    
    /**
     * Send SMS as fallback
     */
    fun sendViaSMS(context: Context, phoneNumber: String, message: String) {
        try {
            val smsIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:$phoneNumber")
                putExtra("sms_body", message)
            }
            context.startActivity(smsIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * Share via any app (email, messaging, etc.)
     */
    fun shareMessage(context: Context, customerName: String, message: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Message for $customerName")
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share message via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing message: ${e.message}", Toast.LENGTH_SHORT).show()
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

