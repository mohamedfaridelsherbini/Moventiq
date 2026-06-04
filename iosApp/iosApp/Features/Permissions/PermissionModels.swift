import Foundation

enum PermissionFlowStep: Equatable {
    case none
    case location
    case denied
    case notification
}

struct PermissionFlowUiState: Equatable {
    var step: PermissionFlowStep = .none

    var isFlowComplete: Bool { step == .none }
}

enum PermissionEvent: Equatable {
    case refresh
    case locationAllow
    case locationLater
    case locationResults(fineGranted: Bool, backgroundGranted: Bool)
    case notificationAllow
    case notificationSkip
    case notificationResult(granted: Bool)
    case deniedOpenSettings
    case deniedLimitedFeatures
}

protocol PermissionStatusStore: AnyObject {
    func isLocationPromptCompleted() -> Bool
    func setLocationPromptCompleted()
    func isNotificationPromptCompleted() -> Bool
    func setNotificationPromptCompleted()
    func isLimitedFeaturesAcknowledged() -> Bool
    func setLimitedFeaturesAcknowledged()
    func shouldShowLocationDeniedScreen() -> Bool
    func setShowLocationDeniedScreen(_ show: Bool)
}

protocol PermissionStatusChecker {
    func hasAdequateLocationAccess() -> Bool
    func isNotificationPromptRequired() -> Bool
    func isNotificationGranted() -> Bool
}
