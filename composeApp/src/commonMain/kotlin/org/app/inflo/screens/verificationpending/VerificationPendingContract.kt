package org.app.inflo.screens.verificationpending

import org.app.inflo.core.data.models.OnboardedUser

data class VerificationPendingState(
    val onboardedUser: OnboardedUser? = null,
    val isLoading: Boolean = false
)

sealed class VerificationPendingIntent {
    data object InitialisationIntent : VerificationPendingIntent()
    data object OnStartIntent : VerificationPendingIntent()
}

sealed class VerificationPendingEffect {

}