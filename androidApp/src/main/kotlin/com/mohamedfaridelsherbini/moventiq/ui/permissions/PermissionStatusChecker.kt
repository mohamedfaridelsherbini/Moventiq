package com.mohamedfaridelsherbini.moventiq.ui.permissions

interface PermissionStatusChecker {
    fun hasAdequateLocationAccess(): Boolean

    /**
     * True when foreground location is granted but background is not (Android Q+).
     * Also used for iOS "When In Use" without Always. Not a generic "denied" check.
     */
    fun isLocationPermissionDenied(): Boolean
    fun isNotificationPromptRequired(): Boolean
    fun isNotificationGranted(): Boolean
}
