package org.app.inflo.db

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.app.inflo.screens.home.creator.domain.ExtraQuestionAnswer

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
				action = action,
				extraQuestionAnswers = null
			)
		}
	}

	override suspend fun upsertDecisionWithAnswers(
		userId: String,
		campaignId: String,
		action: CampaignActionType,
		extraQuestionAnswers: List<ExtraQuestionAnswer>?
	) {
		withContext(ioDispatcher) {
			val answersJson = extraQuestionAnswers?.let { answers ->
				Json.encodeToString(answers)
			}
			
			queries.upsertDecisionWithAnswers(
				userId = userId,
				campaignId = campaignId,
				action = action,
				extraQuestionAnswers = answersJson
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