package org.app.inflo.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.app.inflo.core.data.local.AppLocalDataSource
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.remote.AppRemoteDataSource
import org.app.inflo.screens.login.domain.RequestOtpRequestApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseAppModel
import org.app.inflo.screens.login.domain.VerifyLoginRequestApiModel
import org.app.inflo.screens.login.domain.VerifyLoginResponseApiModel
import org.app.inflo.screens.login.domain.toAppModelOrNullIfInvalid
import org.app.inflo.screens.login.exception.RequestOtpFailedException

class AppRepositoryImpl(
    private val localDataSource: AppLocalDataSource,
    private val remoteDataSource: AppRemoteDataSource
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
    
    override suspend fun requestOtp(request: RequestOtpRequestApiModel): RequestOtpResponseApiModel {
        return remoteDataSource.requestOtp(request)
    }
    
    override suspend fun requestOtpAppModel(request: RequestOtpRequestApiModel): RequestOtpResponseAppModel {
        return remoteDataSource.requestOtp(request).toAppModelOrNullIfInvalid()
            ?: throw RequestOtpFailedException("OTP code is null in response")
    }
    
    override suspend fun verifyLogin(request: VerifyLoginRequestApiModel): VerifyLoginResponseApiModel {
        return remoteDataSource.verifyLogin(request)
    }
    
    override suspend fun storeUser(user: UserAppModel) {
        localDataSource.storeUser(user)
    }
    
    override suspend fun storeOnboardedUser(onboardedUser: OnboardedUser) {
        localDataSource.storeOnboardedUser(onboardedUser)
    }
    
    override suspend fun clearAllUserData() {
        localDataSource.clearAllUserData()
    }
    
    override suspend fun finishOnboarding(onboardedUser: OnboardedUser): VerifyLoginResponseApiModel {
        // Call backend API to finish onboarding
        val userModel = remoteDataSource.finishOnboarding(onboardedUser)
        return userModel
    }
    
    override suspend fun updateUserVerificationStatus(onboardedUser: OnboardedUser): VerifyLoginResponseApiModel {
        // Call backend API to update verification status
        val userModel = remoteDataSource.updateUserVerificationStatus(onboardedUser)
        return userModel
    }
}