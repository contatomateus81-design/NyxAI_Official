package com.nyxai.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nyxai.app.databinding.FragmentOnboardingBinding

/**
 * Adapter para o ViewPager2 do Onboarding
 */
class OnboardingAdapter(
    activity: AppCompatActivity,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(activity) {
    
    override fun getItemCount(): Int = pages.size
    
    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.newInstance(pages[position])
    }
}
