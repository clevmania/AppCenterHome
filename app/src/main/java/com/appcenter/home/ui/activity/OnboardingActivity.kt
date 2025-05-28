package com.appcenter.home.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.appcenter.home.ui.OnboardingStepListener
import com.appcenter.home.viewmodel.OnboardingViewModel
import com.appcenter.home.R
import com.appcenter.home.databinding.ActivityOnboardingBinding
import com.appcenter.home.utils.Constants
import com.appcenter.home.utils.DefaultHomeUtil
import com.appcenter.home.utils.OnboardingPrefsManager

class OnboardingActivity : AppCompatActivity(), OnboardingStepListener {

    private lateinit var binding: ActivityOnboardingBinding

    private val viewModel: OnboardingViewModel by viewModels()
    private lateinit var navController: NavController

    private lateinit var prefsManager: OnboardingPrefsManager

    companion object {
        private const val TAG = "OnboardingActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsManager = OnboardingPrefsManager(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Observe ViewModel's current step to drive navigation
        // This handles initial state, restoration, and navigation changes
        viewModel.currentStep.observe(this) { step ->
            Log.i(TAG, "Observer: ViewModel currentStep is $step. NavController current: ${navController.currentDestination?.label} (id: ${navController.currentDestination?.id})")
            val currentNavId = navController.currentDestination?.id
            val targetNavId = stepToDestinationId(step)

            if (currentNavId == targetNavId) {
                Log.v(TAG, "Observer: Already at target destination for step $step.")
                return@observe
            }

            Log.v(TAG, "Observer: Navigating for step $step. CurrentNavId: $currentNavId, TargetNavId: $targetNavId")

            try {
                when (step) {
                    1 -> { // Target is Step 1 (FirstFragment)
                        if (currentNavId != R.id.FirstFragment) {
                            Log.v(TAG, "Observer: Navigating to WelcomeFragment. Clearing backstack.")
                            navController.navigate(
                                R.id.FirstFragment, null, NavOptions.Builder()
                                .setPopUpTo(navController.graph.id, true) // Pop entire back stack of this graph
                                .build())
                        }
                    }
                    2 -> { // Target is Step 2 (SecondFragment)
                        if (currentNavId == R.id.FirstFragment) {
                            Log.v(TAG, "Observer: Navigating from FirstFragment to SecondFragment.")
                            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                        }
                    }
                    3 -> { // Target is Step 3 (ThankYouFragment)
                        if (currentNavId == R.id.SecondFragment) {
                            Log.v(TAG, "Observer: Navigating from SecondFragment to ThankYouFragment.")
                            navController.navigate(R.id.action_SecondFragment_to_thankYouFragment)
                        }
                    }
                    else -> Log.e(TAG, "Observer: Unknown step $step, cannot navigate.")
                }
            } catch (e: Exception) {
                // Catch potential navigation errors (e.g., destination not found, graph issues)
                Log.e(TAG, "Observer: Error during navigation for step $step.", e)
            }
        }

        // If redirected from HomeActivity after setting default
        if (intent.getBooleanExtra(Constants.EXTRA_REDIRECTED_FROM_HOME, false)) {
            Log.v(TAG, "Redirected from HomeActivity.")
            intent.removeExtra(Constants.EXTRA_REDIRECTED_FROM_HOME) // Consume the extra
            handleHomeRedirection()
        }

        // Prevent going back during onboarding
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.v(TAG, "Back press ignored during onboarding.")
            }
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.v(TAG, "onNewIntent. Intent: $intent")
        setIntent(intent)
        if (intent.getBooleanExtra(Constants.EXTRA_REDIRECTED_FROM_HOME, false)) {
            Log.v(TAG, "onNewIntent: Detected EXTRA_REDIRECTED_FROM_HOME.")
            intent.removeExtra(Constants.EXTRA_REDIRECTED_FROM_HOME)
            handleHomeRedirection()
        }
    }

    private fun stepToDestinationId(step: Int): Int {
        return when (step) {
            1 -> R.id.FirstFragment
            2 -> R.id.SecondFragment
            3 -> R.id.thankYouFragment
            else -> {
                Log.w(TAG, "stepToDestinationId: Unknown step $step, defaulting to FirstFragment")
                0
            }
        }
    }

    private fun handleHomeRedirection() {
        Log.v(TAG, "handleHomeRedirection: Checking if app is default.")
        if (DefaultHomeUtil.isMyAppDefaultLauncher(this) && viewModel.currentStep.value == 2) {
            Log.v(TAG, "App is now default. Moving to step 3 from home redirection.")
            viewModel.moveToStep(3)
        } else {
            Log.v(TAG, "App not default OR not on step 2 after home redirection. Current step: ${viewModel.currentStep.value}")
        }
    }

    override fun onNextStep() {
        Log.v(TAG, "onNextStep called (from WelcomeFragment). Requesting move to step 2.")
        if (viewModel.currentStep.value == 1) {
            viewModel.moveToStep(2)
        } else {
            Log.w(TAG, "onNextStep called but not on step 1. Current step: ${viewModel.currentStep.value}")
        }
    }

    override fun onSetDefaultClicked() {
        Log.v(TAG, "onSetDefaultClicked called.")
        viewModel.setAwaitingHomeSettingsResult(true)
        prefsManager.isPendingHomeSetCheck = true 
        DefaultHomeUtil.openHomeSettings(this)
    }

    override fun onOnboardingFinished() {
        Log.v(TAG, "onOnboardingFinished called.")
        prefsManager.isOnboardingComplete = true
        prefsManager.isPendingHomeSetCheck = false 

        // Optionally, launch HomeActivity explicitly if not already default,
        // but system should handle launching default home.
        // val homeIntent = Intent(this, HomeActivity::class.java)
        // homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        // startActivity(homeIntent)
        Log.v(TAG, "Finishing OnboardingActivity.")
        finish()
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume. Current step: ${viewModel.currentStep.value}, Awaiting result: ${viewModel.isAwaitingHomeSettingsResult.value}")
        // Handle edge case: User sets default externally while app is in background on Step 2
        if (viewModel.currentStep.value == 2 &&
            viewModel.isAwaitingHomeSettingsResult.value == false &&
            DefaultHomeUtil.isMyAppDefaultLauncher(this)
        ) {
            Log.v(TAG, "onResume: Detected app became default externally while on step 2. Moving to step 3.")
            prefsManager.isPendingHomeSetCheck = false
            viewModel.moveToStep(3)
        }
    }

    @Deprecated("Deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(TAG, "onActivityResult. requestCode: $requestCode, resultCode: $resultCode")
        if (requestCode == Constants.REQUEST_CODE_HOME_SETTINGS) {
            Log.i(TAG, "onActivityResult: Returning from Home Settings.")
            handleHomeRedirection()
            viewModel.setAwaitingHomeSettingsResult(false)
            // If HomeActivity redirected, it would clear this. If OnboardingActivity is directly resumed,
            // clear it here.
            prefsManager.isPendingHomeSetCheck = false

            if (DefaultHomeUtil.isMyAppDefaultLauncher(this)) {
                Log.v(TAG, "Returned from Home Settings, app IS default. Requesting move to step 3.")
                viewModel.moveToStep(3)
            } else {
                Log.v(TAG, "Returned from Home Settings, app IS NOT default. Staying on step 2.")
            }
        }
    }
}