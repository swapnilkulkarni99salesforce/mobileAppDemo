package com.example.perfectfit.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.perfectfit.models.OrderImage

/**
 * Data Access Object (DAO) for OrderImage entity operations.
 * 
 * This interface provides methods for managing order images including
 * reference images (customer inspiration) and portfolio images (completed work).
 * 
 * All suspend functions are designed to be called from coroutines.
 */
@Dao
interface OrderImageDao {
    
    // ===== Basic CRUD Operations =====
    
    /**
     * Inserts a new order image.
     * @param image The image to insert
     * @return The row ID of the inserted image
     */
    @Insert
    suspend fun insert(image: OrderImage): Long
    
    /**
     * Updates an existing order image.
     * @param image The image with updated values
     */
    @Update
    suspend fun update(image: OrderImage)
    
    /**
     * Deletes an order image.
     * Note: The actual image file must be deleted separately using ImageHelper.
     * @param image The image to delete
     */
    @Delete
    suspend fun delete(image: OrderImage)
    
    // ===== Query Operations =====
    
    /**
     * Retrieves all images for a specific order.
     * @param orderId The order's local database ID
     * @return List of images ordered by displayOrder
     */
    @Query("SELECT * FROM order_images WHERE orderId = :orderId ORDER BY displayOrder ASC, uploadedAt DESC")
    suspend fun getImagesByOrderId(orderId: Int): List<OrderImage>
    
    /**
     * Retrieves all images of a specific type for an order.
     * @param orderId The order's local database ID
     * @param imageType The image type (REFERENCE, COMPLETED, etc.)
     * @return List of matching images
     */
    @Query("SELECT * FROM order_images WHERE orderId = :orderId AND imageType = :imageType ORDER BY displayOrder ASC")
    suspend fun getImagesByOrderAndType(orderId: Int, imageType: String): List<OrderImage>
    
    /**
     * Retrieves all portfolio-worthy images (completed work).
     * Used for displaying in the portfolio gallery.
     * @return List of completed work images, newest first
     */
    @Query("SELECT * FROM order_images WHERE imageType = 'COMPLETED' ORDER BY uploadedAt DESC")
    suspend fun getAllPortfolioImages(): List<OrderImage>
    
    /**
     * Retrieves all portfolio images as LiveData for reactive updates.
     * @return LiveData list of completed work images
     */
    @Query("SELECT * FROM order_images WHERE imageType = 'COMPLETED' ORDER BY uploadedAt DESC")
    fun getPortfolioImagesLiveData(): LiveData<List<OrderImage>>
    
    /**
     * Retrieves reference images for an order.
     * @param orderId The order's local database ID
     * @return List of reference images
     */
    @Query("SELECT * FROM order_images WHERE orderId = :orderId AND imageType = 'REFERENCE' ORDER BY uploadedAt ASC")
    suspend fun getReferenceImages(orderId: Int): List<OrderImage>
    
    /**
     * Retrieves completed work images for an order.
     * @param orderId The order's local database ID
     * @return List of completed work images
     */
    @Query("SELECT * FROM order_images WHERE orderId = :orderId AND imageType = 'COMPLETED' ORDER BY uploadedAt ASC")
    suspend fun getCompletedImages(orderId: Int): List<OrderImage>
    
    /**
     * Counts the number of images for an order.
     * @param orderId The order's local database ID
     * @return Total image count
     */
    @Query("SELECT COUNT(*) FROM order_images WHERE orderId = :orderId")
    suspend fun getImageCount(orderId: Int): Int
    
    /**
     * Deletes all images for an order.
     * Used when order is deleted (though CASCADE should handle this).
     * @param orderId The order's local database ID
     */
    @Query("DELETE FROM order_images WHERE orderId = :orderId")
    suspend fun deleteAllImagesForOrder(orderId: Int)
}

