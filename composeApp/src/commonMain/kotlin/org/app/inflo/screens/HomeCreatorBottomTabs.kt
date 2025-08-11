package org.app.inflo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import inflo.composeapp.generated.resources.Res
import inflo.composeapp.generated.resources.ic_campaign_tab
import inflo.composeapp.generated.resources.ic_home_tab
import inflo.composeapp.generated.resources.ic_profile_tab
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.screens.home.HomeBottomTab
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeCreatorBottomTabs(
    selectedTab: HomeBottomTab,
    onTabSelected: (HomeBottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(AppTheme.color.white)
            .padding(horizontal = AppTheme.dimens.medium4, vertical = AppTheme.dimens.medium2),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomTabItem(
            isSelected = selectedTab == HomeBottomTab.HOME,
            iconRes = Res.drawable.ic_home_tab,
            label = "Home",
            onClick = { onTabSelected(HomeBottomTab.HOME) },
            modifier = Modifier.weight(1f)
        )
        BottomTabItem(
            isSelected = selectedTab == HomeBottomTab.CAMPAIGN,
            iconRes = Res.drawable.ic_campaign_tab,
            label = "Campaigns",
            onClick = { onTabSelected(HomeBottomTab.CAMPAIGN) },
            modifier = Modifier.weight(1f)
        )
        BottomTabItem(
            isSelected = selectedTab == HomeBottomTab.PROFILE,
            iconRes = Res.drawable.ic_profile_tab,
            label = "Profile",
            onClick = { onTabSelected(HomeBottomTab.PROFILE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomTabItem(
    isSelected: Boolean,
    iconRes: DrawableResource,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tintColor: Color = if (isSelected) AppTheme.color.baseRed else AppTheme.color.black60

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(AppTheme.dimens.small2))
        Text(
            text = label,
            color = tintColor,
            style = AppTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}