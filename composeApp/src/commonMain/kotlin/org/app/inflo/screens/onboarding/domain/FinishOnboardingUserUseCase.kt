package org.app.inflo.screens.onboarding.domain

import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository

class FinishOnboardingUserUseCase(
    private val repository: AppRepository
) {
    
    suspend operator fun invoke(onboardedUser: OnboardedUser): Result<UserAppModel> {
        return try {
            // Call backend API to finish onboarding
            val userModel = repository.finishOnboarding(onboardedUser)
            
            // Store the logged-in user
            repository.storeUser(userModel)
            
            Result.success(userModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 