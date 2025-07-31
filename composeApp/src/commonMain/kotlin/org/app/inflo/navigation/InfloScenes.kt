package org.app.inflo.navigation

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.query
import org.app.inflo.navigation.args.LoginArgs
import org.app.inflo.navigation.args.SceneArgs
import org.app.inflo.navigation.args.SplashArgs
import org.app.inflo.utils.AppJson

enum class InfloScenes(val baseRoute: String) {
    Splash("/splash"),
    Login("/login");

    companion object {
        const val ARGS = "args"
    }
}

fun SceneArgs.resolveCompletePath(): String {
    val scene = this.resolveInfloScene()
    val argsJson = InfloNavigationUtils.getJson(this)
    return "${scene.baseRoute}?${InfloScenes.ARGS}=$argsJson"
}

object InfloNavigationUtils {

    val specialCharacters = listOf(
        ("?" to "QSymbol"),
        ("&" to "AmpSymbol"),
        ("=" to "EqSymbol")
    )

    fun getJson(args: SceneArgs): String {
        var jsonString = AppJson.encodeToString(SceneArgs.serializer(), args)
        specialCharacters.forEach { (original, replacement) ->
            jsonString = jsonString.replace(original, replacement)
        }
        return jsonString
    }

    fun fromJson(jsonString: String): SceneArgs? {
        return try {
            var decodedString = jsonString
            specialCharacters.forEach { (original, replacement) ->
                decodedString = decodedString.replace(replacement, original)
            }
            AppJson.decodeFromString(SceneArgs.serializer(), decodedString)
        } catch (e: Exception) {
            null
        }
    }
}

fun BackStackEntry.getQueryArgsOrNullIfInvalid(): SceneArgs? {
    val argsString = this.query<String>(InfloScenes.ARGS) ?: return null
    return InfloNavigationUtils.fromJson(argsString)
} 