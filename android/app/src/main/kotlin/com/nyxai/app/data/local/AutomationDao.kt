package com.nyxai.app.data.local

import androidx.room.*
import com.nyxai.app.data.model.Automation
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationDao {
    
    @Query("SELECT * FROM automations ORDER BY createdAt DESC")
    fun getAllAutomations(): Flow<List<Automation>>
    
    @Query("SELECT * FROM automations WHERE isEnabled = 1")
    fun getEnabledAutomations(): Flow<List<Automation>>
    
    @Query("SELECT * FROM automations WHERE id = :id")
    suspend fun getAutomationById(id: Long): Automation?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomation(automation: Automation): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomations(automations: List<Automation>)
    
    @Update
    suspend fun updateAutomation(automation: Automation)
    
    @Delete
    suspend fun deleteAutomation(automation: Automation)
    
    @Query("UPDATE automations SET lastTriggered = :timestamp WHERE id = :id")
    suspend fun updateLastTriggered(id: Long, timestamp: Long)
    
    @Query("UPDATE automations SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleAutomation(id: Long, enabled: Boolean)
    
    @Query("DELETE FROM automations")
    suspend fun deleteAllAutomations()
}
