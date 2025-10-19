package com.example.perfectfit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.perfectfit.models.Customer
import com.example.perfectfit.models.Measurement
import com.example.perfectfit.models.Order
import com.example.perfectfit.models.OrderImage
import com.example.perfectfit.models.OrderStageHistory
import com.example.perfectfit.models.ProductionStage
import com.example.perfectfit.models.WorkloadConfig

/**
 * Room Database for the Perfect Fit tailoring application.
 * 
 * This is the main database configuration class that serves as the access point
 * to the persisted data. It defines the database schema, version, and provides
 * access to Data Access Objects (DAOs).
 * 
 * Database Schema:
 * - customers: Stores customer personal information with CLV tracking
 * - measurements: Stores body measurements for each customer
 * - orders: Stores order information with foreign key to customers
 * - workload_config: Stores workload management configuration
 * - order_images: Stores reference and portfolio images for orders
 * - production_stages: Tracks current production stage of each order
 * - order_stage_history: Maintains complete stage transition history
 * 
 * Relationships:
 * - One customer can have multiple orders (one-to-many)
 * - One customer has one measurement record (one-to-one)
 * - One order can have multiple images (one-to-many)
 * - One order has one active production stage (one-to-one)
 * - One order has multiple stage history entries (one-to-many)
 * - Orders, measurements, images, and stages CASCADE delete when customer/order is deleted
 * 
 * Version History:
 * - Version 9: Base version with customers, orders, measurements, workload
 * - Version 10: Added production tracking, images, CLV, birthday alerts
 * 
 * Note: exportSchema = false means Room won't export database schema to a folder.
 * In production apps, consider setting this to true and tracking schema changes.
 * 
 * @see [Customer] for customer entity details
 * @see [Measurement] for measurement entity details
 * @see [Order] for order entity details
 * @see [WorkloadConfig] for workload configuration details
 */
@Database(
    entities = [
        Customer::class,
        Measurement::class,
        Order::class,
        WorkloadConfig::class,
        OrderImage::class,
        ProductionStage::class,
        OrderStageHistory::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to customer-related database operations.
     * @return CustomerDao instance for CRUD operations on customers
     */
    abstract fun customerDao(): CustomerDao
    
    /**
     * Provides access to measurement-related database operations.
     * @return MeasurementDao instance for CRUD operations on measurements
     */
    abstract fun measurementDao(): MeasurementDao
    
    /**
     * Provides access to order-related database operations.
     * @return OrderDao instance for CRUD operations on orders
     */
    abstract fun orderDao(): OrderDao
    
    /**
     * Provides access to workload configuration database operations.
     * @return WorkloadConfigDao instance for managing workload settings
     */
    abstract fun workloadConfigDao(): WorkloadConfigDao
    
    /**
     * Provides access to order image database operations.
     * @return OrderImageDao instance for managing order images
     */
    abstract fun orderImageDao(): OrderImageDao
    
    /**
     * Provides access to production stage database operations.
     * @return ProductionStageDao instance for managing production stages
     */
    abstract fun productionStageDao(): ProductionStageDao
    
    /**
     * Provides access to order stage history database operations.
     * @return OrderStageHistoryDao instance for managing stage history
     */
    abstract fun orderStageHistoryDao(): OrderStageHistoryDao
    
    companion object {
        /**
         * Singleton instance of the database.
         * 
         * @Volatile ensures that changes to INSTANCE are immediately visible to all threads.
         * This prevents threads from caching the value and seeing stale data.
         * 
         * Without @Volatile, one thread might write to INSTANCE while another thread
         * reads a stale null value, leading to multiple database instances being created.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the singleton database instance.
         * 
         * This method implements the double-checked locking pattern for thread-safe
         * lazy initialization of the database singleton:
         * 
         * 1. First check (unsynchronized): If instance exists, return immediately.
         *    This avoids the performance overhead of synchronization after first initialization.
         * 
         * 2. synchronized(this): If instance is null, acquire lock to prevent multiple
         *    threads from creating instances simultaneously.
         * 
         * 3. Second check (inside synchronized block): Check again if another thread
         *    created the instance while this thread was waiting for the lock.
         * 
         * 4. Create instance: If still null, create the database instance.
         * 
         * Why use synchronized(this) and not a Lock?
         * - synchronized is simpler and sufficient for this use case
         * - The performance overhead is negligible since initialization happens once
         * - @Volatile + synchronized provides the memory visibility guarantees we need
         * 
         * Note on .fallbackToDestructiveMigration():
         * - This destroys the database and recreates it if a migration path is not found
         * - Convenient for development but DANGEROUS for production apps with user data
         * - In production, implement proper migration strategies using Room's Migration class
         * 
         * @param context The application context (not activity context to avoid memory leaks)
         * @return The singleton AppDatabase instance
         */
        fun getDatabase(context: Context): AppDatabase {
            // First check (no locking) - fast path for already initialized instance
            return INSTANCE ?: synchronized(this) {
                // Second check (with lock) - ensures only one thread creates the instance
                val instance = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,  // Use app context to avoid memory leaks
                    AppDatabase::class.java,
                    "perfect_fit_database"
                )
                    .fallbackToDestructiveMigration()  // CAUTION: Destroys data on version mismatch
                    .build()
                    
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Clears the database instance.
         * Useful for testing or when you need to force database recreation.
         * 
         * WARNING: Only call this in test code or when the app is not using the database.
         */
        @Suppress("unused")
        fun clearInstance() {
            INSTANCE = null
        }
    }
}

