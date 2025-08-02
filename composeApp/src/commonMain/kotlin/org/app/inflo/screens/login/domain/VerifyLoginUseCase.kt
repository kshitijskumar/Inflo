package org.app.inflo.screens.login.domain

import kotlinx.serialization.Serializable
import org.app.inflo.core.data.models.ContentCategory
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.screens.login.LoginState
import org.app.inflo.screens.login.exception.VerifyLoginFailedException
import org.app.inflo.screens.splash.ProfileType
import org.app.inflo.utils.ServerErrorCodes

class VerifyLoginUseCase(
    private val repository: AppRepository
) {

    suspend operator fun invoke(request: VerifyLoginRequestApiModel): LoginResult {
        return try {
            val response = repository.verifyLogin(request)
            
            // Check if id and mobile number are null or empty
            if (!response.isValidResponse()) {
                return LoginResult.InvalidResponse
            }

            if (response.isCompleteProfile()) {
                // Existing user with complete profile
                val existingUser = response.existingUser() ?: return LoginResult.InvalidResponse
                repository.storeUser(existingUser)
                LoginResult.ExistingUser(existingUser)
            } else {
                // New user or incomplete profile - needs onboarding
                val onboardedUser = response.onboardedUser() ?: return LoginResult.InvalidResponse
                repository.storeOnboardedUser(onboardedUser)
                LoginResult.NewUser(onboardedUser)
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
    val categories: List<ContentCategory>?,
)

fun VerifyLoginResponseApiModel.isValidResponse(): Boolean {
    return !this.id.isNullOrEmpty()
            && !this.mobileNumber.isNullOrEmpty()
            && ProfileType.safeValueOf(profileType ?: "") != null
}

fun VerifyLoginResponseApiModel.isCompleteProfile(): Boolean {
    return this.isValidResponse()
            && !firstName.isNullOrEmpty()
            && !lastName.isNullOrEmpty()
            && dob != null
            && categories != null
            && categories.size >= 3
}

fun VerifyLoginResponseApiModel.onboardedUser(): OnboardedUser? {
    val profileType = ProfileType.safeValueOf(this.profileType ?: "") ?: return null

    return when(profileType) {
        ProfileType.CREATOR -> {
            OnboardedUser.Creator(
                id = this.id ?: return null,
                mobileNumber = this.mobileNumber ?: return null,
                firstName = this.firstName,
                lastName = this.lastName,
                dob = this.dob,
                categories = this.categories
            )
        }
        ProfileType.BRAND -> {
            OnboardedUser.Brand(
                id = this.id ?: return null
            )
        }
    }
}

fun VerifyLoginResponseApiModel.existingUser(): UserAppModel? {
    val profileType = ProfileType.safeValueOf(this.profileType ?: "") ?: return null

    return when(profileType) {
        ProfileType.CREATOR -> {
            UserAppModel.Creator(
                id = this.id ?: return null,
                mobileNumber = this.mobileNumber ?: return null,
                firstName = this.firstName ?: return null,
                lastName = this.lastName ?: return null,
                dob = this.dob ?: return null,
                categories = this.categories ?: return null
            )
        }
        ProfileType.BRAND -> {
            UserAppModel.Brand(
                id = this.id ?: return null
            )
        }
    }
}

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