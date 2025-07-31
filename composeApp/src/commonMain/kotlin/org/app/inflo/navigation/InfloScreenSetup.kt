package org.app.inflo.navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.RouteBuilder
import org.app.inflo.navigation.args.LoginArgs
import org.app.inflo.navigation.args.SceneArgs
import org.app.inflo.navigation.args.SplashArgs
import org.app.inflo.screens.LoginScreen
import org.app.inflo.screens.SplashScreen

fun RouteBuilder.setupScreen(scene: InfloScenes, navigationManager: InfloNavigationManager) {
    when (scene) {
        InfloScenes.Splash -> {
            sceneWithSafeArgs<SplashArgs>(scene) {
                SplashScreen(navigationManager)
            }
        }
        
        InfloScenes.Login -> {
            sceneWithSafeArgs<LoginArgs>(scene) {
                LoginScreen(navigationManager)
            }
        }
    }
}

private inline fun <reified ARGS : SceneArgs> RouteBuilder.sceneWithSafeArgs(
    scene: InfloScenes,
    crossinline content: @Composable (ARGS) -> Unit
) {
    scene(route = scene.baseRoute) { backStackEntry ->
        val args = backStackEntry.getQueryArgsOrNullIfInvalid() as ARGS
        content(args)
    }
} 