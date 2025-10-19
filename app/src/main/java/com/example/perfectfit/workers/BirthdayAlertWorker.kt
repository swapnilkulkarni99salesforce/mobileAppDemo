package com.example.perfectfit.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.perfectfit.database.AppDatabase
import com.example.perfectfit.utils.NotificationHelper
import com.example.perfectfit.utils.WhatsAppHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Background worker that checks for customer birthdays daily and sends greetings.
 * 
 * This worker runs once per day to:
 * - Find customers with birthdays today
 * - Send WhatsApp birthday wishes
 * - Notify shop owner about birthdays
 * - Update tracking to prevent duplicate sends
 * 
 * Scheduling:
 * - Runs daily at specified time (configured in Application or MainActivity)
 * - Uses WorkManager's periodic work with 24-hour interval
 * - Persists across device reboots
 * 
 * @see [Customer.shouldSendBirthdayAlert] for alert logic
 * @see [WhatsAppHelper] for message sending
 * @see [NotificationHelper] for owner notifications
 */
class BirthdayAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    /**
     * Executes the birthday check and notification workflow.
     * 
     * @return Result.success() if checks completed successfully, even if no birthdays found
     * @return Result.retry() if a transient error occurred
     * @return Result.failure() if a permanent error occurred
     */
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val customerDao = database.customerDao()
            
            // Get all customers
            val allCustomers = customerDao.getAllCustomersList()
            
            // Filter customers with birthdays today
            val birthdayCustomers = allCustomers.filter { customer ->
                customer.shouldSendBirthdayAlert()
            }
            
            if (birthdayCustomers.isEmpty()) {
                // No birthdays today - success but no action needed
                return@withContext Result.success()
            }
            
            // Process each birthday customer
            birthdayCustomers.forEach { customer ->
                try {
                    // Send birthday wish via WhatsApp
                    sendBirthdayWish(customer.mobile, customer.firstName)
                    
                    // Send notification to shop owner
                    NotificationHelper.sendBirthdayReminder(
                        applicationContext,
                        "🎂 ${customer.fullName}'s Birthday!",
                        "Tap to call and wish them. Send them a special discount!"
                    )
                    
                    // Update last alert timestamp
                    val updatedCustomer = customer.copy(
                        lastBirthdayAlertSent = System.currentTimeMillis()
                    )
                    customerDao.updateCustomer(updatedCustomer)
                    
                } catch (e: Exception) {
                    // Log error but continue with other customers
                    e.printStackTrace()
                }
            }
            
            // Send summary notification if multiple birthdays
            if (birthdayCustomers.size > 1) {
                NotificationHelper.sendBirthdayReminder(
                    applicationContext,
                    "🎉 ${birthdayCustomers.size} Birthdays Today!",
                    birthdayCustomers.joinToString(", ") { it.firstName }
                )
            }
            
            Result.success()
            
        } catch (e: Exception) {
            // Unexpected error - log and retry
            e.printStackTrace()
            Result.retry()
        }
    }
    
    /**
     * Sends a birthday wish message via WhatsApp.
     * 
     * @param phoneNumber Customer's mobile number
     * @param customerName Customer's first name for personalization
     */
    private fun sendBirthdayWish(phoneNumber: String, customerName: String) {
        val birthdayMessages = listOf(
            "🎂 Happy Birthday $customerName! 🎉\n\nWishing you a wonderful day filled with joy and happiness! " +
                    "As a special birthday gift, visit us this month for 10% OFF on your next order! 🎁\n\n" +
                    "- Perfect Fit Tailors",
            
            "🎉 Happiest Birthday $customerName! 🎂\n\nMay this year bring you success and prosperity! " +
                    "Celebrate with us - Get a special birthday discount of 10% on any order this month! 🎁\n\n" +
                    "- Perfect Fit Tailors",
            
            "🎂 Many Happy Returns $customerName! 🎉\n\nThank you for being our valued customer! " +
                    "As our birthday gift to you - Enjoy 10% OFF on your next tailoring order! Valid all month! 🎁\n\n" +
                    "- Perfect Fit Tailors"
        )
        
        // Pick a random message for variety
        val message = birthdayMessages.random()
        
        WhatsAppHelper.sendCustomMessage(
            applicationContext,
            phoneNumber,
            customerName,
            message
        )
    }
    
    companion object {
        const val WORK_NAME = "BirthdayAlertWorker"
    }
}

