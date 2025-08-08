package org.app.inflo.navigation.args

import kotlinx.serialization.Serializable
import org.app.inflo.navigation.InfloScenes

@Serializable
data object VerificationPendingArgs : SceneArgs() {
    override fun resolveInfloScene() = InfloScenes.VerificationPending
}