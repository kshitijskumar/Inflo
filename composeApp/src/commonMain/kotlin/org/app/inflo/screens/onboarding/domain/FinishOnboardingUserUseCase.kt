package org.app.inflo.screens.onboarding.domain

import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository

class FinishOnboardingUserUseCase(
    private val repository: AppRepository,
    private val parseUserResponseToProfileUseCase: ParseUserResponseToProfileUseCase
) {
    
    suspend operator fun invoke(onboardedUser: OnboardedUser): OnboardingResultStatus {
        return try {
            // Call backend API to finish onboarding
            val response = repository.finishOnboarding(onboardedUser)

            when(val result = parseUserResponseToProfileUseCase.invoke(response)) {
                is ParsedResult.Error -> {
                    OnboardingResultStatus.InvalidResponse
                }
                is ParsedResult.Existing -> {
                    repository.storeUser(result.userAppModel)
                    OnboardingResultStatus.Complete(result.userAppModel)
                }
                is ParsedResult.New -> {
                    repository.storeOnboardedUser(result.onboardedAppModel)
                    OnboardingResultStatus.VerificationPending(result.onboardedAppModel)
                }
            }
        } catch (e: Exception) {
            OnboardingResultStatus.GeneralError
        }
    }
}

sealed class OnboardingResultStatus {
    data class Complete(val userAppModel: UserAppModel) : OnboardingResultStatus()
    data class VerificationPending(val onboardedUser: OnboardedUser) : OnboardingResultStatus()
    data object InvalidResponse : OnboardingResultStatus()
    data object GeneralError : OnboardingResultStatus()
}