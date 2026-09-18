package com.nyx.ai.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nyx.ai.R
import com.nyx.ai.databinding.ActivityOnboardingBinding
import com.nyx.ai.manager.ApiKeyManager
import com.nyx.ai.ui.chat.ChatActivity
import com.nyx.ai.ui.setup.ApiSetupActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingPagerAdapter
    private lateinit var apiKeyManager: ApiKeyManager

    private val steps = listOf(
        StepData(
            "Bem-vindo à Nyx AI",
            "Sua assistente pessoal inteligente que combina conversação natural, automação e privacidade total.",
            R.drawable.ic_onboarding_welcome
        ),
        StepData(
            "Conversação Natural",
            "Converse por texto ou voz com a Nyx. Ela entende contexto e mantém histórico localmente.",
            R.drawable.ic_onboarding_chat
        ),
        StepData(
            "Automação Avançada",
            "Crie gatilhos inteligentes estilo Tasker. A Nyx automatiza tarefas no seu dispositivo.",
            R.drawable.ic_onboarding_automation
        ),
        StepData(
            "Privacidade Total",
            "Seus dados ficam apenas no seu dispositivo. Sem nuvem, sem anúncios, sem rastreamento.",
            R.drawable.ic_onboarding_privacy
        ),
        StepData(
            "Configuração da API",
            "Para funcionar, a Nyx precisa de chaves da Groq Cloud. Recomendamos mínimo de 10 chaves para evitar travamentos.",
            R.drawable.ic_onboarding_setup
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiKeyManager = ApiKeyManager.getInstance(this)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        adapter = OnboardingPagerAdapter(this, steps)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                
                if (position == steps.size - 1) {
                    binding.btnNext.text = "Configurar Chaves"
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnNext.text = "Próximo"
                    binding.btnSkip.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < steps.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                // Ir para tela de configuração de chaves
                startActivity(Intent(this, ApiSetupActivity::class.java))
                finish()
            }
        }

        binding.btnSkip.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < steps.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            }
        }

        binding.btnLater.setOnClickListener {
            // Usuário pula configuração (pode configurar depois nas settings)
            Toast.makeText(
                this,
                "Você pode configurar as chaves depois em Configurações",
                Toast.LENGTH_LONG
            ).show()
            proceedToChat()
        }
    }

    private fun proceedToChat() {
        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }

    data class StepData(
        val title: String,
        val description: String,
        val imageRes: Int
    )
}
