package org.app.inflo.screens.verificationpending.domain

import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.screens.onboarding.domain.ParseUserResponseToProfileUseCase
import org.app.inflo.screens.onboarding.domain.ParsedResult

class UpdateUserVerificationStatusUseCase(
    private val repository: AppRepository,
    private val parseUserResponseToProfileUseCase: ParseUserResponseToProfileUseCase
) {
    
    suspend operator fun invoke(onboardedUser: OnboardedUser): VerificationStatusUpdateResult {
        return try {
            // Call backend API to update verification status
            val response = repository.updateUserVerificationStatus(onboardedUser)

            when(val result = parseUserResponseToProfileUseCase.invoke(response)) {
                is ParsedResult.Error -> {
                    VerificationStatusUpdateResult.InvalidResponse
                }
                is ParsedResult.Existing -> {
                    repository.storeUser(result.userAppModel)
                    VerificationStatusUpdateResult.Complete(result.userAppModel)
                }
                is ParsedResult.New -> {
                    repository.storeOnboardedUser(result.onboardedAppModel)
                    VerificationStatusUpdateResult.StillPending(result.onboardedAppModel)
                }
            }
        } catch (e: Exception) {
            VerificationStatusUpdateResult.GeneralError
        }
    }
}

sealed class VerificationStatusUpdateResult {
    data class Complete(val userAppModel: UserAppModel) : VerificationStatusUpdateResult()
    data class StillPending(val onboardedUser: OnboardedUser) : VerificationStatusUpdateResult()
    data object InvalidResponse : VerificationStatusUpdateResult()
    data object GeneralError : VerificationStatusUpdateResult()
} 