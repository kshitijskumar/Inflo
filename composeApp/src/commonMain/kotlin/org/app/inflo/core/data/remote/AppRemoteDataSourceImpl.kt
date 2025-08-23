package org.app.inflo.core.data.remote

import kotlinx.coroutines.delay
import org.app.inflo.core.data.models.CampaignDisplayDataApiModel
import org.app.inflo.core.data.models.CampaignFetchResponseApiModel
import org.app.inflo.core.data.models.CampaignRequirements
import org.app.inflo.core.data.models.ContentCategory
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.ProfileVerificationStatus
import org.app.inflo.screens.login.domain.RequestOtpRequestApiModel
import org.app.inflo.screens.login.domain.RequestOtpResponseApiModel
import org.app.inflo.screens.login.domain.VerifyLoginRequestApiModel
import org.app.inflo.screens.login.domain.VerifyLoginResponseApiModel
import org.app.inflo.screens.login.exception.RequestOtpFailedException
import org.app.inflo.screens.login.exception.VerifyLoginFailedException
import org.app.inflo.screens.splash.ProfileType
import org.app.inflo.utils.AppSystem
import org.app.inflo.utils.ServerErrorCodes

class AppRemoteDataSourceImpl : AppRemoteDataSource {
    
    override suspend fun requestOtp(request: RequestOtpRequestApiModel): RequestOtpResponseApiModel {
        // Simulate network delay
        delay(1000)
        
        // Mock logic: Simulate success for valid phone numbers, failure for specific cases
        when {
            request.number.startsWith("9999") -> {
                throw RequestOtpFailedException("Server error: Invalid phone number")
            }
            request.number.startsWith("0000") -> {
                throw RequestOtpFailedException("Network error: Unable to send OTP")
            }
            request.number.length != 10 -> {
                throw RequestOtpFailedException("Validation error: Invalid phone number format")
            }
            else -> {
                // Success case - return mock OTP code
                return RequestOtpResponseApiModel(
                    code = "123456"
                )
            }
        }
    }
    
    override suspend fun verifyLogin(request: VerifyLoginRequestApiModel): VerifyLoginResponseApiModel {
        // Simulate network delay
        delay(1500)
        
        // Mock logic for different scenarios
        when {
            request.code == "000000" -> {
                throw VerifyLoginFailedException(ServerErrorCodes.LOGIN_INVALID_OTP, "Invalid OTP entered")
            }
            request.code == "111111" -> {
                throw VerifyLoginFailedException(ServerErrorCodes.LOGIN_OTP_EXPIRED, "OTP has expired")
            }
            request.code == "999999" -> {
                throw VerifyLoginFailedException(0, "Server error occurred")
            }
            request.phoneNumber.startsWith("8888") -> {
                // Mock existing user
                return VerifyLoginResponseApiModel(
                    profileType = ProfileType.CREATOR.name,
                    id = "user_123",
                    mobileNumber = request.phoneNumber,
                    firstName = "John",
                    lastName = "Doe",
                    dob = AppSystem.currentTimeInMillis() - (25 * 365 * 24 * 60 * 60 * 1000L), // 25 years ago
                    brandName = null,
                    instagramAccountName = null,
                    categories = listOf(
                        ContentCategory("beauty", "Beauty"),
                        ContentCategory("fitness", "Fitness"),
                        ContentCategory("automobile", "Automobile"),
                    ),
                    profileVerificationStatus = ProfileVerificationStatus.VERIFIED.name
                )
            }
            else -> {
                // Mock new user (minimal details)
                return VerifyLoginResponseApiModel(
                    profileType = ProfileType.CREATOR.name,
                    id = "new_user_${AppSystem.currentTimeInMillis()}",
                    mobileNumber = request.phoneNumber,
                    firstName = null,
                    lastName = null,
                    dob = null,
                    brandName = null,
                    instagramAccountName = null,
                    categories = null,
                    profileVerificationStatus = ProfileVerificationStatus.VERIFICATION_PENDING.name
                )
            }
        }
    }

    override suspend fun finishOnboarding(onboardedUser: OnboardedUser): VerifyLoginResponseApiModel {
        delay(1500)
        return when(onboardedUser) {
            is OnboardedUser.Brand -> {
                VerifyLoginResponseApiModel(
                    id = onboardedUser.id,
                    firstName = onboardedUser.firstName ?: "",
                    lastName = onboardedUser.lastName ?: "",
                    mobileNumber = onboardedUser.mobileNumber,
                    dob = -1L,
                    categories = null,
                    profileVerificationStatus = ProfileVerificationStatus.VERIFICATION_PENDING.name,
                    profileType = ProfileType.CREATOR.name,
                    instagramAccountName = null,
                    brandName = null
                )
            }
            is OnboardedUser.Creator -> {
                VerifyLoginResponseApiModel(
                    id = onboardedUser.id,
                    firstName = onboardedUser.firstName ?: "",
                    lastName = onboardedUser.lastName ?: "",
                    mobileNumber = onboardedUser.mobileNumber,
                    dob = onboardedUser.dob ?: -1L,
                    categories = onboardedUser.categories ?: listOf(),
                    profileVerificationStatus = ProfileVerificationStatus.VERIFICATION_PENDING.name,
                    profileType = ProfileType.CREATOR.name,
                    instagramAccountName = null,
                    brandName = null
                )
            }
        }
    }

