package com.nyxai.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val sender: SenderType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val metadata: String? = null // JSON for additional data
)

enum class SenderType {
    USER,
    AI,
    SYSTEM
}
