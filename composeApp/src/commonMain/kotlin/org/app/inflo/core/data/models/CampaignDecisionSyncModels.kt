package org.app.inflo.core.data.models

import kotlinx.serialization.Serializable
import org.app.inflo.db.CampaignActionType

@Serializable
data class CampaignDecisionDto(
    val userId: String,
    val campaignId: String,
    val action: String // "ACCEPT" or "DENY"
)

@Serializable
data class CampaignDecisionSyncRequest(
    val decisions: List<CampaignDecisionDto>
)

@Serializable
data class CampaignDecisionSyncResponse(
    val success: Boolean,
    val message: String? = null,
    val syncedCount: Int = 0
)

sealed class CampaignDecisionSyncResult {
    data class Success(val syncedCount: Int) : CampaignDecisionSyncResult()
    data class Failure(val error: String) : CampaignDecisionSyncResult()
} 