package com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * iOS bridge over the shared [PermissionFlowStore]. The store itself is coroutine-free;
 * this holder owns a `MainScope` solely to collect the store's `state`/`effects` Flows
 * and forward each value to a Swift closure (raw KMP interop — replace with SKIE
 * `AsyncSequence` once it supports the project's Kotlin version). All callbacks run on
 * the main dispatcher, so the Swift side may update `@Observable` state directly.
 */
@Suppress("unused") // Public API consumed from Swift; the Kotlin compiler can't see those call sites.
class PermissionFlowStoreHolder(
    statusStore: PermissionStatusStore,
    statusReader: PermissionStatusReader
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val store = PermissionFlowStore(statusStore, statusReader)

    /** Current state, read synchronously so the Swift VM can seed itself before the
     *  async [observeState] stream delivers — otherwise the first render sees the
     *  default `None` step (which reads as "flow complete") and skips the flow. */
    val currentState: PermissionFlowState get() = store.state.value

    fun onEvent(event: PermissionEvent) = store.onEvent(event)

    fun onFlowEntered() = store.onFlowEntered()

    fun refresh() = store.refresh()

    fun observeState(onChange: (PermissionFlowState) -> Unit) {
        store.state.onEach { onChange(it) }.launchIn(scope)
    }

    fun observeEffects(onEffect: (PermissionEffect) -> Unit) {
        store.effects.onEach { onEffect(it) }.launchIn(scope)
    }

    fun dispose() {
        scope.cancel()
    }
}
