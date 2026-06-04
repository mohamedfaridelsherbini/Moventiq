package com.mohamedfaridelsherbini.moventiq.ui.permissions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PermissionFlowViewModel(
    private val statusStore: PermissionStatusStore,
    private val statusChecker: PermissionStatusChecker,
) : ViewModel() {
    private val _state = MutableStateFlow(PermissionFlowUiState())
    val state: StateFlow<PermissionFlowUiState> = _state.asStateFlow()

    init {
        refreshFlow()
    }

    fun onEvent(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Refresh -> refreshFlow()
            PermissionEvent.LocationAllow -> Unit
            PermissionEvent.LocationLater -> {
                statusStore.setLocationPromptCompleted()
                statusStore.setShowLocationDeniedScreen(false)
                refreshFlow()
            }
            is PermissionEvent.LocationResults -> handleLocationResults(event)
            PermissionEvent.NotificationAllow -> Unit
            PermissionEvent.NotificationSkip -> {
                statusStore.setNotificationPromptCompleted()
                refreshFlow()
            }
            is PermissionEvent.NotificationResult -> {
                statusStore.setNotificationPromptCompleted()
                refreshFlow()
            }
            PermissionEvent.DeniedOpenSettings -> Unit
            PermissionEvent.DeniedLimitedFeatures -> {
                statusStore.setLimitedFeaturesAcknowledged()
                statusStore.setLocationPromptCompleted()
                statusStore.setShowLocationDeniedScreen(false)
                refreshFlow()
            }
        }
    }

    private fun handleLocationResults(event: PermissionEvent.LocationResults) {
        val adequate = event.backgroundGranted ||
            (event.fineGranted && !requiresBackgroundPermission())

        if (adequate) {
            statusStore.setLocationPromptCompleted()
            statusStore.setShowLocationDeniedScreen(false)
        } else {
            statusStore.setShowLocationDeniedScreen(true)
        }
        refreshFlow()
    }

    fun refreshFlow() {
        _state.update { it.copy(step = computeStep()) }
    }

    private fun computeStep(): PermissionFlowStep = when {
        statusStore.isLimitedFeaturesAcknowledged() -> PermissionFlowStep.None
        statusChecker.hasAdequateLocationAccess() -> stepAfterAdequateLocation()
        statusStore.shouldShowLocationDeniedScreen() -> PermissionFlowStep.Denied
        !statusStore.isLocationPromptCompleted() -> PermissionFlowStep.Location
        else -> computeNotificationStep()
    }

    private fun stepAfterAdequateLocation(): PermissionFlowStep {
        if (!statusStore.isLocationPromptCompleted()) {
            statusStore.setLocationPromptCompleted()
        }
        statusStore.setShowLocationDeniedScreen(false)
        return computeNotificationStep()
    }

    private fun computeNotificationStep(): PermissionFlowStep = when {
        !statusChecker.isNotificationPromptRequired() -> {
            if (!statusStore.isNotificationPromptCompleted()) {
                statusStore.setNotificationPromptCompleted()
            }
            PermissionFlowStep.None
        }
        statusChecker.isNotificationGranted() -> {
            if (!statusStore.isNotificationPromptCompleted()) {
                statusStore.setNotificationPromptCompleted()
            }
            PermissionFlowStep.None
        }
        !statusStore.isNotificationPromptCompleted() -> PermissionFlowStep.Notification
        else -> PermissionFlowStep.None
    }

    private fun requiresBackgroundPermission(): Boolean =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
}
