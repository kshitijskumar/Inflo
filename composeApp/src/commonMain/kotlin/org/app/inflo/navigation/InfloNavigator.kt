package org.app.inflo.navigation

import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import org.app.inflo.navigation.args.SceneArgs

class InfloNavigator(val navigator: Navigator) {

    fun navigate(
        args: SceneArgs,
        navOptions: InfloNavOptions? = null
    ) {
        navigator.navigate(
            route = args.resolveCompletePath(),
            options = navOptions?.toNavOptions()
        )
    }

    fun goBack(popUpToConfig: PopUpToConfig?) {
        if (popUpToConfig != null) {
            navigator.goBack(popUpToConfig.toPopUpTo())
        } else {
            navigator.goBack()
        }
    }
}

@Composable
fun rememberInfloNavigator(): InfloNavigator {
    val navigator = rememberNavigator()
    return InfloNavigator(navigator)
} 