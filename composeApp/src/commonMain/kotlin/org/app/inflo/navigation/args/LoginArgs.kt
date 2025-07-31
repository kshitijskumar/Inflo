package org.app.inflo.navigation.args

import kotlinx.serialization.Serializable
import org.app.inflo.navigation.InfloScenes
import org.app.inflo.screens.splash.ProfileType

@Serializable
data class LoginArgs(
    val profileType: ProfileType
) : SceneArgs() {
    override fun resolveInfloScene() = InfloScenes.Login
} 