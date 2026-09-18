package com.nyxai.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val content: String,
    val sender: SenderType, // USER, AI, SYSTEM
    val type: MessageType = MessageType.TEXT,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val metadata: String? = null // JSON for additional data
)

enum class MessageType {
    TEXT,
    VOICE,
    IMAGE,
    AUTOMATION_TRIGGER
}
