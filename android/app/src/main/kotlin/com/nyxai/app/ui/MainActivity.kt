package com.nyxai.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nyxai.app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Tela Principal (MainActivity) - Navegação entre Chat, Automação e Configurações
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        // Carregar tela inicial (Chat)
        if (savedInstanceState == null) {
            loadFragment(ChatFragment())
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.nyxai.app.R.id.nav_chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                com.nyxai.app.R.id.nav_automation -> {
                    loadFragment(AutomationFragment())
                    true
                }
                com.nyxai.app.R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(com.nyxai.app.R.id.fragment_container, fragment)
            .commit()
    }
}
