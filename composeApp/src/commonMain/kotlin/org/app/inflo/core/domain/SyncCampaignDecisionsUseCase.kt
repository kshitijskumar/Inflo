package org.app.inflo.core.domain

import kotlinx.coroutines.flow.firstOrNull
import org.app.inflo.core.data.models.CampaignDecisionDto
import org.app.inflo.core.data.models.CampaignDecisionSyncResult
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.db.Campaign_decisions

class SyncCampaignDecisionsUseCase(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(): CampaignDecisionSyncResult {
        return try {
            // Fetch all pending decisions
            val userId = appRepository.loggedInUser().firstOrNull()?.id ?: return CampaignDecisionSyncResult.Failure("")
            val pendingDecisions = appRepository.allPendingDecisionsForUser(userId).firstOrNull() ?: listOf()
            
            if (pendingDecisions.isEmpty()) {
                return CampaignDecisionSyncResult.Success(syncedCount = 0)
            }

            // Convert to DTOs for API
            val decisionDtos = pendingDecisions.map { decision ->
                CampaignDecisionDto(
                    userId = decision.userId,
                    campaignId = decision.campaignId,
                    action = decision.action.name
                )
            }

            // Call sync API
            val response = appRepository.syncCampaignDecisions(decisionDtos)

            // Handle response
            when {
                response.success -> {
                    // All successful - delete all from DB
                    appRepository.deleteCampaignDecision(userId, pendingDecisions.map { it.campaignId })
                    CampaignDecisionSyncResult.Success(response.syncedCount)
                }
                else -> {
                    // Complete failure - keep all in DB
                    CampaignDecisionSyncResult.Failure(response.message ?: "Sync failed")
                }
            }
        } catch (e: Exception) {
            CampaignDecisionSyncResult.Failure(e.message ?: "Unknown error occurred")
        }
    }
} 