package org.app.inflo.screens.home.creator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.app.inflo.core.data.models.CampaignDisplayDataAppModel
import org.app.inflo.core.ui.AppPrimaryButton
import org.app.inflo.core.ui.AppSecondaryButton

@Composable
fun HomeCreatorTabScreen(
    vm: HomeCreatorTabViewModel,
    modifier: Modifier = Modifier
) {

    val state by vm.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.processIntent(HomeCreatorTabIntent.InitialisationIntent)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                Text("Loading")
            }
            state.error != null -> {
                Text("Error: ${state.error}")
            }
            state.campaigns != null -> {
                HomeCreatorTabCampaignsSection(
                    campaigns = state.campaigns ?: listOf(),
                    sendIntent = vm::processIntent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

}

@Composable
fun HomeCreatorTabCampaignsSection(
    campaigns: List<CampaignDisplayDataAppModel>,
    sendIntent: (HomeCreatorTabIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (campaigns.isEmpty()) {
            Text("All done")
        } else {

            campaigns.getOrNull(0)?.let { campaign ->
                Text("Campaign: $campaign")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppSecondaryButton(text = "No", {})
                AppPrimaryButton(text = "Yes", {})
            }
        }
    }
}