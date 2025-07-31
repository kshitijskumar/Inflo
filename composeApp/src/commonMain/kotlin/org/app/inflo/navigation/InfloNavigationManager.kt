package org.app.inflo.navigation

import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.PopUpTo
import org.app.inflo.navigation.args.SceneArgs

interface InfloNavigationManager {
    fun postNavigationRequest(navRequest: InfloNavigationRequest)

    fun registerNavigator(navigator: InfloNavigator)
}

fun InfloNavigationManager.navigate(args: SceneArgs, navOptions: InfloNavOptions? = null) {
    postNavigationRequest(
        InfloNavigationRequest.Navigate(
            args = args,
            navOptions = navOptions
        )
    )
}

fun InfloNavigationManager.goBack(popUpToConfig: PopUpToConfig? = null) {
    postNavigationRequest(
        InfloNavigationRequest.GoBack(popUpToConfig)
    )
}

class InfloNavigationManagerImpl : InfloNavigationManager {

    private var navigator: InfloNavigator? = null

    override fun postNavigationRequest(navRequest: InfloNavigationRequest) {
        when (navRequest) {
            is InfloNavigationRequest.GoBack -> {
                navigator?.goBack(navRequest.popUpToConfig)
            }

            is InfloNavigationRequest.Navigate -> {
                navigator?.navigate(
                    args = navRequest.args,
                    navOptions = navRequest.navOptions
                )
            }
        }
    }

    override fun registerNavigator(navigator: InfloNavigator) {
        this.navigator = navigator
    }
}

sealed class InfloNavigationRequest {
    data class Navigate(
        val args: SceneArgs,
        val navOptions: InfloNavOptions? = null
    ) : InfloNavigationRequest()
    
    data class GoBack(
        val popUpToConfig: PopUpToConfig? = null
    ) : InfloNavigationRequest()
}

data class InfloNavOptions(
    val launchSingleTop: Boolean = false,
    val includePath: Boolean = false,
    val popUpToConfig: PopUpToConfig = PopUpToConfig.None
) {
    fun toNavOptions(): NavOptions {
        return NavOptions(
            launchSingleTop = launchSingleTop,
            includePath = includePath,
            popUpTo = popUpToConfig.toPopUpTo()
        )
    }
}

sealed class PopUpToConfig {
    open val inclusive: Boolean get() = false
    
    data object None : PopUpToConfig()
    
    data object Prev : PopUpToConfig() {
        override val inclusive: Boolean get() = true
    }
    
    data class Scene(
        val scene: InfloScenes,
        override val inclusive: Boolean
    ) : PopUpToConfig()
    
    data class ClearAll(
        override val inclusive: Boolean = true
    ) : PopUpToConfig()
    
    fun toPopUpTo(): PopUpTo {
        return when(this) {
            PopUpToConfig.None -> PopUpTo.None
            PopUpToConfig.Prev -> PopUpTo.Prev
            is PopUpToConfig.Scene -> PopUpTo.Route(
                route = this.scene.baseRoute,
                inclusive = this.inclusive
            )
            is PopUpToConfig.ClearAll -> PopUpTo.First(inclusive = this.inclusive)
        }
    }
} 