package org.app.inflo.screens.creatordetails

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.core.viewmodel.AppBaseViewModel
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloNavOptions
import org.app.inflo.navigation.PopUpToConfig
import org.app.inflo.navigation.args.SplashArgs
import org.app.inflo.navigation.navigate

class CreatorDetailsViewModel(
    private val repository: AppRepository,
    private val navigationManager: InfloNavigationManager
) : AppBaseViewModel<CreatorDetailsIntent, CreatorDetailsState, CreatorDetailsEffect>() {

    override fun initialViewState() = CreatorDetailsState()

    init {
        processIntent(CreatorDetailsIntent.InitialisationIntent)
    }

    override fun processIntent(intent: CreatorDetailsIntent) {
        super.processIntent(intent)
        when(intent) {
            CreatorDetailsIntent.InitialisationIntent -> handleInitialisationIntent()
        }
    }

    private fun handleInitialisationIntent() {
        viewModelScope.launch {
            // Check for logged in user
            val loggedInUser = repository.loggedInUser().firstOrNull()
            
            // If user is null or not a creator, navigate back to splash screen clearing the entire stack
            when(loggedInUser) {
                is UserAppModel.Creator -> {} // valid case
                is UserAppModel.Brand,
                null -> {
                    navigationManager.navigate(
                        args = SplashArgs,
                        navOptions = InfloNavOptions(
                            popUpToConfig = PopUpToConfig.ClearAll()
                        )
                    )
                    return@launch
                }
            }
            
            // Update the state with user's bank details data
            val bankDetails = loggedInUser.bankDetails
            updateState { currentState ->
                val bankEntries = CreatorDetailsBankDetailsEntries(
                    accountHolderName = bankDetails?.accountHolderName ?: "",
                    bankAccountNumber = bankDetails?.bankAccountNumber ?: "",
                    confirmBankAccountNumber = bankDetails?.bankAccountNumber ?: "",
                    bankName = bankDetails?.bankName ?: "",
                    ifscCode = bankDetails?.ifscCode ?: ""
                )

                currentState.copy(
                    bankEntries = bankEntries,
                    enableFinishBtn = finishBtnEnabledForBankDetails(bankEntries)
                )
            }
        }
    }

    private fun finishBtnEnabledForBankDetails(data: CreatorDetailsBankDetailsEntries): Boolean {
        return with(data) {
            accountHolderName.isNotBlank() &&
                    isValidBankAccountNumber(data.bankAccountNumber) &&
                    data.bankAccountNumber == data.confirmBankAccountNumber &&
                    data.bankName.isNotBlank() &&
                    data.ifscCode.isNotBlank()
        }
    }

    private fun isValidBankAccountNumber(bankAccountNumber: String): Boolean {
        return bankAccountNumber.isNotBlank() &&
                bankAccountNumber.all { it.isDigit() } &&
                bankAccountNumber.length in (9..18)
    }
}