    private var counter = 0
    override suspend fun updateUserVerificationStatus(onboardedUser: OnboardedUser): VerifyLoginResponseApiModel {
        delay(1500)
        counter++
        return when(onboardedUser) {
            is OnboardedUser.Brand -> {
                VerifyLoginResponseApiModel(
                    id = onboardedUser.id,
                    firstName = onboardedUser.firstName ?: "",
                    lastName = onboardedUser.lastName ?: "",
                    mobileNumber = onboardedUser.mobileNumber,
                    dob = -1L,
                    categories = null,
                    profileVerificationStatus = null,
                    profileType = ProfileType.BRAND.name,
                    instagramAccountName = null,
                    brandName = onboardedUser.brandName
                )
            }
            is OnboardedUser.Creator -> {
                VerifyLoginResponseApiModel(
                    id = onboardedUser.id,
                    firstName = onboardedUser.firstName ?: "",
                    lastName = onboardedUser.lastName ?: "",
                    mobileNumber = onboardedUser.mobileNumber,
                    dob = onboardedUser.dob ?: -1L,
                    categories = onboardedUser.categories ?: listOf(),
                    profileVerificationStatus = if (counter > 3) ProfileVerificationStatus.VERIFIED.name else ProfileVerificationStatus.VERIFICATION_PENDING.name,
                    profileType = ProfileType.CREATOR.name,
                    instagramAccountName = null,
                    brandName = null
                )
            }
        }
    }
    
    override suspend fun fetchCampaignFeed(creatorId: String, page: Int): CampaignFetchResponseApiModel {
        delay(1000) // Simulate network delay
        
        // Mock data generation based on page
        val baseIndex = page * 10
        val campaigns = mutableListOf<CampaignDisplayDataApiModel>()
        
        repeat(10) { index ->
            val campaignIndex = baseIndex + index + 1
            campaigns.add(
                CampaignDisplayDataApiModel(
                    campaignId = "campaign_$campaignIndex",
                    campaignName = "Campaign $campaignIndex - ${getMockCampaignNames()[index % getMockCampaignNames().size]}",
                    url = "https://example.com/campaign/$campaignIndex",
                    brandName = "Brand name: $campaignIndex",
                    brandId = "brand_${(campaignIndex % 5) + 1}",
                    brandInstagramAccount = "@brand${(campaignIndex % 5) + 1}",
                    requirements = CampaignRequirements(
                        reels = if (campaignIndex % 3 == 0) null else (campaignIndex % 3) + 1,
                        stories = if (campaignIndex % 4 == 0) null else (campaignIndex % 4) + 1,
                        posts = if (campaignIndex % 2 == 0) null else (campaignIndex % 2) + 1
                    ),
                    repostRights = campaignIndex % 3 == 0,
                    categories = getMockCategories().take((campaignIndex % 3) + 1),
                    additionalRequirements = if (campaignIndex % 4 == 0) "Must tag @brand${(campaignIndex % 5) + 1} in caption" else null,
                    campaignBriefUrl = if (campaignIndex % 3 == 0) "https://example.com/brief/$campaignIndex" else null
                )
            )
        }
        
        return CampaignFetchResponseApiModel(
            data = campaigns,
            currentPage = 0,
            isLastPage = true
        )
    }
    
    private fun getMockCampaignNames(): List<String> {
        return listOf(
            "Summer Collection Launch",
            "Fitness Challenge 2024",
            "Beauty Essentials Review",
            "Tech Gadget Showcase",
            "Travel Adventure Story",
            "Sustainable Fashion",
            "Gaming Setup Tour",
            "Food Recipe Challenge",
            "Art & Creativity",
            "Lifestyle Transformation"
        )
    }
    
    private fun getMockCategories(): List<ContentCategory> {
        return listOf(
            ContentCategory("beauty", "Beauty"),
            ContentCategory("fitness", "Fitness"),
            ContentCategory("tech", "Tech"),
            ContentCategory("automobile", "Automobile"),
            ContentCategory("travel", "Travel"),
            ContentCategory("anime", "Anime"),
            ContentCategory("education", "Education"),
            ContentCategory("art", "Art"),
            ContentCategory("food", "Food"),
            ContentCategory("gaming", "Gaming")
        )
    }
} 