package org.app.inflo.screens.login

import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.app.inflo.core.domain.ValidatePhoneNumberUseCase
import org.app.inflo.core.domain.ValidationResult
import org.app.inflo.core.viewmodel.AppBaseViewModel
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.args.LoginArgs
import org.app.inflo.navigation.goBack
import org.app.inflo.screens.login.domain.RequestOtpUseCase
import org.app.inflo.screens.login.domain.VerifyLoginRequestApiModel
import org.app.inflo.screens.login.domain.VerifyLoginUseCase
import org.app.inflo.screens.login.domain.LoginResult

class LoginViewModel(
    private val args: LoginArgs,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
    private val verifyLoginUseCase: VerifyLoginUseCase,
    private val navigationManager: InfloNavigationManager
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
            is LoginIntent.OnOtpEnteredIntent -> handleOtpEnteredIntent(intent)
            is LoginIntent.SubmitOtpIntent -> handleSubmitOtpIntent()
            LoginIntent.BackClickedIntent -> handleBackClickedIntent()
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

    private fun handleOtpEnteredIntent(intent: LoginIntent.OnOtpEnteredIntent) {
        val otp = intent.otp.trim()
        val validationError = validateOtp(otp)
        
        updateState { currentState ->
            currentState.copy(
                otpEntered = otp,
                error = validationError,
                shouldEnableSubmit = validationError == null
            )
        }
    }

    private fun handleSubmitOtpIntent() {
        val currentState = viewState.value
        val otp = currentState.otpEntered.trim()
        val phoneNumber = currentState.numberEntered.trim()
        val otpCode = currentState.otpCode
        
        // Check if OTP entered and phone number are available
        if (phoneNumber.isEmpty() || otpCode == null) {
            updateState { state ->
                state.copy(
                    screenType = LoginScreenType.PHONE_NUMBER,
                    error = "Phone number or OTP code not available"
                )
            }
            return
        }
        
        updateState { state ->
            state.copy(isLoading = true)
        }
        
        viewModelScope.launch {
            val loginResult = verifyLoginUseCase.invoke(
                VerifyLoginRequestApiModel(
                    phoneNumber = phoneNumber,
                    profileType = args.profileType.name,
                    code = otp
                )
            )
            
            when (loginResult) {
                is LoginResult.ExistingUser -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                    // TODO: Navigate to main screen for existing user
                }
                is LoginResult.NewUser -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                    // TODO: Navigate to onboarding screen for new user
                }
                is LoginResult.InvalidOtp -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            error = "Invalid OTP entered"
                        )
                    }
                }
                is LoginResult.OtpExpired -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            error = "OTP has expired",
                            screenType = LoginScreenType.PHONE_NUMBER
                        )
                    }
                }
                is LoginResult.InvalidResponse -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            error = "Invalid response from server"
                        )
                    }
                }
                is LoginResult.GeneralError -> {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            error = loginResult.msg ?: "Something went wrong"
                        )
                    }
                }
            }
        }
    }

    private fun handleBackClickedIntent() {
        when(viewState.value.screenType) {
            LoginScreenType.PHONE_NUMBER -> {
                navigationManager.goBack()
            }
            LoginScreenType.OTP -> {
                updateState {
                    it.copy(
                        screenType = LoginScreenType.PHONE_NUMBER
                    )
                }
            }
        }
    }

    private fun validateOtp(otp: String): String? {
        return when {
            otp.length != 6 -> "OTP must be exactly 6 digits"
            otp.any { !it.isDigit() } -> "OTP must contain only digits"
            else -> null
        }
    }
}