package org.app.inflo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import inflo.composeapp.generated.resources.Res
import inflo.composeapp.generated.resources.clear
import inflo.composeapp.generated.resources.enter_phone_number
import inflo.composeapp.generated.resources.get_otp
import inflo.composeapp.generated.resources.ic_arrow_back
import inflo.composeapp.generated.resources.ic_clear
import inflo.composeapp.generated.resources.phone_number_subtitle
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.BackHandler
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.core.ui.AppPrimaryButton
import org.app.inflo.core.ui.AppTextField
import org.app.inflo.core.ui.AppToolbar
import org.app.inflo.core.ui.LoadingDialog
import org.app.inflo.screens.login.LoginIntent
import org.app.inflo.screens.login.LoginScreenType
import org.app.inflo.screens.login.LoginState
import org.app.inflo.screens.login.LoginViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(vm: LoginViewModel) {
    val state by vm.viewState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LoginScreenContent(
            state = state,
            sendIntent = vm::processIntent,
            modifier = Modifier.fillMaxSize()
        )
    }

    if (state.isLoading) {
        LoadingDialog()
    }

    BackHandler {
        vm.processIntent(LoginIntent.BackClickedIntent)
    }
}

@Composable
private fun LoginScreenContent(
    state: LoginState,
    sendIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Toolbar
        AppToolbar(
            backClicked = { sendIntent(LoginIntent.BackClickedIntent) }
        )

        when(state.screenType) {
            LoginScreenType.PHONE_NUMBER -> LoginPhoneNumberScreen(state, sendIntent)
            LoginScreenType.OTP -> { Text("otp") }
        }
    }

}

@Composable
private fun LoginPhoneNumberScreen(
    state: LoginState,
    sendIntent: (LoginIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
            .padding(horizontal = AppTheme.dimens.medium3)
    ) {

        Spacer(modifier = Modifier.height(AppTheme.dimens.medium4))

        // Title
        Text(
            text = stringResource(Res.string.enter_phone_number),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.color.black
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.medium1))

        // Subtitle
        Text(
            text = stringResource(Res.string.phone_number_subtitle),
            fontSize = 16.sp,
            color = AppTheme.color.black.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.medium4))

        // Phone number input
        PhoneNumberInput(
            phoneNumber = state.numberEntered,
            onPhoneNumberChange = { number ->
                sendIntent(LoginIntent.PhoneNumberEnteredIntent(number))
            },
            error = state.error
        )

        Spacer(modifier = Modifier.weight(1f))

        // Get OTP Button
        AppPrimaryButton(
            text = stringResource(Res.string.get_otp),
            onClick = { sendIntent(LoginIntent.OnGetOtpClickedIntent) },
            enabled = state.shouldEnableGetOtp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.medium4))
    }
}

@Composable
private fun PhoneNumberInput(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    error: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Country code selector
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .background(AppTheme.color.white)
                .border(
                    width = AppTheme.dimens.small0,
                    color = AppTheme.color.black40,
                    shape = RoundedCornerShape(AppTheme.dimens.small2)
                )
                .padding(
                    horizontal = AppTheme.dimens.medium2,
                    vertical = AppTheme.dimens.medium2
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🇮🇳",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = "+91",
                fontSize = 16.sp,
                color = AppTheme.color.black
            )
        }

        Spacer(modifier = Modifier.size(AppTheme.dimens.medium1))

        // Phone number field
        AppTextField(
            value = phoneNumber,
            onValueChange = { value ->
                // Only allow digits and limit to 10 characters
                val filtered = value.filter { it.isDigit() }.take(10)
                onPhoneNumberChange(filtered)
            },
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                if (phoneNumber.isNotEmpty()) {
                    IconButton(
                        onClick = { onPhoneNumberChange("") }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_clear),
                            contentDescription = stringResource(Res.string.clear),
                            tint = AppTheme.color.black.copy(alpha = 0.5f)
                        )
                    }
                }
            },
            isError = error != null,
        )
    }

    if (error != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = error,
            color = AppTheme.color.baseRed,
            fontSize = 12.sp
        )
    }
}