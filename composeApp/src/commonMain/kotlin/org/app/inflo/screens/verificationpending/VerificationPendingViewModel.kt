package org.app.inflo.screens.verificationpending

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.core.viewmodel.AppBaseViewModel
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloNavOptions
import org.app.inflo.navigation.PopUpToConfig
import org.app.inflo.navigation.args.HomeArgs
import org.app.inflo.navigation.args.SplashArgs
import org.app.inflo.navigation.args.VerificationPendingArgs
import org.app.inflo.navigation.navigate
import org.app.inflo.screens.verificationpending.domain.UpdateUserVerificationStatusUseCase
import org.app.inflo.screens.verificationpending.domain.VerificationStatusUpdateResult

class VerificationPendingViewModel(
    private val args: VerificationPendingArgs,
    private val repository: AppRepository,
    private val navigationManager: InfloNavigationManager,
    private val updateUserVerificationStatusUseCase: UpdateUserVerificationStatusUseCase
) : AppBaseViewModel<VerificationPendingIntent, VerificationPendingState, VerificationPendingEffect>() {

    private var verificationJob: Job? = null

    init {
        registerForViewStateChanges()
        processIntent(VerificationPendingIntent.InitialisationIntent)
    }

    override fun initialViewState(): VerificationPendingState {
        return VerificationPendingState()
    }

    override fun processIntent(intent: VerificationPendingIntent) {
        super.processIntent(intent)
        when(intent) {
            VerificationPendingIntent.InitialisationIntent -> handleInitialisationIntent()
            VerificationPendingIntent.OnStartIntent -> handleOnStartIntent()
        }
    }

    private fun handleOnStartIntent() {
        val onboardedUser = viewState.value.onboardedUser
        if (onboardedUser != null) {
            checkVerificationStatus(onboardedUser)
        }
    }

    private fun handleInitialisationIntent() {
        viewModelScope.launch {
            // Check for stored onboarded user
            val onboardedUser = repository.onboardedUser().firstOrNull()
            
            if (onboardedUser == null) {
                // Clear stack and navigate back to splash screen as this is an invalid state
                navigationManager.navigate(
                    args = SplashArgs,
                    navOptions = InfloNavOptions(
                        popUpToConfig = PopUpToConfig.ClearAll()
                    )
                )
                return@launch
            }
            
            // Store it in view state
            updateState { currentState ->
                currentState.copy(onboardedUser = onboardedUser)
            }
            
            // Call checkVerificationStatus function
            checkVerificationStatus(onboardedUser)
        }
    }
    
    private fun checkVerificationStatus(onboardedUser: OnboardedUser) {
        if (verificationJob?.isActive == true) {
            return
        }
        verificationJob = viewModelScope.launch {
            updateState {
                it.copy(isLoading = true)
            }
            val result = updateUserVerificationStatusUseCase.invoke(onboardedUser)
            updateState {
                it.copy(isLoading = false)
            }
            when (result) {
                is VerificationStatusUpdateResult.Complete -> {
                    // User is now verified, navigate to home
                    navigationManager.navigate(
                        args = HomeArgs.resolve() ?: return@launch,
                        navOptions = InfloNavOptions(
                            popUpToConfig = PopUpToConfig.ClearAll()
                        )
                    )
                }
                is VerificationStatusUpdateResult.StillPending -> {
                    // User is still pending verification, update state
                    updateState { currentState ->
                        currentState.copy(onboardedUser = result.onboardedUser)
                    }
                }
                is VerificationStatusUpdateResult.InvalidResponse -> {
                    // Handle invalid response - could show error message
                    // For now, just log or handle silently
                }
                is VerificationStatusUpdateResult.GeneralError -> {
                    // Handle general error - could show error message
                    // For now, just log or handle silently
                }
            }
        }
    }

    override fun onViewStateActive() {
        processIntent(VerificationPendingIntent.OnStartIntent)
    }
}