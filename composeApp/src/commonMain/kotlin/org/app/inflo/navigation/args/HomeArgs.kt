package org.app.inflo.navigation.args

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Serializable
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.navigation.InfloScenes
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Serializable
sealed class HomeArgs : SceneArgs() {
    override fun resolveInfloScene() = InfloScenes.Home

    @Serializable
    data class CreatorHomeArgs internal constructor(val isCreator: Boolean = true): HomeArgs()

    @Serializable
    data class BrandHomeArgs internal constructor(val isBrand: Boolean = true): HomeArgs()

    companion object : KoinComponent {
        private val resolver = get<HomeArgsResolver>()

        suspend fun resolve(): HomeArgs? = resolver.invoke()
    }
}

class HomeArgsResolver(private val appRepository: AppRepository) {

    suspend operator fun invoke(): HomeArgs? {
        return when(val user = appRepository.loggedInUser().firstOrNull()) {
            is UserAppModel.Brand -> HomeArgs.BrandHomeArgs()
            is UserAppModel.Creator -> HomeArgs.CreatorHomeArgs()
            null -> null
        }
    }

}