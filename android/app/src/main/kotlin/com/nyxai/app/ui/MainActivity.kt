package com.nyxai.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nyxai.app.databinding.ActivityMainBinding
import com.nyxai.app.ui.ChatActivity

/**
 * Tela Principal (MainActivity) - Navegação simplificada
 * Foco no Chat como feature principal do MVP
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // No MVP, vamos direto para o Chat
        // Navegação inferior será implementada no v2
        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }
}
