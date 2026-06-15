package com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Shared state machine for the permission flow. Platform ViewModels become thin
 * adapters that forward events and expose [state] / [effects] to the UI:
 *
 *  - Android: collect [state] as `StateFlow`, collect [effects] in a `LaunchedEffect`.
 *  - iOS: bridge [state] into an `@Observable` mirror, consume [effects] in `.task`.
 *
 * The platform owns lifecycle observation (app foreground) and the system calls
 * (permission requests, opening Settings); this store owns the *decisions*.
 *
 * @param scope a long-lived scope (the platform ViewModel's `viewModelScope` /
 *   a `Task`-backed scope on iOS) used for effect emission and async refresh.
 */
class PermissionFlowStore(
    private val statusStore: PermissionStatusStore,
    private val statusReader: PermissionStatusReader,
    private val scope: CoroutineScope
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
        // Notification status may load asynchronously; prime it so a returning user
        // who already granted does not see the notification screen flash on launch.
        scope.launch { refresh() }
    }

    /** Re-run the resolver after the flow becomes visible (no state mutation). */
    fun onFlowEntered() {
        scope.launch { refresh() }
    }

    fun onEvent(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Refresh -> scope.launch { refresh() }
            PermissionEvent.AppReturnedFromBackground -> {
                locationSkippedThisSession = false
                notificationSkippedThisSession = false
                clearStalePersistedDefers()
                recomputeThenRefresh()
            }
            PermissionEvent.LocationAllow -> statusStore.setLocationAllowAttempted()
            PermissionEvent.LocationLater -> {
                locationSkippedThisSession = true
                statusStore.setShowLocationDeniedScreen(false)
                recomputeThenRefresh()
            }
            is PermissionEvent.LocationResults -> handleLocationResults(event)
            PermissionEvent.NotificationAllow -> Unit
            PermissionEvent.NotificationSkip -> {
                notificationSkippedThisSession = true
                recomputeThenRefresh()
            }
            is PermissionEvent.NotificationResult -> {
                if (event.granted) notificationSkippedThisSession = false
                recomputeThenRefresh()
            }
            PermissionEvent.DeniedOpenSettings ->
                scope.launch { _effects.send(PermissionEffect.OpenAppSettings) }
            PermissionEvent.DeniedLimitedFeatures -> {
                statusStore.setLimitedFeaturesAcknowledged()
                statusStore.setShowLocationDeniedScreen(false)
                locationSkippedThisSession = false
                notificationSkippedThisSession = false
                recomputeThenRefresh()
            }
        }
    }

    suspend fun refresh() {
        statusReader.refreshNotificationStatus()
        recompute()
    }

    private fun recomputeThenRefresh() {
        recompute()
        scope.launch { refresh() }
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
        recomputeThenRefresh()
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
