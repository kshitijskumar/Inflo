package org.app.inflo.db

import kotlinx.coroutines.flow.Flow
import org.app.inflo.screens.home.creator.domain.ExtraQuestionAnswer

interface AppDao {
	// Inserts or updates a decision for a (userId, campaignId)
	suspend fun upsertDecision(
		userId: String,
		campaignId: String,
		action: CampaignActionType
	)

	// Inserts or updates a decision with extra question answers
	suspend fun upsertDecisionWithAnswers(
		userId: String,
		campaignId: String,
		action: CampaignActionType,
		extraQuestionAnswers: List<ExtraQuestionAnswer>?
	)

	// Returns pending decisions up to a limit, ordered by rowid ASC
	suspend fun allPendingDecisions(
		userId: String,
		limit: Long
	): Flow<List<Campaign_decisions>>

	// Deletes a decision by composite key (after successful sync)
	suspend fun deleteDecision(userId: String, campaignId: String)

	suspend fun deleteDecision(userId: String, campaignsList: List<String>)
}