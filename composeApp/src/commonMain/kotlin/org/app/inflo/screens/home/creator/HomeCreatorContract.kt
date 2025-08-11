package org.app.inflo.screens.home.creator

import org.app.inflo.screens.home.HomeBottomTab

data class HomeCreatorState(
    val selectedTab: HomeBottomTab = HomeBottomTab.HOME
)

sealed class HomeCreatorIntent {

    data object InitialisationIntent : HomeCreatorIntent()

    data class TabSelectedIntent(val tab: HomeBottomTab) : HomeCreatorIntent()

}

sealed class HomeCreatorEffect