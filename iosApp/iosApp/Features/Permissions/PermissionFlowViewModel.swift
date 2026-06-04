import Foundation
import Observation

@MainActor
@Observable
final class PermissionFlowViewModel {
    private(set) var state = PermissionFlowUiState()

    private let statusStore: PermissionStatusStore
    private let statusChecker: IOSPermissionStatusChecker

    init(
        statusStore: PermissionStatusStore,
        statusChecker: IOSPermissionStatusChecker = IOSPermissionStatusChecker(),
    ) {
        self.statusStore = statusStore
        self.statusChecker = statusChecker
        Task { await refreshFlow() }
    }

    func handle(_ event: PermissionEvent) {
        switch event {
        case .refresh:
            Task { await refreshFlow() }
        case .locationAllow, .notificationAllow, .deniedOpenSettings:
            break
        case .locationLater:
            statusStore.setLocationPromptCompleted()
            statusStore.setShowLocationDeniedScreen(false)
            Task { await refreshFlow() }
        case let .locationResults(fineGranted, backgroundGranted):
            handleLocationResults(fineGranted: fineGranted, backgroundGranted: backgroundGranted)
        case .notificationSkip:
            statusStore.setNotificationPromptCompleted()
            Task { await refreshFlow() }
        case .notificationResult:
            statusStore.setNotificationPromptCompleted()
            Task { await refreshFlow() }
        case .deniedLimitedFeatures:
            statusStore.setLimitedFeaturesAcknowledged()
            statusStore.setLocationPromptCompleted()
            statusStore.setShowLocationDeniedScreen(false)
            Task { await refreshFlow() }
        }
    }

    func refreshFlow() async {
        await statusChecker.refreshNotificationStatus()
        state.step = computeStep()
    }

    private func handleLocationResults(fineGranted: Bool, backgroundGranted: Bool) {
        let adequate = backgroundGranted || (fineGranted && !requiresBackgroundPermission())
        if adequate {
            statusStore.setLocationPromptCompleted()
            statusStore.setShowLocationDeniedScreen(false)
        } else {
            statusStore.setShowLocationDeniedScreen(true)
        }
        Task { await refreshFlow() }
    }

    private func computeStep() -> PermissionFlowStep {
        if statusStore.isLimitedFeaturesAcknowledged() {
            return .none
        }

        if statusChecker.hasAdequateLocationAccess() {
            if !statusStore.isLocationPromptCompleted() {
                statusStore.setLocationPromptCompleted()
            }
            statusStore.setShowLocationDeniedScreen(false)
            return computeNotificationStep()
        }

        if statusStore.shouldShowLocationDeniedScreen() {
            return .denied
        }

        if !statusStore.isLocationPromptCompleted() {
            return .location
        }

        return computeNotificationStep()
    }

    private func computeNotificationStep() -> PermissionFlowStep {
        if statusChecker.isNotificationGranted() {
            if !statusStore.isNotificationPromptCompleted() {
                statusStore.setNotificationPromptCompleted()
            }
            return .none
        }

        if !statusStore.isNotificationPromptCompleted() {
            return .notification
        }

        return .none
    }

    private func requiresBackgroundPermission() -> Bool {
        true
    }
}

enum PermissionFlowViewModelFactory {
    @MainActor
    static func makePermissionFlowViewModel() -> PermissionFlowViewModel {
        let arguments = ProcessInfo.processInfo.arguments
        if arguments.contains("-UITestSkipPermissions") {
            return PermissionFlowViewModel(statusStore: CompletedPermissionStatusStore())
        }
        return PermissionFlowViewModel(statusStore: PermissionPreferences())
    }
}

final class CompletedPermissionStatusStore: PermissionStatusStore {
    func isLocationPromptCompleted() -> Bool { true }
    func setLocationPromptCompleted() {}
    func isNotificationPromptCompleted() -> Bool { true }
    func setNotificationPromptCompleted() {}
    func isLimitedFeaturesAcknowledged() -> Bool { false }
    func setLimitedFeaturesAcknowledged() {}
    func shouldShowLocationDeniedScreen() -> Bool { false }
    func setShowLocationDeniedScreen(_ show: Bool) {}
}

final class FreshPermissionStatusStore: PermissionStatusStore {
    private var locationCompleted = false
    private var notificationCompleted = false
    private var limitedFeatures = false
    private var showDenied = false

    func isLocationPromptCompleted() -> Bool { locationCompleted }
    func setLocationPromptCompleted() { locationCompleted = true }
    func isNotificationPromptCompleted() -> Bool { notificationCompleted }
    func setNotificationPromptCompleted() { notificationCompleted = true }
    func isLimitedFeaturesAcknowledged() -> Bool { limitedFeatures }
    func setLimitedFeaturesAcknowledged() { limitedFeatures = true }
    func shouldShowLocationDeniedScreen() -> Bool { showDenied }
    func setShowLocationDeniedScreen(_ show: Bool) { showDenied = show }
}
