package com.appcenter.home.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * @author by Lawrence on 5/27/25.
 * for AppCenterHome
 */
object DefaultHomeUtil {
    private const val TAG = "DefaultHomeUtil"

    fun isMyAppDefaultLauncher(context: Context): Boolean {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.resolveActivity(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        val isDefault = resolveInfo?.activityInfo?.packageName == context.packageName
        Log.d(TAG, "Is MyApp Default Launcher? $isDefault (Current default: ${resolveInfo?.activityInfo?.packageName})")
        return isDefault
    }

    fun openHomeSettings(activity: android.app.Activity) {
        Log.d(TAG, "Opening home settings.")
        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
        // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_HOME_SETTINGS)
    }
}