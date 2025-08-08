package org.app.inflo.screens.login.domain

import kotlinx.serialization.Serializable
import org.app.inflo.core.data.models.ContentCategory
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.screens.login.exception.VerifyLoginFailedException
import org.app.inflo.screens.onboarding.domain.ParseUserResponseToProfileUseCase
import org.app.inflo.screens.onboarding.domain.ParsedResult
import org.app.inflo.utils.ServerErrorCodes

class VerifyLoginUseCase(
    private val repository: AppRepository,
    private val parseUserResponseToProfileUseCase: ParseUserResponseToProfileUseCase
) {

    suspend operator fun invoke(request: VerifyLoginRequestApiModel): LoginResult {
        return try {
            val response = repository.verifyLogin(request)

            return when(val result = parseUserResponseToProfileUseCase.invoke(response)) {
                is ParsedResult.Error -> LoginResult.InvalidResponse
                is ParsedResult.Existing -> {
                    repository.storeUser(result.userAppModel)
                    LoginResult.ExistingUser(result.userAppModel)
                }
                is ParsedResult.New -> {
                    repository.storeOnboardedUser(result.onboardedAppModel)
                    LoginResult.NewUser(result.onboardedAppModel)
                }
            }
        } catch (e: VerifyLoginFailedException) {
            // Map specific exceptions to appropriate LoginResult
            when(e.errorCode) {
                ServerErrorCodes.LOGIN_INVALID_OTP -> LoginResult.InvalidOtp
                ServerErrorCodes.LOGIN_OTP_EXPIRED -> LoginResult.OtpExpired
                else -> LoginResult.GeneralError(e.message)
            }
        } catch (e: Exception) {
            // Handle any other unexpected exceptions
            LoginResult.GeneralError("An unexpected error occurred: ${e.message}")
        }
    }

}

@Serializable
data class VerifyLoginResponseApiModel(
    val profileType: String?,
    val id: String?,
    val mobileNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val dob: Long?,
    val brandName: String?,
    val instagramAccountName: String?,
    val categories: List<ContentCategory>?,
    val profileVerificationStatus: String?
)

@Serializable
data class VerifyLoginRequestApiModel(
    val phoneNumber: String,
    val profileType: String,
    val code: String
)

sealed class LoginResult {

    data class ExistingUser(
        val user: UserAppModel
    ) : LoginResult()

    data class NewUser(
        val onboardedUser: OnboardedUser
    ) : LoginResult()

    data object InvalidOtp : LoginResult()

    data object OtpExpired : LoginResult()

    data object InvalidResponse : LoginResult()

    data class GeneralError(val msg: String?) : LoginResult()

}