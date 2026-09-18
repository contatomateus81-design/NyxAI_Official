package com.nyxai.app

import android.app.Application
import com.nyxai.app.network.NyxApiClient

class NyxApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializa o contexto no NyxApiClient
        NyxApiClient.appContext = this
        
        // Initialize app-wide components
        // - Database
        // - Preferences
        // - Network client
    }
}
