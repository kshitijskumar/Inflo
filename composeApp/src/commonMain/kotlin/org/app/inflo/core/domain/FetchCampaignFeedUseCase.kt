package org.app.inflo.core.domain

import org.app.inflo.core.data.models.CampaignDisplayDataAppModel
import org.app.inflo.core.data.models.CampaignFetchResponseAppModel
import org.app.inflo.core.data.models.toAppModelOrNull
import org.app.inflo.core.data.repository.AppRepository

class FetchCampaignFeedUseCase(
    private val repository: AppRepository
) {
    
    suspend operator fun invoke(creatorId: String, page: Int): CampaignFeedResult {
        return try {
            val response = repository.fetchCampaignFeed(creatorId, page)
            CampaignFeedResult.Success(response)
        } catch (e: Exception) {
            CampaignFeedResult.Error(e.message ?: "Unknown error occurred")
        }
    }
}

sealed class CampaignFeedResult {
    data class Success(val data: CampaignFetchResponseAppModel) : CampaignFeedResult()
    data class Error(val message: String) : CampaignFeedResult()
} 