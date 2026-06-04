package com.mohamedfaridelsherbini.moventiq.ui.permissions

interface PermissionStatusStore {
    fun isLocationPromptCompleted(): Boolean
    fun setLocationPromptCompleted()
    fun clearLegacyDeferFlags()
    fun wasLocationAllowAttempted(): Boolean
    fun setLocationAllowAttempted()
    fun isNotificationPromptCompleted(): Boolean
    fun setNotificationPromptCompleted()
    fun isLimitedFeaturesAcknowledged(): Boolean
    fun setLimitedFeaturesAcknowledged()
    fun shouldShowLocationDeniedScreen(): Boolean
    fun setShowLocationDeniedScreen(show: Boolean)
}
