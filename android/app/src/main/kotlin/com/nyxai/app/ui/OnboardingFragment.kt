package com.nyxai.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nyxai.app.databinding.FragmentOnboardingBinding

/**
 * Fragment para cada página do Onboarding
 */
class OnboardingFragment : Fragment() {
    
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_ICON = "icon"
        
        fun newInstance(page: OnboardingPage): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putInt(ARG_TITLE, page.titleResId)
            args.putInt(ARG_DESCRIPTION, page.descriptionResId)
            args.putInt(ARG_ICON, page.iconResId)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val titleResId = arguments?.getInt(ARG_TITLE) ?: 0
        val descriptionResId = arguments?.getInt(ARG_DESCRIPTION) ?: 0
        val iconResId = arguments?.getInt(ARG_ICON) ?: 0
        
        binding.textViewTitle.setText(titleResId)
        binding.textViewDescription.setText(descriptionResId)
        binding.imageViewIcon.setImageResource(iconResId)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
