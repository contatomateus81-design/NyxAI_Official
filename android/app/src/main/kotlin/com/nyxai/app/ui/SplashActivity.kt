package com.nyxai.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.nyxai.app.R

/**
 * Splash Screen inicial do Nyx AI
 * Exibe a logo e transita para o onboarding ou tela principal
 */
class SplashActivity : AppCompatActivity() {
    
    private val splashTimeOut: Long = 2500 // 2.5 segundos
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, splashTimeOut)
    }
    
    private fun navigateToNextScreen() {
        // Verificar se é a primeira vez que o app é aberto
        val sharedPreferences = getSharedPreferences("nyx_prefs", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)
        
        val intent = if (isFirstLaunch) {
            Intent(this, OnboardingActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
