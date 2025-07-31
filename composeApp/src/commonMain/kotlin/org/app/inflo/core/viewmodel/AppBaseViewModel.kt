package org.app.inflo.core.viewmodel

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.coroutines.CoroutineContext

@Suppress("LeakingThis")
abstract class AppBaseViewModel<ViewIntent, ViewState: Any, ViewSideEffect: Any> :
    ViewModel() {

    protected var initialIntentHandled = false

    private val _initialViewState = initialViewState()

    private val _viewState = MutableStateFlow(_initialViewState)
    private val _sideEffects = Channel<ViewSideEffect>(Channel.BUFFERED)

    val viewState = _viewState.asStateFlow()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _coroutineScopeState: MutableStateFlow<CoroutineScope?> = MutableStateFlow(null)
    private val coroutineScopeState = _coroutineScopeState.asStateFlow()

    fun launchInScopeWithViewLifecycle(coroutineContext: CoroutineContext = Dispatchers.Default, jobWhenLaunched: ((Job) -> Unit)? = null, block: suspend () -> Unit) {
        viewModelScope.launch {
            val coroutineScope = coroutineScopeState.value ?: coroutineScopeState.filterNotNull().first()
            val job = coroutineScope.launch(coroutineContext) {
                block.invoke()
            }
            jobWhenLaunched?.invoke(job)
        }
    }

    abstract fun initialViewState(): ViewState

    @CallSuper
    open fun processIntent(intent: ViewIntent) {
        println("${this::class.simpleName}: processIntent: $intent")
    }

    protected fun emitSideEffect(sideEffect: ViewSideEffect) {
        viewModelScope.launch {
            _sideEffects.send(sideEffect)
        }
    }

    protected fun updateState(block: (currentState: ViewState) -> ViewState) {
        _viewState.update(block)
    }

    protected suspend fun <T> Flow<T>.collectWhenViewStateActive(collector: suspend (T) -> Unit) {
        combine(
            flow = viewStateActiveStatusFlow(),
            flow2 = this
        ) { isViewStateCollectionActive, flowResult ->
            if (isViewStateCollectionActive) {
                ViewStateCollectionResult.Data(flowResult)
            } else {
                ViewStateCollectionResult.ScreenNotActive
            }
        }
            .distinctUntilChanged()
            .filterIsInstance<ViewStateCollectionResult.Data<T>>()
            .map { it.data }
            .collect(collector)
    }

    protected fun viewStateActiveStatusFlow(): Flow<Boolean> {
        return _viewState.subscriptionCount
            .map { it >= 1 }
            .distinctUntilChanged()
    }

    /**
     * Calling this method will start calling `onViewStateActive` and `onViewStateInactive`
     * whenever the view state is active or inactive.
     */
    protected fun registerForViewStateChanges() = viewModelScope.launch{
        _viewState.subscriptionCount
            .map { it >= 1 }
            .distinctUntilChanged()
            .collect { isActive ->
                if (isActive) {
                    onViewStateActive()
                } else {
                    onViewStateInactive()
                }
            }
    }

    /**
     * Call `registerForViewStateChanges` method to start receiving this callback.
     */
    protected open fun onViewStateActive() {}

    /**
     * Call `registerForViewStateChanges` method to start receiving this callback.
     */
    protected open fun onViewStateInactive() {}

    fun initializeCoroutineScopeWhileVsCollected() {
        viewModelScope.launch {
            viewStateActiveStatusFlow()
                .collectLatest { shouldCreateScope ->
                    if(shouldCreateScope) {
                        _coroutineScopeState.update {
                            CoroutineScope(Dispatchers.Default)
                        }
                    } else {
                        _coroutineScopeState.value?.cancel()
                        _coroutineScopeState.update {
                            null
                        }
                    }
                }
        }
    }
}

sealed class ViewStateCollectionResult<T> {
    data object ScreenNotActive : ViewStateCollectionResult<Nothing>()
    data class Data<T>(val data: T) : ViewStateCollectionResult<T>()
}
