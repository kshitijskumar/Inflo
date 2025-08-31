package org.app.inflo.screens.creatordetails

data class CreatorDetailsState(
    val bankEntries: CreatorDetailsBankDetailsEntries = CreatorDetailsBankDetailsEntries(),
    val screenType: CreatorDetailsScreen = CreatorDetailsScreen.BANK_DETAILS,
    val enableFinishBtn: Boolean = false
)

sealed class CreatorDetailsIntent {
    data object InitialisationIntent : CreatorDetailsIntent()
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
