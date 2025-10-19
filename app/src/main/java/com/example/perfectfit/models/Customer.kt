package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a customer in the Perfect Fit tailoring application.
 * 
 * This entity stores customer personal information and maintains synchronization state
 * with the remote server. The composite unique index on (firstName, lastName, mobile)
 * ensures that duplicate customers cannot be created with the same combination of these fields.
 * 
 * @property id Auto-generated local database ID (primary key)
 * @property firstName Customer's first name (required)
 * @property lastName Customer's last name (required)
 * @property address Customer's residential address (required)
 * @property mobile Primary mobile number (required, part of unique constraint)
 * @property alternateMobile Secondary contact number (optional)
 * @property birthDate Customer's date of birth in dd/MM/yyyy format (required)
 * @property serverId MongoDB _id from the remote server (null until first sync)
 * @property lastModified Timestamp in milliseconds for conflict resolution during sync
 * @property syncStatus Current synchronization state (PENDING/SYNCED/FAILED)
 * 
 * @see [Order] for customer's orders
 * @see [Measurement] for customer's measurements
 */
@Entity(
    tableName = "customers",
    indices = [
        // Composite unique index prevents duplicate customers with same name and mobile
        Index(value = ["firstName", "lastName", "mobile"], unique = true)
    ]
)
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val firstName: String,
    val lastName: String,
    val address: String,
    val mobile: String,
    val alternateMobile: String,
    val birthDate: String,
    
    // ===== Birthday Alert Tracking =====
    val lastBirthdayAlertSent: Long = 0,  // Timestamp of last birthday greeting sent
    val birthdayAlertEnabled: Boolean = true,  // Whether to send birthday alerts for this customer
    
    // ===== Customer Analytics =====
    val totalOrdersValue: Double = 0.0,  // Cumulative value of all orders (for CLV calculation)
    val lastOrderDate: String = "",  // Date of most recent order
    
    // ===== Sync-related fields =====
    // These fields manage synchronization state with the remote server
    val serverId: String? = null,
    val lastModified: Long = System.currentTimeMillis(),
    val syncStatus: String = SYNC_PENDING
) {
    /**
     * Returns the customer's full name for display purposes.
     * Format: "FirstName LastName"
     */
    val fullName: String
        get() = "$firstName $lastName"
    
    /**
     * Checks if this customer has been successfully synced with the server.
     * @return true if syncStatus is SYNCED, false otherwise
     */
    fun isSynced(): Boolean = syncStatus == SYNC_SYNCED
    
    /**
     * Checks if this customer needs to be synced with the server.
     * @return true if syncStatus is PENDING or FAILED
     */
    fun needsSync(): Boolean = syncStatus == SYNC_PENDING || syncStatus == SYNC_FAILED
    
    /**
     * Validates if all required fields are properly filled.
     * @return true if all validation passes, false otherwise
     */
    fun isValid(): Boolean {
        return firstName.isNotBlank() &&
               lastName.isNotBlank() &&
               address.isNotBlank() &&
               mobile.isNotBlank() &&
               mobile.length >= 10 &&
               birthDate.isNotBlank()
    }
    
    /**
     * Returns a copy of this customer with updated lastModified timestamp.
     * Useful when making modifications that need to trigger a sync.
     */
    fun withUpdatedTimestamp(): Customer {
        return copy(
            lastModified = System.currentTimeMillis(),
            syncStatus = SYNC_PENDING
        )
    }
    
    /**
     * Checks if customer's birthday is today.
     * @return true if birthday matches today's date (day and month)
     */
    fun isBirthdayToday(): Boolean {
        return try {
            val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val birthDateParsed = format.parse(birthDate) ?: return false
            
            val birthCal = java.util.Calendar.getInstance().apply { time = birthDateParsed }
            val today = java.util.Calendar.getInstance()
            
            birthCal.get(java.util.Calendar.DAY_OF_MONTH) == today.get(java.util.Calendar.DAY_OF_MONTH) &&
            birthCal.get(java.util.Calendar.MONTH) == today.get(java.util.Calendar.MONTH)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Checks if birthday alert should be sent (birthday today and not already sent).
     */
    fun shouldSendBirthdayAlert(): Boolean {
        if (!birthdayAlertEnabled || !isBirthdayToday()) return false
        
        // Check if alert already sent today
        val lastAlertCal = java.util.Calendar.getInstance().apply {
            timeInMillis = lastBirthdayAlertSent
        }
        val today = java.util.Calendar.getInstance()
        
        val alreadySentToday = lastAlertCal.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) &&
                               lastAlertCal.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
        
        return !alreadySentToday
    }
    
    /**
     * Returns customer lifetime value (total spent).
     */
    fun getLifetimeValue(): Double = totalOrdersValue
    
    /**
     * Returns formatted lifetime value.
     */
    fun getFormattedLifetimeValue(): String = "₹${String.format("%.2f", totalOrdersValue)}"
    
    companion object {
        // Synchronization status constants
        const val SYNC_PENDING = "PENDING"  // Waiting to be synced to server
        const val SYNC_SYNCED = "SYNCED"    // Successfully synced with server
        const val SYNC_FAILED = "FAILED"    // Sync attempt failed, will retry
    }
}

