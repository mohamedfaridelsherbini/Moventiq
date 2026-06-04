package com.mohamedfaridelsherbini.moventiq.ui.permissions

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionFlowStepResolverTest {

    @Test
    fun resolve_hidesAll_whenLimitedFeaturesAcknowledged() {
        val step = PermissionFlowStepResolver.resolve(
            defaultInput(limitedFeaturesAcknowledged = true),
        )
        assertEquals(PermissionFlowStep.None, step)
    }

    @Test
    fun resolve_completes_whenNotificationGranted() {
        val step = PermissionFlowStepResolver.resolve(
            defaultInput(
                hasAdequateLocationAccess = true,
                notificationGranted = true,
            ),
        )
        assertEquals(PermissionFlowStep.None, step)
    }

    @Test
    fun resolve_showsLocation_whenNoAccessAndNotSkipped() {
        val step = PermissionFlowStepResolver.resolve(defaultInput())
        assertEquals(PermissionFlowStep.Location, step)
    }

    @Test
    fun resolve_showsNotification_whenLocationSkippedAndNoAccess() {
        val step = PermissionFlowStepResolver.resolve(
            defaultInput(
                locationSkippedThisSession = true,
            ),
        )
        assertEquals(PermissionFlowStep.Notification, step)
    }

    @Test
    fun resolve_showsNotification_whenLocationGrantedAndNotificationNotSkipped() {
        val step = PermissionFlowStepResolver.resolve(
            defaultInput(
                hasAdequateLocationAccess = true,
            ),
        )
        assertEquals(PermissionFlowStep.Notification, step)
    }

    @Test
    fun resolve_hidesNotification_whenLocationGrantedAndNotificationSkipped() {
        val step = PermissionFlowStepResolver.resolve(
            defaultInput(
                hasAdequateLocationAccess = true,
                notificationSkippedThisSession = true,
            ),
        )
        assertEquals(PermissionFlowStep.None, step)
    }

    @Test
    fun resolve_showsDenied_whenRecoveryRequired() {
        val step = PermissionFlowStepResolver.resolve(
            defaultInput(
                showLocationDeniedRecovery = true,
            ),
        )
        assertEquals(PermissionFlowStep.Denied, step)
    }

    @Test
    fun resolve_showsNotificationAfterLocationGrant_evenWhenLocationWasSkippedEarlier() {
        val step = PermissionFlowStepResolver.resolve(
            defaultInput(
                hasAdequateLocationAccess = true,
                locationSkippedThisSession = true,
            ),
        )
        assertEquals(PermissionFlowStep.Notification, step)
    }

    private fun defaultInput(
        limitedFeaturesAcknowledged: Boolean = false,
        hasAdequateLocationAccess: Boolean = false,
        showLocationDeniedRecovery: Boolean = false,
        locationSkippedThisSession: Boolean = false,
        notificationSkippedThisSession: Boolean = false,
        notificationPromptRequired: Boolean = true,
        notificationGranted: Boolean = false,
    ) = PermissionFlowInput(
        limitedFeaturesAcknowledged = limitedFeaturesAcknowledged,
        hasAdequateLocationAccess = hasAdequateLocationAccess,
        showLocationDeniedRecovery = showLocationDeniedRecovery,
        locationSkippedThisSession = locationSkippedThisSession,
        notificationSkippedThisSession = notificationSkippedThisSession,
        notificationPromptRequired = notificationPromptRequired,
        notificationGranted = notificationGranted,
    )
}
