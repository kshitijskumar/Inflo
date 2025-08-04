package org.app.inflo.screens.onboarding.domain

import org.app.inflo.core.data.models.ContentCategory
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.screens.onboarding.OnboardingDetailsInfo

class GetOnboardingDetailsUseCase {

    operator fun invoke(
        onboardedUser: OnboardedUser
    ): List<OnboardingDetailsInfo> {
        return when (onboardedUser) {
            is OnboardedUser.Creator -> creatorOnboardingDetails(onboardedUser)
            is OnboardedUser.Brand -> brandOnboardingDetails(onboardedUser)
        }
    }

    private fun creatorOnboardingDetails(user: OnboardedUser.Creator): List<OnboardingDetailsInfo> {
        return listOf(
            OnboardingDetailsInfo.BasicUserDetails,
            OnboardingDetailsInfo.Categories(contentCategoriesList())
        )
    }

    private fun brandOnboardingDetails(user: OnboardedUser.Brand): List<OnboardingDetailsInfo> {
        return listOf(OnboardingDetailsInfo.BasicBrandDetails)
    }

    private fun contentCategoriesList(): List<ContentCategory> {
        return listOf(
            ContentCategory("beauty", "Beauty"),
            ContentCategory("fitness", "Fitness"),
            ContentCategory("tech", "Tech"),
            ContentCategory("automobile", "Automobile"),
            ContentCategory("travel", "Travel"),
        )
    }

}