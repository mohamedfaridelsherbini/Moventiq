package com.mohamedfaridelsherbini.moventiq.ui.permissions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionFlowViewModel(
    private val statusStore: PermissionStatusStore,
    private val statusChecker: PermissionStatusChecker,
) : ViewModel() {
    private val _state = MutableStateFlow(PermissionFlowUiState())
    val state: StateFlow<PermissionFlowUiState> = _state.asStateFlow()

    private var locationSkippedThisSession = false
    private var notificationSkippedThisSession = false

    init {
        viewModelScope.launch {
            PermissionAppSession.returnedFromBackground.collect {
                onEvent(PermissionEvent.AppReturnedFromBackground)
            }
        }
        clearStalePersistedDefers()
        syncDeniedStateFromOs()
        refreshFlow()
    }

    fun onPermissionFlowEntered() {
        refreshFlow()
    }

    fun onEvent(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Refresh -> refreshFlow()
            PermissionEvent.AppReturnedFromBackground -> {
                locationSkippedThisSession = false
                notificationSkippedThisSession = false
                clearStalePersistedDefers()
                refreshFlow()
            }
            PermissionEvent.LocationAllow -> {
                statusStore.setLocationAllowAttempted()
            }
            PermissionEvent.LocationLater -> {
                locationSkippedThisSession = true
                statusStore.setShowLocationDeniedScreen(false)
                refreshFlow()
            }
            is PermissionEvent.LocationResults -> handleLocationResults(event)
            PermissionEvent.NotificationAllow -> Unit
            PermissionEvent.NotificationSkip -> {
                notificationSkippedThisSession = true
                refreshFlow()
            }
            is PermissionEvent.NotificationResult -> {
                if (event.granted) {
                    notificationSkippedThisSession = false
                }
                refreshFlow()
            }
            PermissionEvent.DeniedOpenSettings -> Unit
            PermissionEvent.DeniedLimitedFeatures -> {
                statusStore.setLimitedFeaturesAcknowledged()
                statusStore.setShowLocationDeniedScreen(false)
                locationSkippedThisSession = false
                notificationSkippedThisSession = false
                refreshFlow()
            }
        }
    }

    private fun handleLocationResults(event: PermissionEvent.LocationResults) {
        val adequate = event.backgroundGranted ||
            (event.fineGranted && !requiresBackgroundPermission())

        if (adequate) {
            locationSkippedThisSession = false
            statusStore.setShowLocationDeniedScreen(false)
        } else {
            statusStore.setShowLocationDeniedScreen(true)
        }
        refreshFlow()
    }

    fun refreshFlow() {
        _state.update { it.copy(step = computeStep()) }
    }

    private fun clearStalePersistedDefers() {
        if (statusStore.isLimitedFeaturesAcknowledged()) return
        statusStore.clearLegacyDeferFlags()
    }

    private fun syncDeniedStateFromOs() {
        if (statusStore.isLimitedFeaturesAcknowledged()) return
        if (statusChecker.hasAdequateLocationAccess()) {
            statusStore.setShowLocationDeniedScreen(false)
            return
        }
        if (
            statusStore.shouldShowLocationDeniedScreen() ||
            statusChecker.isLocationPermissionDenied() ||
            statusStore.wasLocationAllowAttempted()
        ) {
            statusStore.setShowLocationDeniedScreen(true)
        }
    }

    private fun computeStep(): PermissionFlowStep =
        PermissionFlowStepResolver.resolve(
            PermissionFlowInput(
                limitedFeaturesAcknowledged = statusStore.isLimitedFeaturesAcknowledged(),
                hasAdequateLocationAccess = statusChecker.hasAdequateLocationAccess(),
                showLocationDeniedRecovery = shouldShowLocationDeniedRecovery(),
                locationSkippedThisSession = locationSkippedThisSession,
                notificationSkippedThisSession = notificationSkippedThisSession,
                notificationPromptRequired = statusChecker.isNotificationPromptRequired(),
                notificationGranted = statusChecker.isNotificationGranted(),
            ),
        )

    private fun shouldShowLocationDeniedRecovery(): Boolean =
        statusStore.shouldShowLocationDeniedScreen() || statusChecker.isLocationPermissionDenied()

    private fun requiresBackgroundPermission(): Boolean =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    companion object {
        internal fun resetSessionForTests() {
            PermissionAppSession.resetForTests()
        }
    }
}
