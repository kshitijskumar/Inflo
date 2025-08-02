package org.app.inflo.screens.login

import org.app.inflo.navigation.args.LoginArgs
import org.app.inflo.screens.login.domain.RequestOtpResponseAppModel

data class LoginState(
    val numberEntered: String = "",
    val otpEntered: String = "",
    val resentOtpIn: String? = null,
    val shouldEnableGetOtp: Boolean = false,
    val shouldEnableSubmit: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val screenType: LoginScreenType = LoginScreenType.PHONE_NUMBER,
    val otpCode: RequestOtpResponseAppModel? = null,
)

enum class LoginScreenType {
    PHONE_NUMBER,
    OTP
}

sealed class LoginIntent {

    data class InitialisationIntent(val args: LoginArgs) : LoginIntent()

    data class PhoneNumberEnteredIntent(val number: String) : LoginIntent()

    object OnGetOtpClickedIntent : LoginIntent()

    data class OnOtpEnteredIntent(val otp: String) : LoginIntent()

    data object SubmitOtpIntent : LoginIntent()

    data object BackClickedIntent : LoginIntent()

}

sealed class LoginEffect {

}