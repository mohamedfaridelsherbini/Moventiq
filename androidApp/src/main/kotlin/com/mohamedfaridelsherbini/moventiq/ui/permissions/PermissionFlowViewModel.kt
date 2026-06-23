package com.mohamedfaridelsherbini.moventiq.ui.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionEffect
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionEvent
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionFlowState
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionFlowStore
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusReader
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Thin Android adapter over the shared [PermissionFlowStore]. The reducer lives in
 * `:sharedLogic` (`commonMain`); this class only supplies `viewModelScope`, forwards
 * events, and exposes the store's [state]/[effects] to Compose. Platform-specific
 * lifecycle (app foreground) is observed here and pushed in as an event.
 */
class PermissionFlowViewModel(
    statusStore: PermissionStatusStore,
    statusReader: PermissionStatusReader,
) : ViewModel() {
    private val store = PermissionFlowStore(statusStore, statusReader)

    val state: StateFlow<PermissionFlowState> = store.state
    val effects: Flow<PermissionEffect> = store.effects

    init {
        viewModelScope.launch {
            PermissionAppSession.returnedFromBackground.collect {
                store.onEvent(PermissionEvent.AppReturnedFromBackground)
            }
        }
    }

    fun onPermissionFlowEntered() = store.onFlowEntered()

    fun onEvent(event: PermissionEvent) = store.onEvent(event)

    companion object {
        internal fun resetSessionForTests() {
            PermissionAppSession.resetForTests()
        }
    }
}
