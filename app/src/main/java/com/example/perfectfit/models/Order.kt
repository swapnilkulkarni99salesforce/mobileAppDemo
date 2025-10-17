package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int,
    val customerName: String,
    val orderDate: String,
    val orderType: String, // "Blouse" or "Kurti and Pant"
    val estimatedDeliveryDate: String,
    val instructions: String,
    val amount: Double,
    val status: String = "Pending" // Pending, In Progress, Completed, Closed
) {
    // Computed property for order ID display
    val orderId: String
        get() = "Order #${id.toString().padStart(5, '0')}"
    
    // Computed property for formatted amount with Rupee symbol
    val formattedAmount: String
        get() = "₹${String.format("%.2f", amount)}"
}

