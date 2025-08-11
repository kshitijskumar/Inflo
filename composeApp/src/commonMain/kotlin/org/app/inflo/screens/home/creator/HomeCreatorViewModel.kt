package org.app.inflo.screens.home.creator

import org.app.inflo.core.viewmodel.AppBaseViewModel
import org.app.inflo.navigation.args.HomeArgs

class HomeCreatorViewModel(
    private val args: HomeArgs.CreatorHomeArgs,
) : AppBaseViewModel<HomeCreatorIntent, HomeCreatorState, HomeCreatorEffect>() {

    override fun initialViewState(): HomeCreatorState {
        return HomeCreatorState()
    }

    override fun processIntent(intent: HomeCreatorIntent) {
        super.processIntent(intent)
        when(intent) {
            HomeCreatorIntent.InitialisationIntent -> handleInitialisationIntent()
            is HomeCreatorIntent.TabSelectedIntent -> handleTabSelectedIntent(intent)
        }
    }

    private fun handleInitialisationIntent() {

    }

    private fun handleTabSelectedIntent(intent: HomeCreatorIntent.TabSelectedIntent) {
        updateState {
            it.copy(selectedTab = intent.tab)
        }
    }
}