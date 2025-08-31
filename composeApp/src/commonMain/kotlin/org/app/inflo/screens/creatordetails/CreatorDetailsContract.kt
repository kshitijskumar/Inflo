package org.app.inflo.screens.creatordetails

data class CreatorDetailsState(
    val bankEntries: CreatorDetailsBankDetailsEntries = CreatorDetailsBankDetailsEntries(),
    val screenType: CreatorDetailsScreen = CreatorDetailsScreen.BANK_DETAILS,
    val enableFinishBtn: Boolean = false
)

sealed class CreatorDetailsIntent {
    data object InitialisationIntent : CreatorDetailsIntent()
    
    // Bank details field intents
    data class AccountHolderNameEnteredIntent(val value: String) : CreatorDetailsIntent()
    data class BankAccountNumberEnteredIntent(val value: String) : CreatorDetailsIntent()
    data class ConfirmBankAccountNumberEnteredIntent(val value: String) : CreatorDetailsIntent()
    data class BankNameEnteredIntent(val value: String) : CreatorDetailsIntent()
    data class IfscCodeEnteredIntent(val value: String) : CreatorDetailsIntent()
}

sealed class CreatorDetailsEffect

data class CreatorDetailsBankDetailsEntries(
    val accountHolderName: String = "",
    val bankAccountNumber: String = "",
    val confirmBankAccountNumber: String = "",
    val bankName: String = "",
    val ifscCode: String = "",
)

enum class CreatorDetailsScreen {
    BANK_DETAILS
}
