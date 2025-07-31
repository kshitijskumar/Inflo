package org.app.inflo.core.viewmodel

import org.app.inflo.navigation.args.SceneArgs
import moe.tlaster.precompose.reflect.canonicalName
import moe.tlaster.precompose.stateholder.StateHolder
import moe.tlaster.precompose.viewmodel.ViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

object ViewModelFactory : KoinComponent {

    /**
     * Creates viewmodel instance. For same viewmodel type if key is also same, it returns the same viewmodel
     * instance ie. viewmodel provided will be a shared viewmodel. Shared viewmodel gets cleared
     * when all its requesters lifecycle gets destroyed
     */
    fun <T: ViewModel> injectViewModel(
        vmClass: KClass<T>,
        stateHolder: StateHolder,
        key: String,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null,
    ): T {
        return stateHolder.getOrPut(qualifier?.value ?: key) {
            val sharedVmKey = ViewModelCreationWrapper.createKey(vmClass, key)
            CommonStateHolderHelper.stateHolder.getOrPut(sharedVmKey) {
                ViewModelCreationWrapper(
                    key = sharedVmKey,
                    viewModel = getKoin().get<T>(vmClass, qualifier, parameters)
                )
            }.also { it.increment() }
        }.viewModel
    }

    fun <T: ViewModel> viewModel(
        vmClass: KClass<T>,
        args: SceneArgs,
        stateHolder: StateHolder,
        key: String = stateHolder.toString(),
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition = { parametersOf(args) },
    ): T {
        return injectViewModel(
            vmClass = vmClass,
            stateHolder = stateHolder,
            key = key,
            qualifier = qualifier,
            parameters = parameters
        )
    }

}

internal object CommonStateHolderHelper {

    val stateHolder by lazy {
        StateHolder()
    }

}


internal class ViewModelCreationWrapper<T: ViewModel>(
    private val key: String,
    val viewModel: T,
) : AutoCloseable {
    private var instanceRequestedCount: Int = 0

    fun increment() {
        instanceRequestedCount++
    }

    override fun close() {
        instanceRequestedCount--
        if (instanceRequestedCount <= 0) {
            viewModel.close()
            CommonStateHolderHelper.stateHolder.remove(key)
        }
    }

    companion object {
        fun <T: ViewModel>createKey(vmClass: KClass<T>, givenKey: String): String {
            return "${vmClass.canonicalName}_$givenKey"
        }
    }
} 