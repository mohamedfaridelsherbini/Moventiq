import Foundation

enum PermissionFlowStepResolver {
    static func resolve(_ input: PermissionFlowInput) -> PermissionFlowStep {
        if input.limitedFeaturesAcknowledged {
            return .none
        }

        if input.hasAdequateLocationAccess {
            return resolveNotificationStep(input)
        }

        if input.showLocationDeniedRecovery {
            return .denied
        }

        if !input.locationSkippedThisSession {
            return .location
        }

        return resolveNotificationStep(input)
    }

    private static func resolveNotificationStep(_ input: PermissionFlowInput) -> PermissionFlowStep {
        if !input.notificationPromptRequired || input.notificationGranted {
            return .none
        }
        if input.notificationSkippedThisSession {
            return .none
        }
        return .notification
    }
}

struct PermissionFlowInput: Equatable {
    let limitedFeaturesAcknowledged: Bool
    let hasAdequateLocationAccess: Bool
    let showLocationDeniedRecovery: Bool
    let locationSkippedThisSession: Bool
    let notificationSkippedThisSession: Bool
    let notificationPromptRequired: Bool
    let notificationGranted: Bool
}
