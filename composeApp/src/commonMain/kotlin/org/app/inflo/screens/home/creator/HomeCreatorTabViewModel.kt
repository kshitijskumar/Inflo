package org.app.inflo.screens.home.creator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import org.app.inflo.core.domain.CampaignFeedManager
import org.app.inflo.core.utils.UrlOpener
import org.app.inflo.core.viewmodel.AppBaseViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.app.inflo.db.CampaignActionType
import org.app.inflo.screens.home.creator.domain.RecordCampaignDecisionUseCase
import org.app.inflo.core.data.models.CampaignAdditionalQuestionAppModel
import org.app.inflo.screens.home.creator.domain.CampaignAcceptanceData
import org.app.inflo.screens.home.creator.domain.ExtraQuestionAnswer
import org.app.inflo.screens.home.creator.domain.RecordCampaignAcceptanceWithExtraQuestionsUseCase

class HomeCreatorTabViewModel(
    private val feedManager: CampaignFeedManager,
    private val recordCampaignAcceptanceWithExtraQuestionsUseCase: RecordCampaignAcceptanceWithExtraQuestionsUseCase,
    private val urlOpener: UrlOpener
) : AppBaseViewModel<HomeCreatorTabIntent, HomeCreatorTabState, HomeCreatorTabEffect>() {

    private var isInitialisationHandled: Boolean = false
    private val handledIds = MutableStateFlow(setOf<String>())

    override fun initialViewState(): HomeCreatorTabState {
        return HomeCreatorTabState()
    }

    override fun processIntent(intent: HomeCreatorTabIntent) {
        super.processIntent(intent)
        when(intent) {
            HomeCreatorTabIntent.InitialisationIntent -> handleInitialisationIntent()
            is HomeCreatorTabIntent.CampaignAcceptedIntent -> handleCampaignAcceptTapped(intent.campaignId)
            is HomeCreatorTabIntent.CampaignDeniedIntent -> handleCampaignDenied(intent.campaignId)
            is HomeCreatorTabIntent.OnQuestionAnsweredIntent -> handleQuestionAnswered(intent.question, intent.answer)
            is HomeCreatorTabIntent.ExtraQuestionsContinueClicked -> handleExtraQuestionsContinue(intent.campaignId)
            HomeCreatorTabIntent.BottomSheetDismissed -> clearBottomSheet()
            is HomeCreatorTabIntent.OpenInstagramIntent -> handleOpenInstagram(intent.username)
            is HomeCreatorTabIntent.OpenUrlIntent -> handleOpenUrl(intent.url)
        }
    }

    private fun handleInitialisationIntent() {
        if (isInitialisationHandled) return
        isInitialisationHandled = true

        // Ensure scope lifecycle matches view-state collection
        initializeCoroutineScopeWhileVsCollected()

        // Observe feed status and reflect it in view state only when screen is active
        combine(
            flow = feedManager.data,
            flow2 = handledIds
        ) { status, handledIds ->
            val filtered = status.data?.filterNot { handledIds.contains(it.campaignId) }
            updateState { current ->
                current.copy(
                    campaigns = filtered,
                    isLoading = status.isFetching,
                    error = status.error
                )
            }
        }.launchIn(viewModelScope)

        // Kick off an initial fresh fetch
        feedManager.fetch(scope = viewModelScope, shouldFetchFresh = true)
    }

    private fun handleCampaignAcceptTapped(campaignId: String) {
        val campaign = viewState.value.campaigns?.firstOrNull { it.campaignId == campaignId }
        if (campaign == null) return

        val extraQuestions = campaign.extraQuestions
        if (extraQuestions.isNullOrEmpty()) {
            // No extra questions: accept immediately
            handleCampaignAccepted(campaignId, null)
        } else {
            // Show bottom sheet with extra questions (answers will be empty strings initially)
            updateState {
                it.copy(
                    bottomSheet = HomeCreatorTabBottomSheet.ExtraQuestions(
                        campaignId = campaignId,
                        questions = extraQuestions,
                        enableContinueBtn = false
                    )
                )
            }
        }
    }

    private fun handleQuestionAnswered(question: CampaignAdditionalQuestionAppModel, answer: String) {
        val currentBottomSheet = viewState.value.bottomSheet
        if (currentBottomSheet !is HomeCreatorTabBottomSheet.ExtraQuestions) return

        val updatedQuestions = currentBottomSheet.questions.map { questionModel ->
            if (questionModel.question == question.question) {
                questionModel.copy(answer = answer)
            } else {
                questionModel
            }
        }

        updateState { 
            it.copy(
                bottomSheet = currentBottomSheet.copy(
                    questions = updatedQuestions,
                    enableContinueBtn = updatedQuestions.all { ans -> ans.answer.isNotBlank() }
                )
            )
        }
    }

    private fun handleExtraQuestionsContinue(campaignId: String) {
        // For now we don't collect answers. Just accept and clear bottom sheet.
        val bsType = viewState.value.bottomSheet as? HomeCreatorTabBottomSheet.ExtraQuestions ?: return
        clearBottomSheet()
        handleCampaignAccepted(campaignId, bsType.questions)
    }

    private fun clearBottomSheet() {
        updateState { it.copy(bottomSheet = null) }
    }

    private fun handleCampaignAccepted(
        campaignId: String,
        answers: List<CampaignAdditionalQuestionAppModel>?
    ) {
        handledIds.update { it + campaignId }
        viewModelScope.launch {
            recordCampaignAcceptanceWithExtraQuestionsUseCase.invoke(
                CampaignAcceptanceData(
                    campaignId = campaignId,
                    extraQuestionAnswers = answers?.map { ExtraQuestionAnswer(it.question, it.answer) },
                    actionType = CampaignActionType.ACCEPT
                )
            )
        }
    }

    private fun handleCampaignDenied(campaignId: String) {
        handledIds.update { it + campaignId }
        viewModelScope.launch {
            recordCampaignAcceptanceWithExtraQuestionsUseCase.invoke(
                CampaignAcceptanceData(
                    campaignId = campaignId,
                    extraQuestionAnswers = null,
                    actionType = CampaignActionType.DENY
                )
            )
        }
    }

    private fun handleOpenInstagram(username: String) {
        val success = urlOpener.openInstagram(username)
        if (!success) {
            // Could emit an effect here to show error message
            println("Failed to open Instagram for username: $username")
        }
    }

    private fun handleOpenUrl(url: String) {
        val success = urlOpener.openUrl(url)
        if (!success) {
            // Could emit an effect here to show error message
            println("Failed to open URL: $url")
        }
    }
}