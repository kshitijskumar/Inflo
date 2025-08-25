package org.app.inflo.screens.home.creator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import org.app.inflo.core.data.models.CampaignDisplayDataAppModel
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.core.ui.AppPrimaryButton
import org.app.inflo.core.ui.AppSecondaryButton
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeCreatorTabScreen(
    vm: HomeCreatorTabViewModel,
    modifier: Modifier = Modifier
) {

    val state by vm.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(vm) {
        vm.processIntent(HomeCreatorTabIntent.InitialisationIntent)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                Text("Loading")
            }
            state.error != null -> {
                Text("Error: ${state.error}")
            }
            state.campaigns != null -> {
                HomeCreatorTabCampaignsSection(
                    campaigns = state.campaigns ?: listOf(),
                    sendIntent = vm::processIntent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

}

@Composable
fun HomeCreatorTabCampaignsSection(
    campaigns: List<CampaignDisplayDataAppModel>,
    sendIntent: (HomeCreatorTabIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (campaigns.isEmpty()) {
            Text("All done")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = AppTheme.dimens.medium3,
                        start = AppTheme.dimens.medium3,
                        end = AppTheme.dimens.medium3,
                        bottom = AppTheme.dimens.medium1
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                campaigns.getOrNull(0)?.let { campaign ->
                    CampaignCard(
                        campaign = campaign,
                        sendIntent = sendIntent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppSecondaryButton(
                        text = "No",
                        onClick = {
                            val campaign = campaigns.getOrNull(0)
                            campaign?.let {
                                sendIntent.invoke(HomeCreatorTabIntent.CampaignDeniedIntent(campaign.campaignId))
                            }
                        }
                    )
                    AppPrimaryButton(
                        text = "Yes",
                        onClick = {
                            val campaign = campaigns.getOrNull(0)
                            campaign?.let {
                                sendIntent.invoke(HomeCreatorTabIntent.CampaignAcceptedIntent(campaign.campaignId))
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CampaignCard(
    campaign: CampaignDisplayDataAppModel,
    sendIntent: (HomeCreatorTabIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campaign Image 
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1/1.2f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFD3D3D3)),
                contentAlignment = Alignment.Center
            ) {
                // Debug: Print the URL being loaded
                println("ImgStuff: Loading image from URL: ${campaign.url}")
                
                // Test with a simple image URL first
                AsyncImage(
                    model = campaign.url,
                    contentDescription = campaign.brandName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Brand name overlay at the bottom
                Text(
                    text = campaign.brandName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppTheme.color.white,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(
                            vertical = AppTheme.dimens.medium3,
                            horizontal = AppTheme.dimens.medium3,
                        ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Company IG Button - Now clickable!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        sendIntent(HomeCreatorTabIntent.OpenInstagramIntent(campaign.brandInstagramAccount))
                    }
                    .background(
                        color = AppTheme.color.baseRed,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Company IG",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            RequirementDetailsSection(campaign)

            Spacer(modifier = Modifier.height(24.dp))

            // Additional Requirements Section
            if (!campaign.additionalRequirements.isNullOrBlank()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Additional Requirements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = campaign.additionalRequirements,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Brand Brief Section - Now clickable!
            if (!campaign.campaignBriefUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            sendIntent(HomeCreatorTabIntent.OpenUrlIntent(campaign.campaignBriefUrl))
                        }
                        .background(
                            color = AppTheme.color.secondaryRed,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Brand Brief",
                            color = AppTheme.color.baseRed,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequirementDetailsSection(
    campaign: CampaignDisplayDataAppModel,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (campaign.requirements.reels != null) {
            RequirementRow("${campaign.requirements.reels} Reels")
        }
        if (campaign.requirements.posts != null) {
            RequirementRow("${campaign.requirements.posts} Posts")
        }
        if (campaign.requirements.stories != null) {
            RequirementRow("${campaign.requirements.stories} Stories")
        }
        RequirementRow("Repost rights: ${if (campaign.repostRights) "Yes" else "No"}")

    }
}

@Composable
fun RequirementRow(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = AppTheme.color.secondaryRed,
                shape = CircleShape
            )
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp,
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
    }
}