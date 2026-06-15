package com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation

/**
 * Shared presentation contract for the permission flow. These types live in
 * `commonMain` so the reducer in [PermissionFlowStore] is written once and both
 * platforms consume the same state machine — no parity drift between the Android
 * and iOS hand-written ViewModels.
 */

enum class PermissionFlowStep {
    None,
    Location,
    Denied,
    Notification
}

data class PermissionFlowState(
    val step: PermissionFlowStep = PermissionFlowStep.None
) {
    val isFlowComplete: Boolean get() = step == PermissionFlowStep.None
}

/** Inputs from the UI (UI → store). */
sealed interface PermissionEvent {
    data object Refresh : PermissionEvent

    data object AppReturnedFromBackground : PermissionEvent

    data object LocationAllow : PermissionEvent

    data object LocationLater : PermissionEvent

    data class LocationResults(
        val fineGranted: Boolean,
        val backgroundGranted: Boolean
    ) : PermissionEvent

    data object NotificationAllow : PermissionEvent

    data object NotificationSkip : PermissionEvent

    data class NotificationResult(
        val granted: Boolean
    ) : PermissionEvent

    data object DeniedOpenSettings : PermissionEvent

    data object DeniedLimitedFeatures : PermissionEvent
}

/** One-shot effects (store → UI); never re-applied on recomposition. */
sealed interface PermissionEffect {
    data object OpenAppSettings : PermissionEffect
}

/** Persistence abstraction — implemented by SharedPreferences (Android) / UserDefaults (iOS). */
interface PermissionStatusStore {
    fun clearLegacyDeferFlags()

    fun wasLocationAllowAttempted(): Boolean

    fun setLocationAllowAttempted()

    fun isLimitedFeaturesAcknowledged(): Boolean

    fun setLimitedFeaturesAcknowledged()

    fun shouldShowLocationDeniedScreen(): Boolean

    fun setShowLocationDeniedScreen(show: Boolean)
}

/**
 * Live OS permission status — implemented per platform. [requiresBackgroundLocation]
 * replaces the old `Build.VERSION` / hardcoded-true check that leaked into each
 * platform reducer, keeping the shared logic free of platform APIs.
 */
interface PermissionStatusReader {
    val requiresBackgroundLocation: Boolean

    fun hasAdequateLocationAccess(): Boolean

    fun isLocationPermissionDenied(): Boolean

    fun isNotificationPromptRequired(): Boolean

    fun isNotificationGranted(): Boolean

    suspend fun refreshNotificationStatus()
}
