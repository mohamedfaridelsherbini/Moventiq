package com.mohamedfaridelsherbini.moventiq.ui.permissions

import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusReader
import com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation.PermissionStatusStore

class CompletedPermissionStatusStore : PermissionStatusStore {
    override fun clearLegacyDeferFlags() = Unit
    override fun wasLocationAllowAttempted(): Boolean = false
    override fun setLocationAllowAttempted() = Unit
    override fun isLimitedFeaturesAcknowledged(): Boolean = false
    override fun setLimitedFeaturesAcknowledged() = Unit
    override fun shouldShowLocationDeniedScreen(): Boolean = false
    override fun setShowLocationDeniedScreen(show: Boolean) = Unit
}

class FreshPermissionStatusStore : PermissionStatusStore {
    private var limitedFeatures = false
    private var showDenied = false
    private var allowAttempted = false

    override fun clearLegacyDeferFlags() = Unit
    override fun wasLocationAllowAttempted(): Boolean = allowAttempted
    override fun setLocationAllowAttempted() {
        allowAttempted = true
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
    var adequateLocation: Boolean = false,
    var locationPermissionDenied: Boolean = false,
    var notificationGranted: Boolean = false,
    var notificationRequired: Boolean = true,
    override val requiresBackgroundLocation: Boolean = true,
) : PermissionStatusReader {
    override fun hasAdequateLocationAccess(): Boolean = adequateLocation
    override fun isLocationPermissionDenied(): Boolean = locationPermissionDenied
    override fun isNotificationPromptRequired(): Boolean = notificationRequired
    override fun isNotificationGranted(): Boolean = notificationGranted
}
