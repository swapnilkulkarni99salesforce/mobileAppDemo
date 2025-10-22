package com.example.perfectfit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for managing order and portfolio images.
 * 
 * Features:
 * - Save images from URI to app storage
 * - Compress images to reduce storage
 * - Delete images and clean up files
 * - Get URI for sharing
 * - Handle image rotation (EXIF data)
 * 
 * Storage Structure:
 * - Images stored in: /data/data/com.example.perfectfit/files/images/
 * - Naming: order_{orderId}_{type}_{timestamp}.jpg
 * - Example: order_123_REFERENCE_1698334567890.jpg
 * 
 * @param context Application context
 */
class ImageHelper(private val context: Context) {

    private val imagesDir: File by lazy {
        File(context.filesDir, IMAGES_DIRECTORY).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    private val portfolioDir: File by lazy {
        File(context.filesDir, PORTFOLIO_DIRECTORY).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * Saves an image from URI to app storage with compression.
     * 
     * @param uri Source image URI (from gallery or camera)
     * @param orderId The order ID this image belongs to
     * @param imageType Image type (REFERENCE, COMPLETED, etc.)
     * @return Relative file path within app storage, or null if failed
     */
    fun saveImage(uri: Uri, orderId: Int, imageType: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            // Determine target directory based on image type
            val isPortfolio = imageType == "COMPLETED"
            val targetDir = if (isPortfolio) portfolioDir else imagesDir
            
            // Generate unique filename
            val timestamp = System.currentTimeMillis()
            val fileName = "order_${orderId}_${imageType}_${timestamp}.jpg"
            val targetFile = File(targetDir, fileName)
            
            // Load and compress image
            val bitmap = loadAndOrientBitmap(inputStream, uri)
            val compressedBitmap = compressBitmap(bitmap)
            
            // Save to file
            FileOutputStream(targetFile).use { outputStream ->
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
            }
            
            // Clean up
            bitmap.recycle()
            if (bitmap != compressedBitmap) {
                compressedBitmap.recycle()
            }
            inputStream.close()
            
            // Return relative path with directory prefix if portfolio
            if (isPortfolio) {
                "$PORTFOLIO_DIRECTORY/$fileName"
            } else {
                fileName
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Loads bitmap and corrects orientation based on EXIF data.
     * 
     * @param inputStream Image input stream
     * @param uri Source URI for EXIF reading
     * @return Correctly oriented bitmap
     */
    private fun loadAndOrientBitmap(inputStream: InputStream, uri: Uri): Bitmap {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        
        // Try to read EXIF orientation
        try {
            val exifInputStream = context.contentResolver.openInputStream(uri)
            exifInputStream?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                
                return when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                    else -> bitmap
                }
            }
        } catch (e: Exception) {
            // If EXIF reading fails, return original bitmap
            e.printStackTrace()
        }
        
        return bitmap
    }

    /**
     * Rotates a bitmap by specified degrees.
     * 
     * @param bitmap Source bitmap
     * @param degrees Rotation angle
     * @return Rotated bitmap
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotated
    }

    /**
     * Compresses bitmap if it exceeds maximum dimensions.
     * 
     * @param bitmap Source bitmap
     * @return Compressed bitmap (or original if already small enough)
     */
    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        // Check if resize needed
        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return bitmap
        }
        
        // Calculate scale factor
        val scale = if (width > height) {
            MAX_IMAGE_SIZE.toFloat() / width
        } else {
            MAX_IMAGE_SIZE.toFloat() / height
        }
        
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        
        val resized = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        if (resized != bitmap) {
            bitmap.recycle()
        }
        
        return resized
    }

    /**
     * Deletes an image file.
     * 
     * @param filePath Relative file path (as stored in database)
     * @return true if deleted successfully, false otherwise
     */
    fun deleteImage(filePath: String): Boolean {
        return try {
            // Determine if it's a portfolio image
            val file = if (filePath.startsWith("$PORTFOLIO_DIRECTORY/")) {
                File(context.filesDir, filePath)
            } else {
                File(imagesDir, filePath)
            }
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Gets the full file path for an image.
     * 
     * @param filePath Relative file path from database
     * @return Full file path
     */
    fun getFullPath(filePath: String): String {
        return if (filePath.startsWith("$PORTFOLIO_DIRECTORY/")) {
            File(context.filesDir, filePath).absolutePath
        } else {
            File(imagesDir, filePath).absolutePath
        }
    }

    /**
     * Gets a content URI for an image (for sharing).
     * Uses FileProvider for secure file sharing.
     * 
     * @param filePath Relative file path from database
     * @return Content URI for the image, or null if file doesn't exist
     */
    fun getImageUri(filePath: String): Uri? {
        return try {
            val file = if (filePath.startsWith("$PORTFOLIO_DIRECTORY/")) {
                File(context.filesDir, filePath)
            } else {
                File(imagesDir, filePath)
            }
            
            if (file.exists()) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Checks if an image file exists.
     * 
     * @param filePath Relative file path from database
     * @return true if file exists, false otherwise
     */
    fun imageExists(filePath: String): Boolean {
        val file = File(imagesDir, filePath)
        return file.exists()
    }

    /**
     * Gets the file size of an image in bytes.
     * 
     * @param filePath Relative file path from database
     * @return File size in bytes, or 0 if file doesn't exist
     */
    fun getImageSize(filePath: String): Long {
        val file = File(imagesDir, filePath)
        return if (file.exists()) file.length() else 0L
    }

    /**
     * Deletes all images for a specific order.
     * 
     * @param orderId The order ID
     * @return Number of images deleted
     */
    fun deleteAllImagesForOrder(orderId: Int): Int {
        var count = 0
        try {
            imagesDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("order_${orderId}_")) {
                    if (file.delete()) count++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return count
    }

    /**
     * Gets total storage used by all images.
     * 
     * @return Total size in bytes
     */
    fun getTotalStorageUsed(): Long {
        var total = 0L
        try {
            imagesDir.listFiles()?.forEach { file ->
                total += file.length()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return total
    }

    /**
     * Formats file size in human-readable format.
     * 
     * @param bytes File size in bytes
     * @return Formatted string (e.g., "2.5 MB")
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    /**
     * Cleans up old orphaned images (images without database records).
     * This should be called periodically as maintenance.
     * 
     * @param validFilePaths List of valid file paths from database
     * @return Number of orphaned images deleted
     */
    fun cleanupOrphanedImages(validFilePaths: List<String>): Int {
        var count = 0
        try {
            val validSet = validFilePaths.toSet()
            imagesDir.listFiles()?.forEach { file ->
                if (file.name !in validSet) {
                    if (file.delete()) count++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return count
    }

    companion object {
        private const val IMAGES_DIRECTORY = "images"
        private const val PORTFOLIO_DIRECTORY = "portfolio"
        private const val MAX_IMAGE_SIZE = 1920  // Max width/height in pixels
        private const val JPEG_QUALITY = 85      // JPEG compression quality (0-100)
    }
}

