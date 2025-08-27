package org.app.inflo.screens.home.creator.domain

import kotlinx.coroutines.flow.firstOrNull
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.db.CampaignActionType

class RecordCampaignAcceptanceWithExtraQuestionsUseCase(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(acceptanceData: CampaignAcceptanceData) {
        val userId = (appRepository.loggedInUser().firstOrNull() as? UserAppModel.Creator)?.id ?: return
        
        // Record the campaign decision with optional extra question answers
        appRepository.recordCampaignDecision(
            userId = userId,
            campaignId = acceptanceData.campaignId,
            action = acceptanceData.actionType,
            extraQuestionAnswers = acceptanceData.extraQuestionAnswers?.ifEmpty { null }
        )
    }
} 