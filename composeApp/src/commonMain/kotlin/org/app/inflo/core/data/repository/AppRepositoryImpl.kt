package org.app.inflo.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.app.inflo.core.data.local.AppLocalDataSource
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel

class AppRepositoryImpl(
    private val localDataSource: AppLocalDataSource
) : AppRepository {

    override fun loggedInUser(): Flow<UserAppModel?> {
        return localDataSource.storedUser()
    }

    /**
     * Onboarded user means, they successfully logged in, but they did not completed the onboarding
     * process properly, hence continue their onboarding flow
     */
    override fun onboardedUser(): Flow<OnboardedUser?> {
        return localDataSource.onboardedUser()
    }
}