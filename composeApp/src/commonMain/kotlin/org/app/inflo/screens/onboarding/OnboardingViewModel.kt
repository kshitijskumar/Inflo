package org.app.inflo.screens.onboarding

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlinx.coroutines.launch
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.viewmodel.AppBaseViewModel
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.PopUpToConfig
import org.app.inflo.navigation.InfloNavOptions
import org.app.inflo.navigation.args.HomeArgs
import org.app.inflo.navigation.navigate
import org.app.inflo.navigation.args.SplashArgs
import org.app.inflo.screens.onboarding.domain.GetOnboardingDetailsUseCase
import org.app.inflo.screens.onboarding.domain.FinishOnboardingUserUseCase
import org.app.inflo.utils.AppSystem

class OnboardingViewModel(
    private val getOnboardingDetailsUseCase: GetOnboardingDetailsUseCase,
    private val finishOnboardingUserUseCase: FinishOnboardingUserUseCase,
    private val repository: AppRepository,
    private val navigationManager: InfloNavigationManager
) : AppBaseViewModel<OnboardingIntent, OnboardingState, OnboardingEffect>() {

    init {
        processIntent(OnboardingIntent.InitialisationIntent)
    }

    override fun initialViewState() = OnboardingState()

    override fun processIntent(intent: OnboardingIntent) {
        super.processIntent(intent)
        when(intent) {
            OnboardingIntent.InitialisationIntent -> handleInitialisationIntent()
            OnboardingIntent.SubmitClickedIntent -> handleSubmitClickedIntent()
            OnboardingIntent.BackClickedIntent -> handleBackClickedIntent()
            is OnboardingIntent.FirstNameEnteredIntent -> handleFirstNameEnteredIntent(intent)
            is OnboardingIntent.LastNameEnteredIntent -> handleLastNameEnteredIntent(intent)
            is OnboardingIntent.DobEnteredIntent -> handleDobEnteredIntent(intent)
            is OnboardingIntent.BrandNameEnteredIntent -> handleBrandNameEnteredIntent(intent)
            is OnboardingIntent.InstagramAccountEnteredIntent -> handleInstagramAccountEnteredIntent(intent)
            is OnboardingIntent.CategoryClickedIntent -> handleCategoryClickedIntent(intent)
            OnboardingIntent.ShowDatePickerIntent -> handleShowDatePickerIntent()
            OnboardingIntent.HideDatePickerIntent -> handleHideDatePickerIntent()
        }
    }

    private fun handleInitialisationIntent() {
        viewModelScope.launch {
            // Get the onboarded user stored locally
            val onboardedUser = repository.onboardedUser().firstOrNull()

            if (onboardedUser == null) {
                // Clear stack and navigate to splash screen
                navigationManager.navigate(
                    args = SplashArgs,
                    navOptions = InfloNavOptions(
                        popUpToConfig = PopUpToConfig.ClearAll()
                    )
                )
                return@launch
            }

            // Get details required for user through the usecase
            val details = getOnboardingDetailsUseCase.invoke(onboardedUser)

            if (details.isEmpty()) {
                // Clear stack and navigate to splash screen
                navigationManager.navigate(
                    args = HomeArgs,
                    navOptions = InfloNavOptions(
                        popUpToConfig = PopUpToConfig.ClearAll()
                    )
                )
                return@launch
            }

            val currentDetails = details[0]
            // Update the state accordingly
            updateState { currentState ->
                currentState.copy(
                    onboardedUser = onboardedUser,
                    details = details,
                    currentDetailsIndex = 0,
                    shouldEnableConfirmBtn = currentDetails.isDetailsFilled(onboardedUser)
                )
            }
        }
    }

    private fun handleSubmitClickedIntent() {
        val currentState = viewState.value

        // If the details are not filled -> return coz submit function wont be enabled anyways
        if (!currentState.shouldEnableConfirmBtn) {
            return
        }

        val details = currentState.details ?: return
        val onboardedUser = currentState.onboardedUser ?: return
        val currentIndex = currentState.currentDetailsIndex

        // If the current details is last index of the list, we will make an api call
        if (currentIndex == details.lastIndex) {
            // Finish onboarding by calling the use case
            updateState {
                it.copy(
                    shouldShowLoading = true
                )
            }

            viewModelScope.launch {
                val result = finishOnboardingUserUseCase(onboardedUser)
                updateState {
                    it.copy(
                        shouldShowLoading = false
                    )
                }
                result.fold(
                    onSuccess = { userModel ->
                        // Onboarding completed successfully, navigate to home
                        navigationManager.navigate(
                            args = HomeArgs,
                            navOptions = InfloNavOptions(
                                popUpToConfig = PopUpToConfig.ClearAll()
                            )
                        )
                    },
                    onFailure = { exception ->
                        // Handle error - could show error message or retry
                        // For now, just log the error
                        println("Onboarding failed: ${exception.message}")
                    }
                )
            }
        } else {
            // If the current details is not the last index, update the current details with the next item
            val nextIndex = currentIndex + 1
            val nextDetails = details[nextIndex]

            updateState { state ->
                state.copy(
                    currentDetailsIndex = nextIndex,
                    shouldEnableConfirmBtn = nextDetails.isDetailsFilled(onboardedUser)
                )
            }
        }
    }

    private fun handleFirstNameEnteredIntent(intent: OnboardingIntent.FirstNameEnteredIntent) {
        val currentState = viewState.value
        val onboardedUser = currentState.onboardedUser ?: return

        val updatedUser = when (onboardedUser) {
            is OnboardedUser.Creator -> {
                // Update the first name for creator and set it back in the state
                onboardedUser.copy(firstName = intent.name)
            }
            is OnboardedUser.Brand -> {
                onboardedUser.copy(firstName = intent.name)
            }
        }

        val currentDetails = currentState.details?.getOrNull(currentState.currentDetailsIndex)
        updateState { state ->
            state.copy(
                onboardedUser = updatedUser,
                shouldEnableConfirmBtn = currentDetails?.isDetailsFilled(updatedUser) ?: state.shouldEnableConfirmBtn
            )
        }
    }

    private fun handleLastNameEnteredIntent(intent: OnboardingIntent.LastNameEnteredIntent) {
        val currentState = viewState.value
        val onboardedUser = currentState.onboardedUser ?: return

        val updatedUser = when (onboardedUser) {
            is OnboardedUser.Creator -> {
                // Update the last name for creator and set it back in the state
                onboardedUser.copy(lastName = intent.name)
            }
            is OnboardedUser.Brand -> {
                onboardedUser.copy(lastName = intent.name)
            }
        }

        val currentDetails = currentState.details?.getOrNull(currentState.currentDetailsIndex)
        updateState { state ->
            state.copy(
                onboardedUser = updatedUser,
                shouldEnableConfirmBtn = currentDetails?.isDetailsFilled(updatedUser) ?: state.shouldEnableConfirmBtn
            )
        }
    }

    private fun handleDobEnteredIntent(intent: OnboardingIntent.DobEnteredIntent) {
        val currentState = viewState.value
        val onboardedUser = currentState.onboardedUser ?: return

        val updatedUser = when (onboardedUser) {
            is OnboardedUser.Creator -> {
                // Update the DOB for creator and set it back in the state
                onboardedUser.copy(dob = intent.dobInMillis)
            }
            is OnboardedUser.Brand -> {
                onboardedUser
            }
        }

        val currentDetails = currentState.details?.getOrNull(currentState.currentDetailsIndex)
        updateState { state ->
            state.copy(
                onboardedUser = updatedUser,
                shouldEnableConfirmBtn = currentDetails?.isDetailsFilled(updatedUser) ?: state.shouldEnableConfirmBtn,
                showDatePicker = false,
                selectedDateForPicker = intent.dobInMillis
            )
        }
    }

    private fun handleBrandNameEnteredIntent(intent: OnboardingIntent.BrandNameEnteredIntent) {
        val currentState = viewState.value
        val onboardedUser = currentState.onboardedUser ?: return

        val updatedUser = when (onboardedUser) {
            is OnboardedUser.Creator -> {
                // Creator users don't have brand name, return as-is
                onboardedUser
            }
            is OnboardedUser.Brand -> {
                // Update the brand name for brand user
                onboardedUser.copy(brandName = intent.name)
            }
        }

        val currentDetails = currentState.details?.getOrNull(currentState.currentDetailsIndex)
        updateState { state ->
            state.copy(
                onboardedUser = updatedUser,
                shouldEnableConfirmBtn = currentDetails?.isDetailsFilled(updatedUser) ?: state.shouldEnableConfirmBtn
            )
        }
    }

    private fun handleInstagramAccountEnteredIntent(intent: OnboardingIntent.InstagramAccountEnteredIntent) {
        val currentState = viewState.value
        val onboardedUser = currentState.onboardedUser ?: return

        val updatedUser = when (onboardedUser) {
            is OnboardedUser.Creator -> {
                // Creator users don't have Instagram account in brand context, return as-is
                onboardedUser
            }
            is OnboardedUser.Brand -> {
                // Update the Instagram account for brand user
                onboardedUser.copy(brandInstagramAccountName = intent.accountName)
            }
        }

        val currentDetails = currentState.details?.getOrNull(currentState.currentDetailsIndex)
        updateState { state ->
            state.copy(
                onboardedUser = updatedUser,
                shouldEnableConfirmBtn = currentDetails?.isDetailsFilled(updatedUser) ?: state.shouldEnableConfirmBtn
            )
        }
    }

    private fun handleShowDatePickerIntent() {
        val currentState = viewState.value
        val onboardedUser = currentState.onboardedUser

        // Only show date picker for Creator users
        if (onboardedUser !is OnboardedUser.Creator) {
            return
        }

        // Get the currently selected date or use current date
        val selectedDate = onboardedUser.dob ?: AppSystem.currentTimeInMillis()

        updateState { state ->
            state.copy(
                showDatePicker = true,
                selectedDateForPicker = selectedDate
            )
        }
    }

    private fun handleHideDatePickerIntent() {
        updateState { state ->
            state.copy(
                showDatePicker = false
            )
        }
    }

    private fun handleBackClickedIntent() {
        val currentState = viewState.value
        val currentIndex = currentState.currentDetailsIndex

        // If there is a possible previous details, we go back
        when {
            currentIndex > 0 -> {
                val previousIndex = currentIndex - 1
                val details = currentState.details ?: return
                val previousDetails = details.getOrNull(previousIndex) ?: return
                val onboardedUser = currentState.onboardedUser ?: return

                updateState { state ->
                    state.copy(
                        currentDetailsIndex = previousIndex,
                        shouldEnableConfirmBtn = previousDetails.isDetailsFilled(onboardedUser)
                    )
                }
            }
            else -> {
                // If there is nothing to go back, it should not do anything
            }
        }
    }

    private fun handleCategoryClickedIntent(intent: OnboardingIntent.CategoryClickedIntent) {
        val currentState = viewState.value
        val onboardedUser = currentState.onboardedUser

        // Only handle category selection for Creator users
        if (onboardedUser !is OnboardedUser.Creator) {
            return
        }

        val currentCategories = onboardedUser.categories?.toMutableList() ?: mutableListOf()
        val clickedCategory = intent.category

        // Check if category is already selected
        val existingCategoryIndex = currentCategories.indexOfFirst { it.id == clickedCategory.id }

        if (existingCategoryIndex != -1) {
            // Category is already selected, deselect it (remove it)
            currentCategories.removeAt(existingCategoryIndex)
        } else {
            // Category is not selected, add it if we haven't reached max limit
            if (currentCategories.size < MAX_CATEGORY_SELECTION_ALLOWED) {
                currentCategories.add(clickedCategory)
            }
        }

        // Update the user with new categories
        val updatedUser = onboardedUser.copy(categories = currentCategories)

        val currentDetails = currentState.details?.getOrNull(currentState.currentDetailsIndex)
        updateState { state ->
            state.copy(
                onboardedUser = updatedUser,
                shouldEnableConfirmBtn = currentDetails?.isDetailsFilled(updatedUser) ?: state.shouldEnableConfirmBtn
            )
        }
    }

    companion object {
        const val MAX_CATEGORY_SELECTION_ALLOWED = 5
    }
}