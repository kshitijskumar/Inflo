package org.app.inflo.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AppDaoImpl(
	private val queries: CampaignDecisionsQueries,
	private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : AppDao {
	override suspend fun upsertDecision(
		userId: String,
		campaignId: String,
		action: CampaignActionType
	) {
		withContext(ioDispatcher) {
			queries.upsertDecision(
				userId = userId,
				campaignId = campaignId,
				action = action
			)
		}
	}

	override suspend fun allPendingDecisions(
		userId: String,
		limit: Long
	): Flow<List<Campaign_decisions>> {
		return queries
			.selectPending(userId, limit)
			.asFlow()
			.mapToList(ioDispatcher)

	}

	override suspend fun deleteDecision(userId: String, campaignId: String) {
		withContext(ioDispatcher) {
			queries.deleteDecisions(userId, campaignId)
		}
	}

	override suspend fun deleteDecision(userId: String, campaignsList: List<String>) {
		withContext(ioDispatcher) {
			queries.transaction {
				campaignsList.forEach {
					queries.deleteDecisions(userId, it)
				}
			}
		}
	}
}