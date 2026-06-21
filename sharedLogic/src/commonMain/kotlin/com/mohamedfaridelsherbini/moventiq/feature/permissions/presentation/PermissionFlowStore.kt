package com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Shared state machine for the permission flow — a plain synchronous reducer with no
 * coroutine scope of its own. Platform adapters wrap it:
 *
 *  - Android: `PermissionFlowViewModel` exposes [state] as `StateFlow` and collects
 *    [effects] in a `LaunchedEffect`.
 *  - iOS: a holder collects [state]/[effects] on a `MainScope` and mirrors them into
 *    an `@Observable` ViewModel.
 *
 * The platform owns lifecycle observation (app foreground), the system calls
 * (permission requests, opening Settings), and any async status refresh — after which
 * it calls [refresh]. This store owns only the *decisions*.
 */
class PermissionFlowStore(
    private val statusStore: PermissionStatusStore,
    private val statusReader: PermissionStatusReader
) {
    private val _state = MutableStateFlow(PermissionFlowState())
    val state: StateFlow<PermissionFlowState> = _state.asStateFlow()

    private val _effects = Channel<PermissionEffect>(Channel.BUFFERED)
    val effects: Flow<PermissionEffect> = _effects.receiveAsFlow()

    private var locationSkippedThisSession = false
    private var notificationSkippedThisSession = false

    init {
        clearStalePersistedDefers()
        syncDeniedStateFromOs()
        recompute()
    }

    /** Re-run the resolver after the flow becomes visible or status was refreshed. */
    fun onFlowEntered() = recompute()

    fun refresh() = recompute()

    fun onEvent(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Refresh -> recompute()
            PermissionEvent.AppReturnedFromBackground -> {
                locationSkippedThisSession = false
                notificationSkippedThisSession = false
                clearStalePersistedDefers()
                recompute()
            }
            PermissionEvent.LocationAllow -> statusStore.setLocationAllowAttempted()
            PermissionEvent.LocationLater -> {
                locationSkippedThisSession = true
                statusStore.setShowLocationDeniedScreen(false)
                recompute()
            }
            is PermissionEvent.LocationResults -> handleLocationResults(event)
            PermissionEvent.NotificationAllow -> Unit
            PermissionEvent.NotificationSkip -> {
                notificationSkippedThisSession = true
                recompute()
            }
            is PermissionEvent.NotificationResult -> {
                if (event.granted) notificationSkippedThisSession = false
                recompute()
            }
            PermissionEvent.DeniedOpenSettings -> _effects.trySend(PermissionEffect.OpenAppSettings)
            PermissionEvent.DeniedLimitedFeatures -> {
                statusStore.setLimitedFeaturesAcknowledged()
                statusStore.setShowLocationDeniedScreen(false)
                locationSkippedThisSession = false
                notificationSkippedThisSession = false
                recompute()
            }
        }
    }

    private fun recompute() {
        _state.update { it.copy(step = computeStep()) }
    }

    private fun handleLocationResults(event: PermissionEvent.LocationResults) {
        val adequate =
            event.backgroundGranted ||
                (event.fineGranted && !statusReader.requiresBackgroundLocation)
        if (adequate) {
            locationSkippedThisSession = false
            statusStore.setShowLocationDeniedScreen(false)
        } else {
            statusStore.setShowLocationDeniedScreen(true)
        }
        recompute()
    }

    private fun clearStalePersistedDefers() {
        if (statusStore.isLimitedFeaturesAcknowledged()) return
        statusStore.clearLegacyDeferFlags()
    }

    private fun syncDeniedStateFromOs() {
        if (statusStore.isLimitedFeaturesAcknowledged()) return
        if (statusReader.hasAdequateLocationAccess()) {
            statusStore.setShowLocationDeniedScreen(false)
            return
        }
        if (
            statusStore.shouldShowLocationDeniedScreen() ||
            statusReader.isLocationPermissionDenied() ||
            statusStore.wasLocationAllowAttempted()
        ) {
            statusStore.setShowLocationDeniedScreen(true)
        }
    }

    private fun computeStep(): PermissionFlowStep =
        PermissionFlowStepResolver.resolve(
            PermissionFlowInput(
                limitedFeaturesAcknowledged = statusStore.isLimitedFeaturesAcknowledged(),
                hasAdequateLocationAccess = statusReader.hasAdequateLocationAccess(),
                showLocationDeniedRecovery = shouldShowLocationDeniedRecovery(),
                locationSkippedThisSession = locationSkippedThisSession,
                notificationSkippedThisSession = notificationSkippedThisSession,
                notificationPromptRequired = statusReader.isNotificationPromptRequired(),
                notificationGranted = statusReader.isNotificationGranted()
            )
        )

    private fun shouldShowLocationDeniedRecovery(): Boolean =
        statusStore.shouldShowLocationDeniedScreen() || statusReader.isLocationPermissionDenied()
}
