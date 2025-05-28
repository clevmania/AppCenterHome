package com.appcenter.home.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appcenter.home.ui.OnboardingStepListener
import com.appcenter.home.databinding.FragmentSetDefaultBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SetDefaultFragment : Fragment() {

    private var _binding: FragmentSetDefaultBinding? = null
    private var listener: OnboardingStepListener? = null
    
    companion object {
        private const val TAG = "SetDefaultFragment"
    }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetDefaultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSetDefault.setOnClickListener {
            Log.v(TAG, "Set Default button clicked")
            listener?.onSetDefaultClicked()
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