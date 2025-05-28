package com.appcenter.home.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appcenter.home.R
import com.appcenter.home.utils.DefaultHomeUtil
import com.appcenter.home.utils.OnboardingPrefsManager

class SplashActivity : AppCompatActivity() {
    private lateinit var prefsManager: OnboardingPrefsManager

    companion object {
        private const val TAG = "LauncherActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.v(TAG, "onCreate")

        prefsManager = OnboardingPrefsManager(this)

        // Use a small delay to show splash
        // Not the ideal way to display a splash, we're keeping things simple
        Handler(Looper.getMainLooper()).postDelayed({
            decideNextActivity()
        }, 500)
    }

    private fun decideNextActivity() {
        val isDefault = DefaultHomeUtil.isMyAppDefaultLauncher(this)
        val onboardingComplete = prefsManager.isOnboardingComplete

        Log.v(TAG, "Decision: isDefault=$isDefault, onboardingComplete=$onboardingComplete")

        if (isDefault && !onboardingComplete) {
            // Edge case: App is default, but onboarding never finished
            // As per requirement: "finish onboarding"
            Log.v(TAG, "App is default but onboarding not marked complete. Marking complete and launching Home.")
            prefsManager.isOnboardingComplete = true
            launchHomeActivity()
        } else if (!onboardingComplete) {
            Log.v(TAG, "Onboarding not complete. Launching OnboardingActivity.")
            launchOnboardingActivity()
        } else {
            // Onboarding complete and if it's default, system handles it
            // if not, well, it's just an app
            Log.v(TAG, "Onboarding complete. Launching HomeActivity.")
            launchHomeActivity()
        }
        finish() // Finish SplashActivity so it's not in back stack
    }

    private fun launchHomeActivity() {
        Log.v(TAG, "launchHomeActivity")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun launchOnboardingActivity() {
        Log.v(TAG, "launchOnboardingActivity")
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

}