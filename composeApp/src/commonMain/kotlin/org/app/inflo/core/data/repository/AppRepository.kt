package org.app.inflo.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.app.inflo.core.data.models.CampaignDisplayDataApiModel
import org.app.inflo.core.data.models.CampaignDisplayDataAppModel
import org.app.inflo.core.data.models.CampaignFetchResponseAppModel
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.screens.home.creator.domain.ExtraQuestionAnswer
import org.app.inflo.screens.login.domain.RequestOtpRequestApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseAppModel
import org.app.inflo.screens.login.domain.VerifyLoginRequestApiModel
import org.app.inflo.screens.login.domain.VerifyLoginResponseApiModel
import org.app.inflo.db.CampaignActionType
import org.app.inflo.core.data.models.CampaignDecisionDto
import org.app.inflo.core.data.models.CampaignDecisionSyncResponse
import org.app.inflo.db.Campaign_decisions

interface AppRepository {

    fun loggedInUser(): Flow<UserAppModel?>
    
    fun onboardedUser(): Flow<OnboardedUser?>
    
    suspend fun requestOtp(request: RequestOtpRequestApiModel): RequestOtpResponseApiModel
    
    suspend fun requestOtpAppModel(request: RequestOtpRequestApiModel): RequestOtpResponseAppModel
    
    suspend fun verifyLogin(request: VerifyLoginRequestApiModel): VerifyLoginResponseApiModel
    
    suspend fun storeUser(user: UserAppModel)
    
    suspend fun storeOnboardedUser(onboardedUser: OnboardedUser)
    
    suspend fun finishOnboarding(onboardedUser: OnboardedUser): VerifyLoginResponseApiModel

    suspend fun updateUserVerificationStatus(onboardedUser: OnboardedUser): VerifyLoginResponseApiModel

    suspend fun fetchCampaignFeed(creatorId: String, page: Int): CampaignFetchResponseAppModel

    suspend fun clearAllUserData()

    // Campaign decisions
    suspend fun recordCampaignDecision(
        userId: String,
        campaignId: String,
        action: CampaignActionType,
        extraQuestionAnswers: List<ExtraQuestionAnswer>? = null
    )

    suspend fun allPendingDecisionsForUser(userId: String): Flow<List<Campaign_decisions>>
    
    suspend fun deleteCampaignDecision(userId: String, campaignId: String)

    suspend fun deleteCampaignDecision(userId: String, campaignsList: List<String>)

    suspend fun syncCampaignDecisions(decisions: List<CampaignDecisionDto>): CampaignDecisionSyncResponse
}