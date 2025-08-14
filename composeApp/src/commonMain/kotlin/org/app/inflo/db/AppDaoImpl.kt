package org.app.inflo.db

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppDaoImpl(
	private val queries: CampaignDecisionsQueries,
	private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : AppDao {
	override suspend fun upsertDecision(
		userId: String,
		campaignId: String,
		action: CampaignActionType,
		updatedAt: Long
	) {
		withContext(ioDispatcher) {
			queries.upsertDecision(
				Campaign_decisions(
					userId = userId,
					campaignId = campaignId,
					action = action,
					updatedAt = updatedAt,
					attemptCount = 0,
					lastError = null,
					status = CampaignDecisionStatus.PENDING
				)
			)
		}
	}

	override suspend fun selectPending(limit: Long): List<Campaign_decisions> = withContext(ioDispatcher) {
		queries.selectPending(limit).executeAsList()
	}

	override suspend fun deleteDecision(userId: String, campaignId: String) {
		withContext(ioDispatcher) {
			queries.deleteDecisions(userId, campaignId)
		}
	}

	override suspend fun markFailed(userId: String, campaignId: String, lastError: String?) {
		withContext(ioDispatcher) {
			queries.markFailed(
				lastError = lastError,
				userId = userId,
				campaignId = campaignId
			)
		}
	}
} 