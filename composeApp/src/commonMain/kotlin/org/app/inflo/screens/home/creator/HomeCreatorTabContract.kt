package org.app.inflo.screens.home.creator

import org.app.inflo.core.data.models.CampaignDisplayDataAppModel
import org.app.inflo.core.data.models.CampaignAdditionalQuestionAppModel
import org.app.inflo.core.data.models.UserAppModel

data class HomeCreatorTabState(
    val user: UserAppModel.Creator? = null,
    val campaigns: List<CampaignDisplayDataAppModel>? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val bottomSheet: HomeCreatorTabBottomSheet? = null,
)

sealed class HomeCreatorTabIntent {
    data object InitialisationIntent : HomeCreatorTabIntent()

    // Accept/Deny flow
    data class CampaignAcceptedIntent(val campaignId: String) : HomeCreatorTabIntent()
    data class CampaignDeniedIntent(val campaignId: String) : HomeCreatorTabIntent()

    // Extra questions bottom sheet flow
    data class OnQuestionAnsweredIntent(
        val question: CampaignAdditionalQuestionAppModel,
        val answer: String
    ) : HomeCreatorTabIntent()
    data class ExtraQuestionsContinueClicked(val campaignId: String) : HomeCreatorTabIntent()
    data object BottomSheetDismissed : HomeCreatorTabIntent()

    // External actions
    data class OpenInstagramIntent(val username: String) : HomeCreatorTabIntent()
    data class OpenUrlIntent(val url: String) : HomeCreatorTabIntent()
}

sealed class HomeCreatorTabBottomSheet {
    data class ExtraQuestions(
        val campaignId: String,
        val questions: List<CampaignAdditionalQuestionAppModel>,
        val enableContinueBtn: Boolean
    ) : HomeCreatorTabBottomSheet()
}

sealed class HomeCreatorTabEffect {

}