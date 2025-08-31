package org.app.inflo.core.data.models

import kotlinx.serialization.Serializable

@Serializable
sealed class UserAppModel {

    abstract val id: String

    @Serializable
    data class Creator(
        override val id: String,
        val mobileNumber: String,
        val firstName: String,
        val lastName: String,
        val dob: Long,
        val categories: List<ContentCategory>,
        val bankDetails: CreatorBankDetailsAppModel?
    ) : UserAppModel()

    @Serializable
    data class Brand(
        override val id: String
    ) : UserAppModel()
}

@Serializable
sealed class OnboardedUser {

    abstract val id: String

    @Serializable
    data class Creator(
        override val id: String,
        val mobileNumber: String,
        val firstName: String?,
        val lastName: String?,
        val dob: Long?,
        val categories: List<ContentCategory>?,
        val verificationStatus: ProfileVerificationStatus,
        val bankDetails: CreatorBankDetailsAppModel?
    ) : OnboardedUser()

    @Serializable
    data class Brand(
        override val id: String,
        val mobileNumber: String,
        val firstName: String?,
        val lastName: String?,
        val brandName: String?,
        val brandInstagramAccountName: String?
    ) : OnboardedUser()

}

@Serializable
data class ContentCategory(
    val id: String,
    val name: String
)

@Serializable
data class CreatorBankDetailsAppModel(
    val accountHolderName: String,
    val bankAccountNumber: String,
    val bankName: String,
    val ifscCode: String
)

enum class ProfileVerificationStatus {
    NOT_SUBMITTED,
    VERIFIED,
    VERIFICATION_PENDING;

    companion object {
        fun safeValueOf(str: String): ProfileVerificationStatus? {
            return try {
                ProfileVerificationStatus.valueOf(str)
            } catch (e: Exception) {
                null
            }
        }
    }
}