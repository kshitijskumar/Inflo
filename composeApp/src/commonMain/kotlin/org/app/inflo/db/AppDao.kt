package org.app.inflo.db

interface AppDao {
	// Inserts or updates a decision for a (userId, campaignId)
	suspend fun upsertDecision(
		userId: String,
		campaignId: String,
		action: CampaignActionType,
		updatedAt: Long
	)

	// Returns pending decisions up to a limit, ordered by updatedAt ASC
	suspend fun selectPending(limit: Long): List<Campaign_decisions>

	// Deletes a decision by composite key (after successful sync)
	suspend fun deleteDecision(userId: String, campaignId: String)

	// Marks a decision as failed with error and increments attempt count
	suspend fun markFailed(userId: String, campaignId: String, lastError: String?)
} 