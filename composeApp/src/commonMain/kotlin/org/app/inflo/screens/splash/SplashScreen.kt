package org.app.inflo.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import inflo.composeapp.generated.resources.Res
import inflo.composeapp.generated.resources.ic_inflo
import inflo.composeapp.generated.resources.ic_inflo_secondary
import inflo.composeapp.generated.resources.profile_selection_subtitle
import inflo.composeapp.generated.resources.profile_type_creator
import inflo.composeapp.generated.resources.profile_type_brand
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.core.ui.AppPrimaryButton
import org.app.inflo.core.ui.AppSecondaryButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    
    val state by viewModel.viewState.collectAsState()
    
    // Handle initialization
    LaunchedEffect(Unit) {
        viewModel.processIntent(SplashIntent.InitialisationIntent)
    }
    
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        when (state.screenType) {
            SplashScreenType.LOADING -> {
                SplashLoading()
            }
            SplashScreenType.PROFILE_TYPE_SELECTION -> {
                ProfileTypeSelectionContent(
                    onProfileSelected = { profileType ->
                        viewModel.processIntent(SplashIntent.ProfileTypeSelectedIntent(profileType))
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileTypeSelectionContent(
    onProfileSelected: (ProfileType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.white)
            .padding(horizontal = AppTheme.dimens.medium3),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(96.dp))

            Image(
                painter = painterResource(Res.drawable.ic_inflo_secondary),
                modifier = Modifier
                    .fillMaxWidth(0.75f),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )

            Text(
                text = stringResource(Res.string.profile_selection_subtitle),
                color = AppTheme.color.baseRed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimens.medium3),
                textAlign = TextAlign.Center
            )
        }

        AppPrimaryButton(
            text = stringResource(Res.string.profile_type_creator),
            onClick = { onProfileSelected.invoke(ProfileType.CREATOR) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(AppTheme.dimens.medium1))

        AppSecondaryButton(
            text = stringResource(Res.string.profile_type_brand),
            onClick = { onProfileSelected.invoke(ProfileType.BRAND) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(AppTheme.dimens.medium4))
    }
}

@Composable
private fun SplashLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.baseRed)
            .padding(horizontal = AppTheme.dimens.medium3),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_inflo),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}