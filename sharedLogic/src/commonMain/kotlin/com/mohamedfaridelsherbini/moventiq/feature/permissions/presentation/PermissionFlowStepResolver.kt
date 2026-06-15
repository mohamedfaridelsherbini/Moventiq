package com.mohamedfaridelsherbini.moventiq.feature.permissions.presentation

/**
 * Resolves which permission screen to show. Each step has its own skip rule —
 * location defer does not block notification re-prompt after location is granted.
 *
 * Pure function: identical branch order is now guaranteed across Android and iOS
 * because there is a single implementation, not a Kotlin copy and a Swift copy.
 */
object PermissionFlowStepResolver {
    fun resolve(input: PermissionFlowInput): PermissionFlowStep =
        when {
            input.limitedFeaturesAcknowledged -> PermissionFlowStep.None
            input.hasAdequateLocationAccess -> resolveNotificationStep(input)
            input.showLocationDeniedRecovery -> PermissionFlowStep.Denied
            !input.locationSkippedThisSession -> PermissionFlowStep.Location
            else -> resolveNotificationStep(input)
        }

    private fun resolveNotificationStep(input: PermissionFlowInput): PermissionFlowStep =
        when {
            !input.notificationPromptRequired || input.notificationGranted -> PermissionFlowStep.None
            input.notificationSkippedThisSession -> PermissionFlowStep.None
            else -> PermissionFlowStep.Notification
        }
}

data class PermissionFlowInput(
    val limitedFeaturesAcknowledged: Boolean,
    val hasAdequateLocationAccess: Boolean,
    val showLocationDeniedRecovery: Boolean,
    val locationSkippedThisSession: Boolean,
    val notificationSkippedThisSession: Boolean,
    val notificationPromptRequired: Boolean,
    val notificationGranted: Boolean
)
