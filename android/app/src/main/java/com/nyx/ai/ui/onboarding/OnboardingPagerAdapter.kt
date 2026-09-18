package com.nyx.ai.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val steps: List<OnboardingActivity.StepData>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = steps.size

    override fun createFragment(position: Int): Fragment {
        val step = steps[position]
        return OnboardingStepFragment.newInstance(step.title, step.description, step.imageRes)
    }
}
