package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents an order in the Perfect Fit tailoring application.
 * 
 * This entity manages customer orders with payment tracking and delivery scheduling.
 * Orders are linked to customers via a foreign key with CASCADE delete, meaning
 * deleting a customer will automatically delete all their orders.
 * 
 * @property id Auto-generated local database ID (primary key)
 * @property customerId Foreign key reference to the Customer who placed this order
 * @property customerName Denormalized customer name for display efficiency
 * @property orderDate Date when the order was placed (dd/MM/yyyy format)
 * @property orderType Type of garment: "Blouse" or "Kurti and Pant"
 * @property estimatedDeliveryDate Expected delivery date (dd/MM/yyyy format)
 * @property instructions Special instructions or notes for this order
 * @property amount Total order amount in rupees
 * @property status Order status: Pending, In Progress, Completed, or Closed
 * @property advancePayment Amount paid upfront at order placement
 * @property balancePayment Additional payments made after advance
 * @property paymentStatus Payment state: Unpaid, Partial, or Paid
 * @property paymentDate Date when the order was fully paid (dd/MM/yyyy format)
 * @property serverId MongoDB _id from the remote server (null until first sync)
 * @property lastModified Timestamp in milliseconds for conflict resolution during sync
 * @property syncStatus Current synchronization state (PENDING/SYNCED/FAILED)
 * 
 * @see [Customer] for the customer who placed this order
 */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE  // Delete orders when customer is deleted
        )
    ],
    indices = [
        Index(value = ["customerId"])  // Index for efficient customer order lookups
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    // ===== Customer Information =====
    val customerId: Int,
    val customerName: String,  // Denormalized for display efficiency
    
    // ===== Order Details =====
    val orderDate: String,
    val orderType: String,  // "Blouse" or "Kurti and Pant"
    val estimatedDeliveryDate: String,
    val instructions: String,
    val amount: Double,
    val status: String = STATUS_PENDING,
    
    // ===== Payment Information =====
    val advancePayment: Double = 0.0,
    val balancePayment: Double = 0.0,
    val paymentStatus: String = PAYMENT_UNPAID,
    val paymentDate: String? = null,
    
    // ===== Sync-related fields =====
    val serverId: String? = null,
    val lastModified: Long = System.currentTimeMillis(),
    val syncStatus: String = SYNC_PENDING
) {
    /**
     * Returns a formatted order ID for display.
     * Format: "Order #00001"
     */
    val orderId: String
        get() = "Order #${id.toString().padStart(5, '0')}"
    
    /**
     * Returns the total amount formatted with rupee symbol.
     * Format: "₹1,234.56"
     */
    val formattedAmount: String
        get() = "₹${String.format("%.2f", amount)}"
    
    /**
     * Calculates the total amount paid so far.
     * @return Sum of advance payment and balance payment
     */
    val totalPaid: Double
        get() = advancePayment + balancePayment
    
    /**
     * Calculates the remaining amount to be paid.
     * @return Order amount minus total paid
     */
    val outstandingAmount: Double
        get() = (amount - totalPaid).coerceAtLeast(0.0)  // Ensure non-negative
    
    /**
     * Returns the outstanding amount formatted with rupee symbol.
     * Format: "₹1,234.56"
     */
    val formattedOutstanding: String
        get() = "₹${String.format("%.2f", outstandingAmount)}"
    
    /**
     * Returns the total paid amount formatted with rupee symbol.
     * Format: "₹1,234.56"
     */
    val formattedTotalPaid: String
        get() = "₹${String.format("%.2f", totalPaid)}"
    
    /**
     * Checks if the order payment is fully completed.
     * @return true if payment status is PAID
     */
    fun isFullyPaid(): Boolean = paymentStatus == PAYMENT_PAID
    
    /**
     * Checks if the order has any outstanding payment.
     * @return true if payment status is UNPAID or PARTIAL
     */
    fun hasOutstandingPayment(): Boolean = 
        paymentStatus == PAYMENT_UNPAID || paymentStatus == PAYMENT_PARTIAL
    
    /**
     * Checks if the order is still active (not completed or closed).
     * @return true if status is PENDING or IN_PROGRESS
     */
    fun isActive(): Boolean = 
        status == STATUS_PENDING || status == STATUS_IN_PROGRESS
    
    /**
     * Checks if the order is completed.
     * @return true if status is COMPLETED or DELIVERED
     */
    fun isCompleted(): Boolean = 
        status.equals(STATUS_COMPLETED, ignoreCase = true) || 
        status.equals("Delivered", ignoreCase = true)
    
    /**
     * Returns a copy of this order with updated payment information.
     * Automatically calculates and updates the payment status based on amounts.
     * 
     * @param advance New advance payment amount
     * @param balance New balance payment amount
     * @param date Payment date (optional, uses current date if fully paid)
     * @return Updated copy of the order
     */
    fun withUpdatedPayment(
        advance: Double = this.advancePayment,
        balance: Double = this.balancePayment,
        date: String? = this.paymentDate
    ): Order {
        val newTotalPaid = advance + balance
        val newPaymentStatus = when {
            newTotalPaid >= amount -> PAYMENT_PAID
            newTotalPaid > 0 -> PAYMENT_PARTIAL
            else -> PAYMENT_UNPAID
        }
        
        return copy(
            advancePayment = advance,
            balancePayment = balance,
            paymentStatus = newPaymentStatus,
            paymentDate = if (newPaymentStatus == PAYMENT_PAID) date else null,
            lastModified = System.currentTimeMillis(),
            syncStatus = SYNC_PENDING
        )
    }
    
    /**
     * Returns a copy of this order with updated sync information.
     * @param newServerId Server ID from remote database
     * @param newSyncStatus New sync status
     * @return Updated copy of the order
     */
    fun withSyncInfo(newServerId: String, newSyncStatus: String): Order {
        return copy(
            serverId = newServerId,
            syncStatus = newSyncStatus,
            lastModified = System.currentTimeMillis()
        )
    }
    
    companion object {
        // Order status constants
        const val STATUS_PENDING = "Pending"
        const val STATUS_IN_PROGRESS = "In Progress"
        const val STATUS_COMPLETED = "Completed"
        const val STATUS_CLOSED = "Closed"
        
        // Payment status constants
        const val PAYMENT_UNPAID = "Unpaid"     // No payment received
        const val PAYMENT_PARTIAL = "Partial"   // Partial payment received
        const val PAYMENT_PAID = "Paid"         // Full payment received
        
        // Synchronization status constants
        const val SYNC_PENDING = "PENDING"      // Waiting to be synced to server
        const val SYNC_SYNCED = "SYNCED"        // Successfully synced with server
        const val SYNC_FAILED = "FAILED"        // Sync attempt failed, will retry
        
        // Order type constants
        const val TYPE_BLOUSE = "Blouse"
        const val TYPE_KURTI_PANT = "Kurti and Pant"
        const val TYPE_ANAARKALI = "Anaarkali"
        const val TYPE_SAREE_DRESS = "Saree Dress"
        const val TYPE_DESIGNER_BLOUSE = "Designer Blouse"
        const val TYPE_KIDS_DRESS = "Kids Dress"
    }
}

