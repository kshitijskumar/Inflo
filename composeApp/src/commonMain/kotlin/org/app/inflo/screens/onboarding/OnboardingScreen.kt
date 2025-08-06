package org.app.inflo.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import inflo.composeapp.generated.resources.Res
import inflo.composeapp.generated.resources.ic_arrow_back
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.navigation.BackHandler
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.core.ui.AppPrimaryButton
import org.app.inflo.core.ui.AppTextField
import org.app.inflo.core.ui.AppToolbar
import org.app.inflo.core.ui.appColors
import org.app.inflo.core.utils.TimeUtils
import org.app.inflo.utils.AppSystem
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(vm: OnboardingViewModel) {
    val state by vm.viewState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
    ) {
        state.details?.let { details ->
            val detailsScreen = details.getOrNull(state.currentDetailsIndex)

            AppToolbar(
                backIcon = if (state.currentDetailsIndex == 0) null else Res.drawable.ic_arrow_back,
                backClicked = {
                    vm.processIntent(OnboardingIntent.BackClickedIntent)
                }
            )

            Text(
                text = "Let's get started",
                style = AppTheme.typography.headlineLarge,
                color = AppTheme.color.black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = AppTheme.dimens.medium4,
                        start = AppTheme.dimens.medium3,
                        end = AppTheme.dimens.medium3
                    )
            )

            when(detailsScreen) {
                OnboardingDetailsInfo.BasicUserDetails -> {
                    BasicUserDetailsScreen(
                        state = state,
                        sendIntent = vm::processIntent,
                        timeUtils = koinInject(),
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    )
                }
                OnboardingDetailsInfo.BasicBrandDetails -> {
                    BasicBrandDetailsScreen(
                        state = state,
                        sendIntent = vm::processIntent,
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    )
                }
                is OnboardingDetailsInfo.Categories -> {
                    (state.onboardedUser as? OnboardedUser.Creator)?.let { user ->
                        OnboardingCreatorCategories(
                            user = user,
                            screenState = detailsScreen,
                            sendIntent = vm::processIntent,
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
                null -> {}
            }

            val isLastStep = state.currentDetailsIndex == details.lastIndex

            AppPrimaryButton(
                text = if (isLastStep) "Finish" else "Next",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimens.medium3),
                enabled = state.shouldEnableConfirmBtn,
                onClick = {
                    vm.processIntent(OnboardingIntent.SubmitClickedIntent)
                }
            )

            Spacer(Modifier.height(AppTheme.dimens.medium3))
        }
    }

    BackHandler {
        vm.processIntent(OnboardingIntent.BackClickedIntent)
    }

    // Date Picker Dialog
    if (state.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.selectedDateForPicker,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis <= AppSystem.currentTimeInMillis()
                }
            }
        )
        
        DatePickerDialog(
            onDismissRequest = {
                vm.processIntent(OnboardingIntent.HideDatePickerIntent)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate ->
                            vm.processIntent(OnboardingIntent.DobEnteredIntent(selectedDate))
                        }
                        vm.processIntent(OnboardingIntent.HideDatePickerIntent)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        vm.processIntent(OnboardingIntent.HideDatePickerIntent)
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@Composable
fun OnboardingCreatorCategories(
    user: OnboardedUser.Creator,
    screenState: OnboardingDetailsInfo.Categories,
    sendIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = AppTheme.dimens.medium3)
    ) {
        Text(
            text = "Select the categories that best define you.",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.color.black60,
            modifier = Modifier.padding(top = AppTheme.dimens.small2)
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.large2))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium1),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium2)
        ) {
                         screenState.categories.forEach { item ->
                 CategorySelectablePill(
                     categoryName = item.name,
                     isSelected = user.categories?.find { it.id == item.id } != null,
                     onClick = { 
                         sendIntent(OnboardingIntent.CategoryClickedIntent(item))
                     },
                 )
             }
        }

        Text(
            text = "${user.categories?.size ?: 0}/${OnboardingViewModel.MAX_CATEGORY_SELECTION_ALLOWED}",
            color = AppTheme.color.black60,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(AppTheme.dimens.medium3)
        )
    }
}

@Composable
fun CategorySelectablePill(
    categoryName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                color = if (isSelected) AppTheme.color.secondaryRed else AppTheme.color.black.copy(0.05f),
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = if (isSelected) AppTheme.color.baseRed else AppTheme.color.black40,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = AppTheme.dimens.medium4,
                vertical = AppTheme.dimens.medium2
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = categoryName,
            textAlign = TextAlign.Center,
            color = if (isSelected) AppTheme.color.baseRed else AppTheme.color.black60
        )
    }
}

