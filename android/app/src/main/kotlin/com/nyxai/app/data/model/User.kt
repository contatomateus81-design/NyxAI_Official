package com.nyxai.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSyncAt: Long? = null,
    val preferences: UserPreferences? = null
)

data class UserPreferences(
    val theme: String = "dark",
    val notificationsEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val language: String = "pt-BR"
)
