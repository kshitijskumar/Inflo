package org.app.inflo.screens.splash

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.ProfileVerificationStatus
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.core.viewmodel.AppBaseViewModel
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloNavOptions
import org.app.inflo.navigation.PopUpToConfig
import org.app.inflo.navigation.args.HomeArgs
import org.app.inflo.navigation.args.LoginArgs
import org.app.inflo.navigation.args.OnboardingArgs
import org.app.inflo.navigation.args.VerificationPendingArgs
import org.app.inflo.navigation.navigate

class SplashViewModel(
    private val repository: AppRepository,
    private val navigationManager: InfloNavigationManager
) : AppBaseViewModel<SplashIntent, SplashState, SplashEffect>() {

    override fun initialViewState() = SplashState()

    override fun processIntent(intent: SplashIntent) {
        super.processIntent(intent)
        when(intent) {
            SplashIntent.InitialisationIntent -> handleInitialisationIntent()
            is SplashIntent.ProfileTypeSelectedIntent -> handleProfileTypeSelectedIntent(intent)
        }
    }

    private fun handleInitialisationIntent() = viewModelScope.launch {
        delay(SPLASH_DELAY)
        // First check if user is logged in
        val loggedInUser = repository.loggedInUser().firstOrNull()
        if (loggedInUser != null) {
            // Case 1: User is logged in - navigate to home screen
            navigationManager.navigate(
                args = HomeArgs,
                navOptions = InfloNavOptions(
                    popUpToConfig = PopUpToConfig.ClearAll()
                )
            )
            return@launch
        }
        
        // User is not logged in, check if they are onboarded
        val onboardedUser = repository.onboardedUser().firstOrNull()
        if (onboardedUser != null) {
            // Case 2: User is onboarded but not logged in - navigate to onboarding flow
            when(onboardedUser) {
                is OnboardedUser.Brand -> {
                    navigationManager.navigate(
                        args = OnboardingArgs,
                        navOptions = InfloNavOptions(
                            popUpToConfig = PopUpToConfig.ClearAll()
                        )
                    )
                }
                is OnboardedUser.Creator -> {
                    when(onboardedUser.verificationStatus) {
                        ProfileVerificationStatus.NOT_SUBMITTED -> {
                            navigationManager.navigate(
                                args = OnboardingArgs,
                                navOptions = InfloNavOptions(
                                    popUpToConfig = PopUpToConfig.ClearAll()
                                )
                            )
                        }
                        ProfileVerificationStatus.VERIFIED, // this is not supposed to happen, but if it does navigate to verification pending
                            // screen, it will handle accordingly
                        ProfileVerificationStatus.VERIFICATION_PENDING -> {
                            navigationManager.navigate(
                                args = VerificationPendingArgs,
                                navOptions = InfloNavOptions(
                                    popUpToConfig = PopUpToConfig.ClearAll()
                                )
                            )
                        }
                    }
                }
            }
            return@launch
        }
        
        // Case 3: Fresh user - start fresh user flow
        updateState {
            it.copy(
                screenType = SplashScreenType.PROFILE_TYPE_SELECTION
            )
        }
    }

    private fun handleProfileTypeSelectedIntent(intent: SplashIntent.ProfileTypeSelectedIntent) {
        navigationManager.navigate(
            args = LoginArgs(profileType = intent.type)
        )
    }

    companion object {
        private const val SPLASH_DELAY = 1500L
    }
}