package com.mohamedfaridelsherbini.moventiq.ui.permissions

interface PermissionStatusStore {
    fun clearLegacyDeferFlags()
    fun wasLocationAllowAttempted(): Boolean
    fun setLocationAllowAttempted()
    fun isLimitedFeaturesAcknowledged(): Boolean
    fun setLimitedFeaturesAcknowledged()
    fun shouldShowLocationDeniedScreen(): Boolean
    fun setShowLocationDeniedScreen(show: Boolean)
}
