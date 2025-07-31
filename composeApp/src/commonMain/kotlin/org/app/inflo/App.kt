package org.app.inflo

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloScenes
import org.app.inflo.navigation.args.SplashArgs
import org.app.inflo.navigation.rememberInfloNavigator
import org.app.inflo.navigation.resolveCompletePath
import org.app.inflo.navigation.setupScreen
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

@Composable
fun App() {
    PreComposeApp {
        KoinContext {
            MaterialTheme {
                val navigationManager = koinInject<InfloNavigationManager>()
                val infloNavigator = rememberInfloNavigator()

                LaunchedEffect(navigationManager, infloNavigator) {
                    navigationManager.registerNavigator(infloNavigator)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navigator = infloNavigator.navigator,
                        navTransition = remember { InfloNavTransition() },
                        initialRoute = InfloScenes.Splash.baseRoute,
                    ) {
                        InfloScenes.entries.forEach { scene ->
                            setupScreen(scene, navigationManager)
                        }
                    }
                }
            }
        }
    }
}

@Suppress("FunctionName")
fun InfloNavTransition(): NavTransition {
    val createAnimSpec: () -> FiniteAnimationSpec<IntOffset> = {
        tween(durationMillis = 300, easing = LinearEasing)
    }
    return NavTransition(
        createTransition = slideInHorizontally(
            animationSpec = createAnimSpec(),
            initialOffsetX = { it }
        ),
        destroyTransition = slideOutHorizontally(
            animationSpec = createAnimSpec(),
            targetOffsetX = { it }
        ),
        resumeTransition = slideInHorizontally(
            animationSpec = createAnimSpec(),
            initialOffsetX = { -it }
        ),
        pauseTransition = slideOutHorizontally(
            animationSpec = createAnimSpec(),
            targetOffsetX = { -it }
        ),
    )
}