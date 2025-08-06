package org.app.inflo.screens.onboarding

import org.app.inflo.core.data.models.ContentCategory
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.utils.AppSystem

data class OnboardingState(
    val onboardedUser: OnboardedUser? = null,
    val details: List<OnboardingDetailsInfo>? = null,
    val currentDetailsIndex: Int = 0,
    val shouldEnableConfirmBtn: Boolean = false,
    val showDatePicker: Boolean = false,
    val selectedDateForPicker: Long = AppSystem.currentTimeInMillis()
)

sealed class OnboardingIntent {
    data object InitialisationIntent : OnboardingIntent()

    data object SubmitClickedIntent : OnboardingIntent()

    data object BackClickedIntent : OnboardingIntent()

    data class FirstNameEnteredIntent(val name: String) : OnboardingIntent()
    
    data class LastNameEnteredIntent(val name: String) : OnboardingIntent()
    
    data class DobEnteredIntent(val dobInMillis: Long) : OnboardingIntent()
    
    data class BrandNameEnteredIntent(val name: String) : OnboardingIntent()
    
    data class InstagramAccountEnteredIntent(val accountName: String) : OnboardingIntent()
    
    data class CategoryClickedIntent(val category: ContentCategory) : OnboardingIntent()
    
    data object ShowDatePickerIntent : OnboardingIntent()
    
    data object HideDatePickerIntent : OnboardingIntent()
}

sealed class OnboardingEffect