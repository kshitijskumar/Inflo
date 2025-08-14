package org.app.inflo.screens.home.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.core.ui.AppToolbar
import org.app.inflo.screens.HomeCreatorBottomTabs
import org.app.inflo.screens.home.HomeBottomTab

@Composable
fun HomeCreatorScreen(
    vm: HomeCreatorViewModel,
    homeCreatorVm: HomeCreatorTabViewModel,
) {

    val state by vm.viewState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.white)
    ) {
        AppToolbar(
            backIcon = null,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(AppTheme.color.background)
        ) {
            when(state.selectedTab) {
                HomeBottomTab.HOME -> {
                    HomeCreatorTabScreen(
                        vm = homeCreatorVm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                HomeBottomTab.CAMPAIGN,
                HomeBottomTab.PROFILE,
                HomeBottomTab.CREATE -> Text(state.selectedTab.name)
            }
        }

        HomeCreatorBottomTabs(
            selectedTab = state.selectedTab,
            onTabSelected = { vm.processIntent(HomeCreatorIntent.TabSelectedIntent(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = AppTheme.dimens.medium1,
                    top = AppTheme.dimens.small2
                )
        )
    }
}