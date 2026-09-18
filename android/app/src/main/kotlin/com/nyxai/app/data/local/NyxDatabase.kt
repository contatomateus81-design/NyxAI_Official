package com.nyxai.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nyxai.app.data.model.Message
import com.nyxai.app.data.model.Automation
import com.nyxai.app.data.model.User

@Database(
    entities = [Message::class, Automation::class, User::class],
    version = 1,
    exportSchema = false
)
abstract class NyxDatabase : RoomDatabase() {
    
    abstract fun messageDao(): MessageDao
    abstract fun automationDao(): AutomationDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: NyxDatabase? = null
        
        fun getDatabase(context: Context): NyxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NyxDatabase::class.java,
                    "nyx_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
