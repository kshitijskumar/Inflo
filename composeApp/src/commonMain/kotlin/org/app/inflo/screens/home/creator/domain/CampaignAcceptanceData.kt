package org.app.inflo.screens.home.creator.domain

import kotlinx.serialization.Serializable
import org.app.inflo.db.CampaignActionType

data class CampaignAcceptanceData(
    val campaignId: String,
    val extraQuestionAnswers: List<ExtraQuestionAnswer>?,
    val actionType: CampaignActionType
)

@Serializable
data class ExtraQuestionAnswer(
    val question: String,
    val answer: String
)

data class CampaignExtraQuestion(
    val question: String,
    val isRequired: Boolean = true
) 