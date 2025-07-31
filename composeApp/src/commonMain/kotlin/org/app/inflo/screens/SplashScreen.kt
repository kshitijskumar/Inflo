package org.app.inflo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import inflo.composeapp.generated.resources.Res
import inflo.composeapp.generated.resources.ic_inflo
import moe.tlaster.precompose.stateholder.LocalStateHolder
import org.app.inflo.core.theme.AppTheme
import org.app.inflo.core.viewmodel.ViewModelFactory
import org.app.inflo.screens.splash.ProfileType
import org.app.inflo.screens.splash.SplashIntent
import org.app.inflo.screens.splash.SplashScreenType
import org.app.inflo.screens.splash.SplashViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreen(viewModel: SplashViewModel) {
    
    val state by viewModel.viewState.collectAsState()
    
    // Handle initialization
    LaunchedEffect(Unit) {
        viewModel.processIntent(SplashIntent.InitialisationIntent)
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
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
    Text("Profile selection")
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