package org.app.inflo.screens.login

import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.app.inflo.core.domain.ValidatePhoneNumberUseCase
import org.app.inflo.core.domain.ValidationResult
import org.app.inflo.core.viewmodel.AppBaseViewModel
import org.app.inflo.navigation.args.LoginArgs
import org.app.inflo.screens.login.domain.RequestOtpUseCase

class LoginViewModel(
    private val args: LoginArgs,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase,
    private val requestOtpUseCase: RequestOtpUseCase
) : AppBaseViewModel<LoginIntent, LoginState, LoginEffect>() {

    init {
        processIntent(LoginIntent.InitialisationIntent(args))
    }

    override fun initialViewState(): LoginState {
        return LoginState()
    }

    override fun processIntent(intent: LoginIntent) {
        super.processIntent(intent)
        when(intent) {
            is LoginIntent.InitialisationIntent -> handleInitialisationIntent(intent)
            is LoginIntent.PhoneNumberEnteredIntent -> handlePhoneNumberEnteredIntent(intent)
            is LoginIntent.OnGetOtpClickedIntent -> handleGetOtpClickedIntent()
        }
    }

    private fun handleInitialisationIntent(intent: LoginIntent.InitialisationIntent) {}

    private fun handlePhoneNumberEnteredIntent(intent: LoginIntent.PhoneNumberEnteredIntent) {
        val phoneNumber = intent.number.trim()
        val validationResult = validatePhoneNumberUseCase(phoneNumber)
        val error: String?
        val shouldEnableGetOtp: Boolean

        when(validationResult) {
            is ValidationResult.Error -> {
                error = validationResult.message
                shouldEnableGetOtp = false
            }
            ValidationResult.Success -> {
                error = null
                shouldEnableGetOtp = true
            }
        }
        
        updateState { currentState ->
            currentState.copy(
                numberEntered = phoneNumber,
                error = error,
                shouldEnableGetOtp = shouldEnableGetOtp
            )
        }
    }

    private fun handleGetOtpClickedIntent() {
        val currentState = viewState.value
        
        if (!currentState.shouldEnableGetOtp) {
            return
        }
        
        updateState { state ->
            state.copy(isLoading = true)
        }
        
        viewModelScope.launch {
            val otpResponse = requestOtpUseCase.invoke(
                number = currentState.numberEntered,
                profileType = args.profileType
            )
            
            if (otpResponse != null) {
                // Success - update screen type to OTP
                updateState { state ->
                    state.copy(
                        isLoading = false,
                        screenType = LoginScreenType.OTP,
                        otpCode = otpResponse,
                        error = null
                    )
                }
            } else {
                // Failure - show generic error
                updateState { state ->
                    state.copy(
                        isLoading = false,
                        error = "Something went wrong"
                    )
                }
            }
        }
    }
}