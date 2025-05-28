package com.appcenter.home.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.appcenter.home.R
import com.appcenter.home.utils.Constants
import com.appcenter.home.utils.OnboardingPrefsManager

class HomeActivity : AppCompatActivity() {
    private lateinit var prefsManager: OnboardingPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.v(TAG, "onCreate. Intent: $intent")
        prefsManager = OnboardingPrefsManager(this)

        handlePendingHomeSetCheck()

        // If we reach here, it's either normal launch or onboarding is complete.
        Log.v(TAG, "Setting content view for HomeActivity.")
        setContentView(R.layout.activity_home)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.v(TAG, "onNewIntent. Intent: $intent")
        handlePendingHomeSetCheck()
    }

    private fun handlePendingHomeSetCheck() {
        if (prefsManager.isPendingHomeSetCheck && !prefsManager.isOnboardingComplete) {
            Log.v(TAG, "HomeActivity launched while pending home set check during onboarding.")
            prefsManager.isPendingHomeSetCheck = false // Consume the flag

            val intent = Intent(this, OnboardingActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra(Constants.EXTRA_REDIRECTED_FROM_HOME, true)
            }
            Log.v(TAG, "Redirecting to OnboardingActivity.")
            startActivity(intent)
            finish()
            return
        }
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}