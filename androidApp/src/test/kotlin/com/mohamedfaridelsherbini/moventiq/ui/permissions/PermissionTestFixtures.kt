package com.mohamedfaridelsherbini.moventiq.ui.permissions

class CompletedPermissionStatusStore : PermissionStatusStore {
    override fun isLocationPromptCompleted(): Boolean = true
    override fun setLocationPromptCompleted() = Unit
    override fun isNotificationPromptCompleted(): Boolean = true
    override fun setNotificationPromptCompleted() = Unit
    override fun isLimitedFeaturesAcknowledged(): Boolean = false
    override fun setLimitedFeaturesAcknowledged() = Unit
    override fun shouldShowLocationDeniedScreen(): Boolean = false
    override fun setShowLocationDeniedScreen(show: Boolean) = Unit
}

class FreshPermissionStatusStore : PermissionStatusStore {
    private var locationCompleted = false
    private var notificationCompleted = false
    private var limitedFeatures = false
    private var showDenied = false

    override fun isLocationPromptCompleted(): Boolean = locationCompleted
    override fun setLocationPromptCompleted() {
        locationCompleted = true
    }
    override fun isNotificationPromptCompleted(): Boolean = notificationCompleted
    override fun setNotificationPromptCompleted() {
        notificationCompleted = true
    }
    override fun isLimitedFeaturesAcknowledged(): Boolean = limitedFeatures
    override fun setLimitedFeaturesAcknowledged() {
        limitedFeatures = true
    }
    override fun shouldShowLocationDeniedScreen(): Boolean = showDenied
    override fun setShowLocationDeniedScreen(show: Boolean) {
        showDenied = show
    }
}

class FakePermissionStatusChecker(
    private val adequateLocation: Boolean = false,
    private val notificationGranted: Boolean = false,
    private val notificationRequired: Boolean = true,
) : PermissionStatusChecker {
    override fun hasAdequateLocationAccess(): Boolean = adequateLocation
    override fun isNotificationPromptRequired(): Boolean = notificationRequired
    override fun isNotificationGranted(): Boolean = notificationGranted
}
