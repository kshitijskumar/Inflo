package org.app.inflo.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.stateholder.StateHolder
import moe.tlaster.precompose.stateholder.currentLocalStateHolder
import org.app.inflo.core.viewmodel.ViewModelFactory
import org.app.inflo.navigation.args.HomeArgs
import org.app.inflo.navigation.args.LoginArgs
import org.app.inflo.navigation.args.OnboardingArgs
import org.app.inflo.navigation.args.SceneArgs
import org.app.inflo.navigation.args.SplashArgs
import org.app.inflo.navigation.args.VerificationPendingArgs
import org.app.inflo.screens.LoginScreen
import org.app.inflo.screens.home.creator.HomeCreatorScreen
import org.app.inflo.screens.home.creator.HomeCreatorTabViewModel
import org.app.inflo.screens.home.creator.HomeCreatorViewModel
import org.app.inflo.screens.login.LoginViewModel
import org.app.inflo.screens.onboarding.OnboardingScreen
import org.app.inflo.screens.onboarding.OnboardingViewModel
import org.app.inflo.screens.splash.SplashScreen
import org.app.inflo.screens.splash.SplashViewModel
import org.app.inflo.screens.verificationpending.VerificationPendingScreen
import org.app.inflo.screens.verificationpending.VerificationPendingViewModel

fun RouteBuilder.setupScreen(scene: InfloScenes, navigationManager: InfloNavigationManager) {
    when (scene) {
        InfloScenes.Splash -> {
            scene(scene.baseRoute) {
                SplashScreen(
                    viewModel = ViewModelFactory.viewModel(
                        vmClass = SplashViewModel::class,
                        args = SplashArgs,
                        stateHolder = it.stateHolder
                    )
                )
            }
        }
        
        InfloScenes.Login -> {
            sceneWithSafeArgs<LoginArgs>(scene) {
                LoginScreen(
                    vm = ViewModelFactory.viewModel(
                        vmClass = LoginViewModel::class,
                        args = this,
                        stateHolder = it
                    )
                )
            }
        }

        InfloScenes.Home -> {
            sceneWithSafeArgs<HomeArgs>(scene) {
                when(this) {
                    is HomeArgs.BrandHomeArgs -> {
                        Text("Brand Home")
                    }
                    is HomeArgs.CreatorHomeArgs -> {
                        HomeCreatorScreen(
                            vm = ViewModelFactory.viewModel(
                                vmClass = HomeCreatorViewModel::class,
                                args = this,
                                stateHolder = currentLocalStateHolder
                            ),
                            homeCreatorVm = ViewModelFactory.viewModel(
                                vmClass = HomeCreatorTabViewModel::class,
                                args = this,
                                key = "${this}_creator_home",
                                stateHolder = currentLocalStateHolder
                            )
                        )
                    }
                }
            }
        }
        InfloScenes.Onboarding -> {
            sceneWithSafeArgs<OnboardingArgs>(scene) {
                OnboardingScreen(
                    vm = ViewModelFactory.viewModel(
                        vmClass = OnboardingViewModel::class,
                        args = this,
                        stateHolder = it
                    )
                )
            }
        }

        InfloScenes.VerificationPending -> {
            sceneWithSafeArgs<VerificationPendingArgs>(scene) {
                VerificationPendingScreen(
                    vm = ViewModelFactory.viewModel(
                        vmClass = VerificationPendingViewModel::class,
                        args = this,
                        stateHolder = it
                    )
                )
            }
        }
    }
}

private inline fun <reified ARGS : SceneArgs> RouteBuilder.sceneWithSafeArgs(
    scene: InfloScenes,
    crossinline content: @Composable ARGS.(StateHolder) -> Unit
) {
    scene(route = scene.baseRoute) { backStackEntry ->
        val args = backStackEntry.getQueryArgsOrNullIfInvalid() as ARGS
        args.content(backStackEntry.stateHolder)
    }
} 