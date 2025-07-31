package org.app.inflo.screens.splash

data class SplashState(
    val showError: Boolean = false,
    val screenType: SplashScreenType = SplashScreenType.LOADING
)

enum class SplashScreenType {
    LOADING,
    PROFILE_TYPE_SELECTION
}

enum class ProfileType {
    CREATOR,
    BRAND
}

sealed class SplashIntent {

    data object InitialisationIntent : SplashIntent()

    data class ProfileTypeSelectedIntent(val type: ProfileType) : SplashIntent()

}

sealed class SplashEffect {

}