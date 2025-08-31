package org.app.inflo.screens.onboarding.domain

import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.ProfileVerificationStatus
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.screens.login.domain.VerifyLoginResponseApiModel
import org.app.inflo.screens.splash.ProfileType

class ParseUserResponseToProfileUseCase {

    operator fun invoke(response: VerifyLoginResponseApiModel): ParsedResult {
        val profileType = ProfileType.safeValueOf(response.profileType ?: "")
        return when(profileType) {
            ProfileType.BRAND -> {
                completeBrandProfile(response)?.let { ParsedResult.Existing(it) }
                    ?: onboardedBrandProfile(response)?.let { ParsedResult.New(it) }
                    ?: ParsedResult.Error("Profile details missing")
            }
            ProfileType.CREATOR -> {
                val verificationStatus = ProfileVerificationStatus.safeValueOf(response.profileVerificationStatus ?: "")
                when(verificationStatus) {
                    ProfileVerificationStatus.VERIFIED -> {
                        completeCreatorProfile(response)?.let { ParsedResult.Existing(it) }
                            ?: ParsedResult.Error("Profile details missing")
                    }
                    ProfileVerificationStatus.NOT_SUBMITTED,
                    ProfileVerificationStatus.VERIFICATION_PENDING,
                    null -> {
                        onboardedCreatorProfile(response)?.let { ParsedResult.New(it) }
                            ?: ParsedResult.Error("Profile details missing")
                    }
                }
            }
            null -> ParsedResult.Error("Invalid profile information")
        }
    }

    private fun completeBrandProfile(response: VerifyLoginResponseApiModel): UserAppModel.Brand? {
        return UserAppModel.Brand(
            id = response.id ?: return null
        )
    }

    private fun onboardedBrandProfile(response: VerifyLoginResponseApiModel): OnboardedUser.Brand? {
        return OnboardedUser.Brand(
            id = response.id ?: return null,
            mobileNumber = response.mobileNumber ?: return null,
            firstName = response.firstName,
            lastName = response.lastName,
            brandName = response.brandName,
            brandInstagramAccountName = response.instagramAccountName
        )
    }

    private fun completeCreatorProfile(response: VerifyLoginResponseApiModel): UserAppModel.Creator? {
        return UserAppModel.Creator(
            id = response.id ?: return null,
            mobileNumber = response.mobileNumber ?: return null,
            firstName = response.firstName ?: return null,
            lastName = response.lastName ?: return null,
            dob = response.dob ?: return null,
            categories = response.categories ?: return null,
            bankDetails = response.bankDetails
        )
    }

    private fun onboardedCreatorProfile(response: VerifyLoginResponseApiModel): OnboardedUser.Creator? {
        return OnboardedUser.Creator(
            id = response.id ?: return null,
            mobileNumber = response.mobileNumber ?: return null,
            firstName = response.firstName,
            lastName = response.lastName,
            dob = response.dob,
            categories = response.categories,
            verificationStatus = ProfileVerificationStatus.safeValueOf(response.profileVerificationStatus ?: "") ?: ProfileVerificationStatus.VERIFICATION_PENDING,
            bankDetails = response.bankDetails
        )
    }
}

sealed class ParsedResult {
    data class Existing(val userAppModel: UserAppModel) : ParsedResult()
    data class New(val onboardedAppModel: OnboardedUser) : ParsedResult()
    data class Error(val msg: String) : ParsedResult()
}