package org.app.inflo.screens.home.creator.domain

import kotlinx.coroutines.flow.firstOrNull
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.db.CampaignActionType
import org.app.inflo.utils.AppSystem

class RecordCampaignDecisionUseCase(
	private val appRepository: AppRepository
) {
	suspend operator fun invoke(campaignId: String, action: CampaignActionType) {
		val userId = (appRepository.loggedInUser().firstOrNull() as? UserAppModel.Creator)?.id ?: return
		val now = AppSystem.currentTimeInMillis()
		appRepository.recordCampaignDecision(
			userId = userId,
			campaignId = campaignId,
			action = action,
			updatedAt = now
		)
	}
} 