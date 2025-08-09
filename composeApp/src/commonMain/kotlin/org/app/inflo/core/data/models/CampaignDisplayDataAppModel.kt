package org.app.inflo.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CampaignFetchResponseApiModel(
    val data: List<CampaignDisplayDataApiModel>?,
    val currentPage: Int?,
    val isLastPage: Boolean?
)

@Serializable
data class CampaignDisplayDataApiModel(
    val campaignId: String?,
    val campaignName: String?,
    val url: String?,
    val brandId: String?,
    val brandInstagramAccount: String?,
    val requirements: CampaignRequirements?,
    val repostRights: Boolean?,
    val categories: List<ContentCategory>?,
    val additionalRequirements: String?,
    val campaignBriefUrl: String?,
)

data class CampaignFetchResponseAppModel(
    val data: List<CampaignDisplayDataAppModel>,
    val currentPage: Int,
    val isLastPage: Boolean
)

fun CampaignFetchResponseApiModel.toAppModel(): CampaignFetchResponseAppModel {
    return CampaignFetchResponseAppModel(
        data = data?.mapNotNull { it.toAppModelOrNull() } ?: listOf(),
        currentPage = currentPage ?: 0,
        isLastPage = isLastPage ?: true
    )
}

fun CampaignDisplayDataApiModel.toAppModelOrNull(): CampaignDisplayDataAppModel? {
    return CampaignDisplayDataAppModel(
        campaignId = campaignId ?: return null,
        campaignName = campaignName ?: return null,
        url = url ?: return null,
        brandId = brandId ?: return null,
        brandInstagramAccount = brandInstagramAccount ?: return null,
        requirements = requirements ?: return null,
        repostRights = repostRights ?: true,
        categories = categories,
        additionalRequirements = additionalRequirements,
        campaignBriefUrl = campaignBriefUrl,
    )
}

data class CampaignDisplayDataAppModel(
    val campaignId: String,
    val campaignName: String,
    val url: String,
    val brandId: String,
    val brandInstagramAccount: String,
    val requirements: CampaignRequirements,
    val repostRights: Boolean,
    val categories: List<ContentCategory>?,
    val additionalRequirements: String?,
    val campaignBriefUrl: String?,
)

@Serializable
data class CampaignRequirements(
    val reels: Int?,
    val stories: Int?,
    val posts: Int?
)