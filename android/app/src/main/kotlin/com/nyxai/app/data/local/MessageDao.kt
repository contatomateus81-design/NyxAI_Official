package com.nyxai.app.data.local

import androidx.room.*
import com.nyxai.app.data.model.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>
    
    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int = 50): List<Message>
    
    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessageById(id: Long): Message?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    @Update
    suspend fun updateMessage(message: Message)
    
    @Delete
    suspend fun deleteMessage(message: Message)
    
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
    
    @Query("SELECT COUNT(*) FROM messages")
    fun getMessageCount(): Flow<Int>
}
