package com.nyx.ai.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nyx.ai.databinding.FragmentOnboardingStepBinding

class OnboardingStepFragment : Fragment() {

    private var _binding: FragmentOnboardingStepBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_IMAGE_RES = "image_res"

        fun newInstance(title: String, description: String, imageRes: Int): OnboardingStepFragment {
            return OnboardingStepFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_DESCRIPTION, description)
                    putInt(ARG_IMAGE_RES, imageRes)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        arguments?.let {
            binding.titleText.text = it.getString(ARG_TITLE, "")
            binding.descriptionText.text = it.getString(ARG_DESCRIPTION, "")
            binding.stepImage.setImageResource(it.getInt(ARG_IMAGE_RES, 0))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
