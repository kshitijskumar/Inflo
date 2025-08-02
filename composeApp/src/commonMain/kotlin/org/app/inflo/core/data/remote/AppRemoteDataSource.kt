package org.app.inflo.core.data.remote

import org.app.inflo.screens.login.domain.RequestOtpRequestApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseApiModel
import org.app.inflo.screens.login.domain.VerifyLoginRequestApiModel
import org.app.inflo.screens.login.domain.VerifyLoginResponseApiModel

interface AppRemoteDataSource {
    
    suspend fun requestOtp(request: RequestOtpRequestApiModel): RequestOtpResponseApiModel
    
    suspend fun verifyLogin(request: VerifyLoginRequestApiModel): VerifyLoginResponseApiModel
    
}