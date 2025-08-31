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
            is CreatorDetailsIntent.AccountHolderNameEnteredIntent -> handleAccountHolderNameEntered(intent)
            is CreatorDetailsIntent.BankAccountNumberEnteredIntent -> handleBankAccountNumberEntered(intent)
            is CreatorDetailsIntent.ConfirmBankAccountNumberEnteredIntent -> handleConfirmBankAccountNumberEntered(intent)
            is CreatorDetailsIntent.BankNameEnteredIntent -> handleBankNameEntered(intent)
            is CreatorDetailsIntent.IfscCodeEnteredIntent -> handleIfscCodeEntered(intent)
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

    private fun handleAccountHolderNameEntered(intent: CreatorDetailsIntent.AccountHolderNameEnteredIntent) {
        updateState { currentState ->
            val updatedBankEntries = currentState.bankEntries.copy(
                accountHolderName = intent.value
            )
            currentState.copy(
                bankEntries = updatedBankEntries,
                enableFinishBtn = finishBtnEnabledForBankDetails(updatedBankEntries)
            )
        }
    }

    private fun handleBankAccountNumberEntered(intent: CreatorDetailsIntent.BankAccountNumberEnteredIntent) {
        updateState { currentState ->
            val updatedBankEntries = currentState.bankEntries.copy(
                bankAccountNumber = intent.value
            )
            currentState.copy(
                bankEntries = updatedBankEntries,
                enableFinishBtn = finishBtnEnabledForBankDetails(updatedBankEntries)
            )
        }
    }

    private fun handleConfirmBankAccountNumberEntered(intent: CreatorDetailsIntent.ConfirmBankAccountNumberEnteredIntent) {
        updateState { currentState ->
            val updatedBankEntries = currentState.bankEntries.copy(
                confirmBankAccountNumber = intent.value
            )
            currentState.copy(
                bankEntries = updatedBankEntries,
                enableFinishBtn = finishBtnEnabledForBankDetails(updatedBankEntries)
            )
        }
    }

    private fun handleBankNameEntered(intent: CreatorDetailsIntent.BankNameEnteredIntent) {
        updateState { currentState ->
            val updatedBankEntries = currentState.bankEntries.copy(
                bankName = intent.value
            )
            currentState.copy(
                bankEntries = updatedBankEntries,
                enableFinishBtn = finishBtnEnabledForBankDetails(updatedBankEntries)
            )
        }
    }

    private fun handleIfscCodeEntered(intent: CreatorDetailsIntent.IfscCodeEnteredIntent) {
        updateState { currentState ->
            val updatedBankEntries = currentState.bankEntries.copy(
                ifscCode = intent.value.uppercase()
            )
            currentState.copy(
                bankEntries = updatedBankEntries,
                enableFinishBtn = finishBtnEnabledForBankDetails(updatedBankEntries)
            )
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