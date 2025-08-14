package org.app.inflo.screens.home.creator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import org.app.inflo.core.domain.CampaignFeedManager
import org.app.inflo.core.viewmodel.AppBaseViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeCreatorTabViewModel(
    private val feedManager: CampaignFeedManager
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
            is HomeCreatorTabIntent.CampaignAcceptedIntent -> handleCampaignAccepted(intent.campaignId)
            is HomeCreatorTabIntent.CampaignDeniedIntent -> handleCampaignDenied(intent.campaignId)
        }
    }

    private fun handleInitialisationIntent() {
        if (isInitialisationHandled) return
        isInitialisationHandled = true

        // Ensure scope lifecycle matches view-state collection
        initializeCoroutineScopeWhileVsCollected()

        // Observe feed status and reflect it in view state only when screen is active
        viewModelScope.launch {
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
            }
        }

        // Kick off an initial fresh fetch
        feedManager.fetch(scope = viewModelScope, shouldFetchFresh = true)
    }

    private fun handleCampaignAccepted(campaignId: String) {
        handledIds.update { it + campaignId }
        // TODO: persist acceptance and/or call backend API when available
    }

    private fun handleCampaignDenied(campaignId: String) {
        handledIds.update { it + campaignId }
        // TODO: persist denial and/or call backend API when available
    }
}