package com.mohamedfaridelsherbini.moventiq.ui.permissions

interface PermissionStatusStore {
    fun isLocationPromptCompleted(): Boolean
    fun setLocationPromptCompleted()
    fun isNotificationPromptCompleted(): Boolean
    fun setNotificationPromptCompleted()
    fun isLimitedFeaturesAcknowledged(): Boolean
    fun setLimitedFeaturesAcknowledged()
    fun shouldShowLocationDeniedScreen(): Boolean
    fun setShowLocationDeniedScreen(show: Boolean)
}