@Composable
fun BasicBrandDetailsScreen(
    state: OnboardingState,
    sendIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier
) {
    val onboardedUser = state.onboardedUser
    
    Column(
        modifier = modifier
            .padding(horizontal = AppTheme.dimens.medium3)
    ) {
        Text(
            text = "Time for us to get to know one another!",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.color.black60,
            modifier = Modifier.padding(top = AppTheme.dimens.small2)
        )
        
        Spacer(modifier = Modifier.height(AppTheme.dimens.medium4))
        
        // First name and last name fields in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium2)
        ) {
            // First name field
            AppTextField(
                value = (onboardedUser as? OnboardedUser.Brand)?.firstName ?: "",
                onValueChange = { name ->
                    sendIntent(OnboardingIntent.FirstNameEnteredIntent(name))
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        text = "First name",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black60
                    )
                },
                placeholder = {
                    Text(
                        text = "John",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black40
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
            )
            
            // Last name field
            AppTextField(
                value = (onboardedUser as? OnboardedUser.Brand)?.lastName ?: "",
                onValueChange = { name ->
                    sendIntent(OnboardingIntent.LastNameEnteredIntent(name))
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        text = "Last Name",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black60
                    )
                },
                placeholder = {
                    Text(
                        text = "Doe",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black40
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
            )
        }
        
        Spacer(modifier = Modifier.height(AppTheme.dimens.medium3))
        
        // Brand Name field
        AppTextField(
            value = (onboardedUser as? OnboardedUser.Brand)?.brandName ?: "",
            onValueChange = { name ->
                sendIntent(OnboardingIntent.BrandNameEnteredIntent(name))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Brand Name",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.color.black60
                )
            },
            placeholder = {
                Text(
                    text = "JD Fitness",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.color.black40
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            )
        )
        
        Spacer(modifier = Modifier.height(AppTheme.dimens.medium3))
        
        // Brand Instagram field
        AppTextField(
            value = (onboardedUser as? OnboardedUser.Brand)?.brandInstagramAccountName ?: "",
            onValueChange = { accountName ->
                sendIntent(OnboardingIntent.InstagramAccountEnteredIntent(accountName))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Brand Instagram",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.color.black60
                )
            },
            placeholder = {
                Text(
                    text = "@",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.color.black40
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
    }
}

@Composable
fun BasicUserDetailsScreen(
    state: OnboardingState,
    sendIntent: (OnboardingIntent) -> Unit,
    timeUtils: TimeUtils,
    modifier: Modifier = Modifier
) {
    val onboardedUser = state.onboardedUser
    
    Column(
        modifier = modifier
            .padding(horizontal = AppTheme.dimens.medium3)
    ) {
        Text(
            text = "Time for us to get to know one another!",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.color.black60,
            modifier = Modifier.padding(top = AppTheme.dimens.small2)
        )
        
        Spacer(modifier = Modifier.height(AppTheme.dimens.medium4))
        
        // First name and last name fields in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium2)
        ) {
            // First name field
            AppTextField(
                value = (onboardedUser as? OnboardedUser.Creator)?.firstName ?: "",
                onValueChange = { name ->
                    sendIntent(OnboardingIntent.FirstNameEnteredIntent(name))
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        text = "First name",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black60
                    )
                },
                placeholder = {
                    Text(
                        text = "John",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black40
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
            )
            
            // Last name field
            AppTextField(
                value = (onboardedUser as? OnboardedUser.Creator)?.lastName ?: "",
                onValueChange = { name ->
                    sendIntent(OnboardingIntent.LastNameEnteredIntent(name))
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text(
                        text = "Last Name",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black60
                    )
                },
                placeholder = {
                    Text(
                        text = "Doe",
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.color.black40
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
            )
        }
        
        Spacer(modifier = Modifier.height(AppTheme.dimens.medium3))
        
        // DOB field
        AppTextField(
            value = (onboardedUser as? OnboardedUser.Creator)?.dob?.let { dob ->
                // Convert milliseconds to DD/MM/YYYY format using TimeUtils
                timeUtils.ddMMyyyy(dob)
            } ?: "",
            onValueChange = { /* DOB will be handled by date picker */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Only trigger date picker for Creator users
                    val onboardedUser = state.onboardedUser
                    if (onboardedUser is OnboardedUser.Creator) {
                        sendIntent(OnboardingIntent.ShowDatePickerIntent)
                    }
                },
            label = {
                Text(
                    text = "DOB",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.color.black60
                )
            },
            placeholder = {
                Text(
                    text = "DD/MM/YYYY",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.color.black40
                )
            },
            singleLine = true,
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.appColors(
                disabledTextColor = AppTheme.color.black
            )
        )
    }
}
