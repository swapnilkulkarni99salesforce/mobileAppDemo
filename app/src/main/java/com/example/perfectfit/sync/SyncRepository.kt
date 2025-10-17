package com.example.perfectfit.sync

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for sync operations - provides a clean interface for UI layer
 */
class SyncRepository(private val context: Context) {
    
    private val syncManager = SyncManager(context)
    
    private val _syncStatus = MutableLiveData<SyncStatus>()
    val syncStatus: LiveData<SyncStatus> = _syncStatus
    
    /**
     * Sync status data class
     */
    data class SyncStatus(
        val isSyncing: Boolean = false,
        val lastSyncTime: Long = 0L,
        val lastSyncSuccess: Boolean = true,
        val message: String = ""
    )
    
    /**
     * Trigger manual sync
     */
    suspend fun sync() = withContext(Dispatchers.IO) {
        _syncStatus.postValue(SyncStatus(isSyncing = true, message = "Syncing..."))
        
        try {
            val result = syncManager.syncAll()
            
            val status = when (result) {
                SyncManager.SYNC_SUCCESS -> {
                    SyncStatus(
                        isSyncing = false,
                        lastSyncTime = System.currentTimeMillis(),
                        lastSyncSuccess = true,
                        message = "Sync completed successfully"
                    )
                }
                SyncManager.SYNC_PARTIAL -> {
                    SyncStatus(
                        isSyncing = false,
                        lastSyncTime = System.currentTimeMillis(),
                        lastSyncSuccess = true,
                        message = "Sync completed with some warnings"
                    )
                }
                SyncManager.SYNC_NO_NETWORK -> {
                    SyncStatus(
                        isSyncing = false,
                        lastSyncTime = syncManager.getLastSyncTimestamp(),
                        lastSyncSuccess = false,
                        message = "No network connection"
                    )
                }
                else -> {
                    SyncStatus(
                        isSyncing = false,
                        lastSyncTime = syncManager.getLastSyncTimestamp(),
                        lastSyncSuccess = false,
                        message = "Sync failed. Please try again."
                    )
                }
            }
            
            _syncStatus.postValue(status)
            result == SyncManager.SYNC_SUCCESS || result == SyncManager.SYNC_PARTIAL
            
        } catch (e: Exception) {
            _syncStatus.postValue(
                SyncStatus(
                    isSyncing = false,
                    lastSyncTime = syncManager.getLastSyncTimestamp(),
                    lastSyncSuccess = false,
                    message = "Error: ${e.message}"
                )
            )
            false
        }
    }
    
    /**
     * Get last sync timestamp
     */
    fun getLastSyncTimestamp(): Long {
        return syncManager.getLastSyncTimestamp()
    }
    
    /**
     * Get formatted last sync time
     */
    fun getLastSyncTimeFormatted(): String {
        val lastSync = syncManager.getLastSyncTimestamp()
        
        if (lastSync == 0L) {
            return "Never synced"
        }
        
        val diff = System.currentTimeMillis() - lastSync
        val minutes = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 * 24)
        
        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
            days < 7 -> "$days day${if (days != 1L) "s" else ""} ago"
            else -> {
                val date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(lastSync))
                "On $date"
            }
        }
    }
    
    /**
     * Enable background sync
     */
    fun enableBackgroundSync() {
        SyncWorker.schedulePeriodicSync(context)
    }
    
    /**
     * Disable background sync
     */
    fun disableBackgroundSync() {
        SyncWorker.cancelSync(context)
    }
    
    /**
     * Trigger immediate background sync
     */
    fun syncInBackground() {
        SyncWorker.scheduleImmediateSync(context)
    }
}

