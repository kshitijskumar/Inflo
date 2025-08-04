package org.app.inflo.core.data.remote

import kotlinx.coroutines.delay
import org.app.inflo.core.data.models.ContentCategory
import org.app.inflo.screens.login.domain.RequestOtpRequestApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseApiModel
import org.app.inflo.screens.login.domain.VerifyLoginRequestApiModel
import org.app.inflo.screens.login.domain.VerifyLoginResponseApiModel
import org.app.inflo.screens.login.exception.RequestOtpFailedException
import org.app.inflo.screens.login.exception.VerifyLoginFailedException
import org.app.inflo.screens.splash.ProfileType
import org.app.inflo.utils.AppSystem
import org.app.inflo.utils.ServerErrorCodes

class AppRemoteDataSourceImpl : AppRemoteDataSource {
    
    override suspend fun requestOtp(request: RequestOtpRequestApiModel): RequestOtpResponseApiModel {
        // Simulate network delay
        delay(1000)
        
        // Mock logic: Simulate success for valid phone numbers, failure for specific cases
        when {
            request.number.startsWith("9999") -> {
                throw RequestOtpFailedException("Server error: Invalid phone number")
            }
            request.number.startsWith("0000") -> {
                throw RequestOtpFailedException("Network error: Unable to send OTP")
            }
            request.number.length != 10 -> {
                throw RequestOtpFailedException("Validation error: Invalid phone number format")
            }
            else -> {
                // Success case - return mock OTP code
                return RequestOtpResponseApiModel(
                    code = "123456"
                )
            }
        }
    }
    
    override suspend fun verifyLogin(request: VerifyLoginRequestApiModel): VerifyLoginResponseApiModel {
        // Simulate network delay
        delay(1500)
        
        // Mock logic for different scenarios
        when {
            request.code == "000000" -> {
                throw VerifyLoginFailedException(ServerErrorCodes.LOGIN_INVALID_OTP, "Invalid OTP entered")
            }
            request.code == "111111" -> {
                throw VerifyLoginFailedException(ServerErrorCodes.LOGIN_OTP_EXPIRED, "OTP has expired")
            }
            request.code == "999999" -> {
                throw VerifyLoginFailedException(0, "Server error occurred")
            }
            request.phoneNumber.startsWith("8888") -> {
                // Mock existing user
                return VerifyLoginResponseApiModel(
                    profileType = ProfileType.CREATOR.name,
                    id = "user_123",
                    mobileNumber = request.phoneNumber,
                    firstName = "John",
                    lastName = "Doe",
                    dob = AppSystem.currentTimeInMillis() - (25 * 365 * 24 * 60 * 60 * 1000L), // 25 years ago
                    brandName = null,
                    instagramAccountName = null,
                    categories = listOf(
                        ContentCategory("beauty", "Beauty"),
                        ContentCategory("fitness", "Fitness"),
                        ContentCategory("automobile", "Automobile"),
                    )
                )
            }
            else -> {
                // Mock new user (minimal details)
                return VerifyLoginResponseApiModel(
                    profileType = ProfileType.CREATOR.name,
                    id = "new_user_${AppSystem.currentTimeInMillis()}",
                    mobileNumber = request.phoneNumber,
                    firstName = null,
                    lastName = null,
                    dob = null,
                    brandName = null,
                    instagramAccountName = null,
                    categories = null
                )
            }
        }
    }
} 