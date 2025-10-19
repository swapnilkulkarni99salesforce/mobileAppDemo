package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents an image associated with an order.
 * 
 * This entity stores both reference images (customer-provided inspiration)
 * and completed work images (portfolio/before-after photos).
 * 
 * Image Types:
 * - REFERENCE: Customer-provided inspiration/design reference
 * - COMPLETED: Photos of finished work for portfolio
 * - PROGRESS: Work-in-progress photos
 * - DEFECT: Photos documenting issues/alterations needed
 * 
 * Storage:
 * - Images are stored in app's private storage
 * - filePath contains the relative path to the image file
 * - Actual file management handled by ImageHelper utility
 * 
 * @property id Auto-generated local database ID
 * @property orderId Foreign key to associated order
 * @property filePath Relative path to image file in storage
 * @property imageType Type of image (REFERENCE, COMPLETED, PROGRESS, DEFECT)
 * @property caption Optional description or notes about the image
 * @property uploadedAt Timestamp when image was added
 * @property displayOrder Order for displaying in gallery (lower = first)
 * 
 * @see [Order] for associated order
 */
@Entity(
    tableName = "order_images",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE  // Delete images when order is deleted
        )
    ],
    indices = [
        Index(value = ["orderId"]),
        Index(value = ["imageType"])
    ]
)
data class OrderImage(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    val orderId: Int,
    val filePath: String,
    val imageType: String,
    val caption: String = "",
    val uploadedAt: Long = System.currentTimeMillis(),
    val displayOrder: Int = 0
) {
    /**
     * Checks if this is a reference image (customer inspiration).
     */
    fun isReference(): Boolean = imageType == TYPE_REFERENCE
    
    /**
     * Checks if this is a completed work image (for portfolio).
     */
    fun isCompleted(): Boolean = imageType == TYPE_COMPLETED
    
    /**
     * Checks if this image should be shown in portfolio gallery.
     */
    fun isPortfolioWorthy(): Boolean = imageType == TYPE_COMPLETED
    
    companion object {
        // Image type constants
        const val TYPE_REFERENCE = "REFERENCE"      // Customer inspiration
        const val TYPE_COMPLETED = "COMPLETED"      // Finished work
        const val TYPE_PROGRESS = "PROGRESS"        // Work in progress
        const val TYPE_DEFECT = "DEFECT"           // Issue documentation
    }
}

