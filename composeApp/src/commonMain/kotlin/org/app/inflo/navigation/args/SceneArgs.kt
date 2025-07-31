package org.app.inflo.navigation.args

import kotlinx.serialization.Serializable
import org.app.inflo.navigation.InfloScenes

@Serializable
sealed class SceneArgs {
    abstract fun resolveInfloScene(): InfloScenes
} 