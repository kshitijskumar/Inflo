package org.app.inflo.screens.home.creator

import org.app.inflo.core.data.models.CampaignDisplayDataAppModel
import org.app.inflo.core.data.models.UserAppModel

data class HomeCreatorTabState(
    val user: UserAppModel.Creator? = null,
    val campaigns: List<CampaignDisplayDataAppModel>? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class HomeCreatorTabIntent {
    data object InitialisationIntent : HomeCreatorTabIntent()
    data class CampaignAcceptedIntent(val campaignId: String) : HomeCreatorTabIntent()
    data class CampaignDeniedIntent(val campaignId: String) : HomeCreatorTabIntent()
}

sealed class HomeCreatorTabEffect {

}