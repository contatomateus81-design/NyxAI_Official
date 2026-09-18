package com.nyxai.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nyxai.app.databinding.ActivityOnboardingBinding

/**
 * Tela de Onboarding - Apresentação gradual das capacidades do app
 */
class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOnboardingBinding
    
    private val onboardingPages = listOf(
        OnboardingPage(
            R.string.onboarding_1_title,
            R.string.onboarding_1_description,
            R.drawable.ic_chat
        ),
        OnboardingPage(
            R.string.onboarding_2_title,
            R.string.onboarding_2_description,
            R.drawable.ic_calendar
        ),
        OnboardingPage(
            R.string.onboarding_3_title,
            R.string.onboarding_3_description,
            R.drawable.ic_privacy
        ),
        OnboardingPage(
            R.string.onboarding_4_title,
            R.string.onboarding_4_description,
            R.drawable.ic_automation
        )
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViewPager()
        setupButtons()
    }
    
    private fun setupViewPager() {
        val adapter = OnboardingAdapter(this, onboardingPages)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()
    }
    
    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < onboardingPages.size - 1) {
                binding.viewPager.currentItem += 1
            } else {
                completeOnboarding()
            }
        }
        
        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }
    }
    
    private fun completeOnboarding() {
        val sharedPreferences = getSharedPreferences("nyx_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
        
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

data class OnboardingPage(
    val titleResId: Int,
    val descriptionResId: Int,
    val iconResId: Int
)
