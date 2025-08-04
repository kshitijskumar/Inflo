package org.app.inflo.screens.onboarding

import org.app.inflo.core.data.models.ContentCategory
import org.app.inflo.core.data.models.OnboardedUser

sealed class OnboardingDetailsInfo {

    data object BasicUserDetails : OnboardingDetailsInfo()

    data class Categories(
        val categories: List<ContentCategory>
    ) : OnboardingDetailsInfo()

    data object BasicBrandDetails : OnboardingDetailsInfo()

}

val OnboardingDetailsInfo.priorityOrder: Int
    get() {
        return when(this) {
            OnboardingDetailsInfo.BasicBrandDetails -> 1
            OnboardingDetailsInfo.BasicUserDetails -> 1
            is OnboardingDetailsInfo.Categories -> 2
        }
    }

fun List<OnboardingDetailsInfo>.sortByPriority(): List<OnboardingDetailsInfo> {
    return this.sortedBy { it.priorityOrder }
}

fun OnboardingDetailsInfo.isDetailsFilled(user: OnboardedUser): Boolean {
    return when(this) {
        OnboardingDetailsInfo.BasicUserDetails -> {
            when(user) {
                is OnboardedUser.Brand -> true // consider this filled for a brand
                is OnboardedUser.Creator -> {
                    with(user) {
                        !this.firstName.isNullOrEmpty() && !this.lastName.isNullOrEmpty() && this.dob != null
                    }
                }
            }
        }
        OnboardingDetailsInfo.BasicBrandDetails ->  {
            when(user) {
                is OnboardedUser.Brand -> {
                    false // TODO::KSHITIJ - come back for this when working on brand
                }
                is OnboardedUser.Creator -> true // consider this filled for a creator
            }
        }
        is OnboardingDetailsInfo.Categories -> {
            when(user) {
                is OnboardedUser.Brand -> true
                is OnboardedUser.Creator -> {
                    val categorySize = user.categories?.size ?: 0
                    // minimum 1 required
                    categorySize >= 1
                }
            }
        }
    }
}