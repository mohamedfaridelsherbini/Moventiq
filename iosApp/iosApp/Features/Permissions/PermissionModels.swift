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
    case appReturnedFromBackground
    case locationAllow
    case locationLater
    case locationResults(fineGranted: Bool, backgroundGranted: Bool)
    case notificationAllow
    case notificationSkip
    case notificationResult(granted: Bool)
    case deniedOpenSettings
    case deniedLimitedFeatures
}

/// One-shot effects emitted by `PermissionFlowViewModel` for the view to perform once
/// (launching system screens). Unlike `PermissionFlowUiState` these must not be
/// re-applied on re-render, so they travel over an `AsyncStream`, not observable state.
enum PermissionEffect: Equatable {
    case openAppSettings
}

protocol PermissionStatusStore: AnyObject {
    func clearLegacyDeferFlags()
    func wasLocationAllowAttempted() -> Bool
    func setLocationAllowAttempted()
    func isLimitedFeaturesAcknowledged() -> Bool
    func setLimitedFeaturesAcknowledged()
    func shouldShowLocationDeniedScreen() -> Bool
    func setShowLocationDeniedScreen(_ show: Bool)
}

protocol PermissionStatusChecker {
    func hasAdequateLocationAccess() -> Bool
    func isLocationPermissionDenied() -> Bool
    func isNotificationPromptRequired() -> Bool
    func isNotificationGranted() -> Bool
    func refreshNotificationStatus() async
}
