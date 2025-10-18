package com.example.perfectfit.database

import androidx.room.*
import com.example.perfectfit.models.WorkloadConfig

@Dao
interface WorkloadConfigDao {
    
    @Query("SELECT * FROM workload_config LIMIT 1")
    suspend fun getConfig(): WorkloadConfig?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: WorkloadConfig): Long
    
    @Update
    suspend fun updateConfig(config: WorkloadConfig)
    
    @Query("DELETE FROM workload_config")
    suspend fun deleteAllConfigs()
    
    // Helper method to get or create default config
    suspend fun getOrCreateConfig(): WorkloadConfig {
        return getConfig() ?: WorkloadConfig().also {
            insertConfig(it)
        }
    }
}

