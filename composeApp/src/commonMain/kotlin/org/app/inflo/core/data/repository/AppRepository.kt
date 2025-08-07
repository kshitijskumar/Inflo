package org.app.inflo.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.screens.login.domain.RequestOtpRequestApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseAppModel
import org.app.inflo.screens.login.domain.VerifyLoginRequestApiModel
import org.app.inflo.screens.login.domain.VerifyLoginResponseApiModel

interface AppRepository {

    fun loggedInUser(): Flow<UserAppModel?>
    
    fun onboardedUser(): Flow<OnboardedUser?>
    
    suspend fun requestOtp(request: RequestOtpRequestApiModel): RequestOtpResponseApiModel
    
    suspend fun requestOtpAppModel(request: RequestOtpRequestApiModel): RequestOtpResponseAppModel
    
    suspend fun verifyLogin(request: VerifyLoginRequestApiModel): VerifyLoginResponseApiModel
    
    suspend fun storeUser(user: UserAppModel)
    
    suspend fun storeOnboardedUser(onboardedUser: OnboardedUser)
    
    suspend fun finishOnboarding(onboardedUser: OnboardedUser): UserAppModel

    suspend fun clearAllUserData()

}