package com.nyxai.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NyxApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize app-wide components
        // - Database
        // - Preferences
        // - Network client
    }
}
