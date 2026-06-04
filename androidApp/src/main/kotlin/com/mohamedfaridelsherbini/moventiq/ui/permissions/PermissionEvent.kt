package com.mohamedfaridelsherbini.moventiq.ui.permissions

sealed interface PermissionEvent {
    data object Refresh : PermissionEvent
    data object AppReturnedFromBackground : PermissionEvent
    data object LocationAllow : PermissionEvent
    data object LocationLater : PermissionEvent
    data class LocationResults(
        val fineGranted: Boolean,
        val backgroundGranted: Boolean,
    ) : PermissionEvent
    data object NotificationAllow : PermissionEvent
    data object NotificationSkip : PermissionEvent
    data class NotificationResult(val granted: Boolean) : PermissionEvent
    data object DeniedOpenSettings : PermissionEvent
    data object DeniedLimitedFeatures : PermissionEvent
}
