package com.appcenter.home.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit

/**
 * @author by Lawrence on 5/27/25.
 * for AppCenterHome
 */
class OnboardingPrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "PrefsManager"
    }

    var isOnboardingComplete: Boolean
        get() {
            val complete = prefs.getBoolean(Constants.KEY_ONBOARDING_COMPLETE, false)
            Log.d(TAG, "isOnboardingComplete GET: $complete")
            return complete
        }
        set(value) {
            Log.d(TAG, "isOnboardingComplete SET: $value")
            prefs.edit {
                putBoolean(Constants.KEY_ONBOARDING_COMPLETE, value).apply()
            }
        }

    var isPendingHomeSetCheck: Boolean // For HomeActivity to know if it should redirect
        get() {
            val pending = prefs.getBoolean(Constants.KEY_PENDING_HOME_SET_CHECK, false)
            Log.d(TAG, "isPendingHomeSetCheck GET: $pending")
            return pending
        }
        set(value) {
            Log.d(TAG, "isPendingHomeSetCheck SET: $value")
            prefs.edit {
                putBoolean(Constants.KEY_PENDING_HOME_SET_CHECK, value).apply()
            }
        }
}