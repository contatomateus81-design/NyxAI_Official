package com.nyxai.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automations")
data class Automation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val trigger: TriggerConfig,
    val actions: List<ActionConfig>,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastTriggered: Long? = null
)

data class TriggerConfig(
    val type: TriggerType,
    val config: Map<String, Any> // Flexible configuration based on trigger type
)

enum class TriggerType {
    TIME_BASED,       // Specific time/date
    LOCATION_BASED,   // GPS location
    APP_EVENT,        // App opened/closed
    NOTIFICATION,     // Notification received
    CONNECTIVITY,     // WiFi/Bluetooth connect/disconnect
    MANUAL            // Manual trigger
}

data class ActionConfig(
    val type: ActionType,
    val config: Map<String, Any> // Flexible configuration based on action type
)

enum class ActionType {
    SEND_MESSAGE,     // Send WhatsApp/SMS
    CREATE_CALENDAR_EVENT,
    SET_ALARM,
    CHANGE_SETTING,   // WiFi, Bluetooth, brightness, etc.
    LAUNCH_APP,
    PLAY_MEDIA,
    SPEAK_TEXT,       // Text-to-speech
    RUN_SCRIPT        // Custom script
}
