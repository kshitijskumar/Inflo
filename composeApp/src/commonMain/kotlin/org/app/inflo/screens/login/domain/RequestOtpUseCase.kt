package org.app.inflo.screens.login.domain

import kotlinx.serialization.Serializable
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.screens.login.exception.RequestOtpFailedException
import org.app.inflo.screens.splash.ProfileType

class RequestOtpUseCase(
    private val repository: AppRepository
) {

    suspend operator fun invoke(
        number: String,
        profileType: ProfileType
    ) : RequestOtpResponseAppModel? {
        return try {
            val request = RequestOtpRequestApiModel(
                number = number,
                profileType = profileType.name
            )
            
            repository.requestOtpAppModel(request)
        } catch (e: RequestOtpFailedException) {
            // Handle exception in use case and return null
            null
        }
    }

}

@Serializable
data class RequestOtpRequestApiModel(
    val number: String,
    val profileType: String
)

@Serializable
data class RequestOtpResponseApiModel(
    val code: String?
)

data class RequestOtpResponseAppModel(
    val code: String
)

fun RequestOtpResponseApiModel.toAppModelOrNullIfInvalid(): RequestOtpResponseAppModel? {
    return RequestOtpResponseAppModel(
        code = code?.takeIf { it.isNotEmpty() } ?: return null
    )
}