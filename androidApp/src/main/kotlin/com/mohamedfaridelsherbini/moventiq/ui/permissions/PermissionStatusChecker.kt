package com.mohamedfaridelsherbini.moventiq.ui.permissions

interface PermissionStatusChecker {
    fun hasAdequateLocationAccess(): Boolean
    fun isNotificationPromptRequired(): Boolean
    fun isNotificationGranted(): Boolean
}
