package com.appcenter.home.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appcenter.home.ui.OnboardingStepListener
import com.appcenter.home.databinding.FragmentThankYouBinding

class ThankYouFragment : Fragment() {
    private var listener: OnboardingStepListener? = null
    
    companion object {
        private const val TAG = "ThankYouFragment"
    }

    private var _binding: FragmentThankYouBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentThankYouBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFinishOnboarding.setOnClickListener {
            Log.v(TAG, "Finish Onboarding button clicked")
            listener?.onOnboardingFinished()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v(TAG, "onAttach")
        if (context is OnboardingStepListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnboardingStepListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.v(TAG, "onDetach")
        listener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}