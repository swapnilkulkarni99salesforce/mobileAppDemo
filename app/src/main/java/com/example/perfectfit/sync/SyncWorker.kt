package com.example.perfectfit.sync

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * WorkManager worker for background sync
 */
class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    companion object {
        private const val TAG = "SyncWorker"
        const val WORK_NAME = "perfect_fit_sync"
        
        /**
         * Schedule periodic sync (runs every 15 minutes when conditions are met)
         */
        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Only when network available
                .setRequiresBatteryNotLow(true) // Only when battery is not low
                .build()
            
            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES // Minimum is 15 minutes
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Keep existing work
                syncRequest
            )
            
            Log.d(TAG, "Periodic sync scheduled")
        }
        
        /**
         * Schedule one-time immediate sync
         */
        fun scheduleImmediateSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            
            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            
            WorkManager.getInstance(context).enqueueUniqueWork(
                "${WORK_NAME}_immediate",
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
            
            Log.d(TAG, "Immediate sync scheduled")
        }
        
        /**
         * Cancel all sync work
         */
        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "Sync cancelled")
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting background sync...")
            
            val syncManager = SyncManager(applicationContext)
            val result = syncManager.syncAll()
            
            when (result) {
                SyncManager.SYNC_SUCCESS -> {
                    Log.d(TAG, "Background sync completed successfully")
                    Result.success()
                }
                SyncManager.SYNC_PARTIAL -> {
                    Log.w(TAG, "Background sync completed with some errors")
                    Result.success() // Still count as success
                }
                SyncManager.SYNC_NO_NETWORK -> {
                    Log.w(TAG, "Background sync failed: No network")
                    Result.retry() // Retry when network available
                }
                else -> {
                    Log.e(TAG, "Background sync failed")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Background sync error: ${e.message}", e)
            Result.retry()
        }
    }
}

