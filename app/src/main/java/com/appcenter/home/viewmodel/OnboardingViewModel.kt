package com.appcenter.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * @author by Lawrence on 5/27/25.
 * for AppCenterHome
 */

class OnboardingViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val TAG = "OnboardingViewModel"
        private const val KEY_CURRENT_STEP = "current_step"
    }

    private val _currentStep = savedStateHandle.getLiveData(KEY_CURRENT_STEP, 1)
    val currentStep: LiveData<Int> = _currentStep

    private val _isAwaitingHomeSettingsResult = MutableLiveData(false)
    val isAwaitingHomeSettingsResult: LiveData<Boolean> = _isAwaitingHomeSettingsResult

    fun moveToStep(step: Int) {
        Log.v(TAG, "Moving to step: $step")
        savedStateHandle[KEY_CURRENT_STEP] = step
    }

    fun setAwaitingHomeSettingsResult(isAwaiting: Boolean) {
        Log.v(TAG, "setAwaitingHomeSettingsResult: $isAwaiting")
        _isAwaitingHomeSettingsResult.value = isAwaiting
    }
}