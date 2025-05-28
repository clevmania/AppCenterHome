package com.appcenter.home.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appcenter.home.ui.OnboardingStepListener
import com.appcenter.home.databinding.FragmentWelcomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private var listener: OnboardingStepListener? = null

    companion object {
        private const val TAG = "WelcomeFragment"
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.setOnClickListener {
            listener?.onNextStep()
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