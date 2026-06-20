import Foundation
import Observation

@MainActor
@Observable
final class PermissionFlowViewModel {
    private(set) var state = PermissionFlowUiState()

    let effects: AsyncStream<PermissionEffect>
    private let effectsContinuation: AsyncStream<PermissionEffect>.Continuation

    private let statusStore: PermissionStatusStore
    private let statusChecker: any PermissionStatusChecker

    private var locationSkippedThisSession = false
    private var notificationSkippedThisSession = false
    private nonisolated(unsafe) var sessionTask: Task<Void, Never>?

    init(
        statusStore: PermissionStatusStore,
        statusChecker: (any PermissionStatusChecker)? = nil,
    ) {
        (effects, effectsContinuation) = AsyncStream<PermissionEffect>.makeStream()
        self.statusStore = statusStore
        self.statusChecker = statusChecker ?? IOSPermissionStatusChecker()
        clearStalePersistedDefers()
        syncDeniedStateFromOs()
        state.step = computeStep()
        // Notification status loads asynchronously; prime it now so a returning user
        // who already granted does not see the notification screen flash on launch.
        Task { @MainActor [weak self] in
            await self?.refreshFlow()
        }
        sessionTask = Task { @MainActor [weak self] in
            for await _ in NotificationCenter.default.notifications(
                named: .permissionAppReturnedFromBackground,
            ) {
                self?.handle(.appReturnedFromBackground)
            }
        }
    }

    deinit {
        sessionTask?.cancel()
        effectsContinuation.finish()
    }

    fileprivate init(previewStep: PermissionFlowStep) {
        (effects, effectsContinuation) = AsyncStream<PermissionEffect>.makeStream()
        self.statusStore = CompletedPermissionStatusStore()
        self.statusChecker = IOSPermissionStatusChecker()
        self.state = PermissionFlowUiState(step: previewStep)
    }

    static func preview(step: PermissionFlowStep) -> PermissionFlowViewModel {
        PermissionFlowViewModel(previewStep: step)
    }

    func onPermissionFlowEntered() async {
        await refreshFlow()
    }

    func handle(_ event: PermissionEvent) {
        switch event {
        case .refresh:
            state.step = computeStep()
            Task { await refreshFlow() }
        case .appReturnedFromBackground:
            locationSkippedThisSession = false
            notificationSkippedThisSession = false
            clearStalePersistedDefers()
            state.step = computeStep()
            Task { await refreshFlow() }
        case .locationAllow:
            statusStore.setLocationAllowAttempted()
        case .notificationAllow:
            break
        case .deniedOpenSettings:
            effectsContinuation.yield(.openAppSettings)
        case .locationLater:
            locationSkippedThisSession = true
            statusStore.setShowLocationDeniedScreen(false)
            state.step = computeStep()
            Task { await refreshFlow() }
        case let .locationResults(fineGranted, backgroundGranted):
            handleLocationResults(fineGranted: fineGranted, backgroundGranted: backgroundGranted)
        case .notificationSkip:
            notificationSkippedThisSession = true
            state.step = computeStep()
            Task { await refreshFlow() }
        case .notificationResult(let granted):
            handleNotificationResult(granted: granted)
        case .deniedLimitedFeatures:
            statusStore.setLimitedFeaturesAcknowledged()
            statusStore.setShowLocationDeniedScreen(false)
            locationSkippedThisSession = false
            notificationSkippedThisSession = false
            state.step = computeStep()
            Task { await refreshFlow() }
        }
    }

    private func handleNotificationResult(granted: Bool) {
        if granted {
            notificationSkippedThisSession = false
        }
        state.step = computeStep()
        Task { await refreshFlow() }
    }

    func refreshFlow() async {
        await statusChecker.refreshNotificationStatus()
        state.step = computeStep()
    }

    private func clearStalePersistedDefers() {
        if statusStore.isLimitedFeaturesAcknowledged() {
            return
        }
        statusStore.clearLegacyDeferFlags()
    }

    private func syncDeniedStateFromOs() {
        if statusStore.isLimitedFeaturesAcknowledged() {
            return
        }
        if statusChecker.hasAdequateLocationAccess() {
            statusStore.setShowLocationDeniedScreen(false)
            return
        }
        if statusStore.shouldShowLocationDeniedScreen()
            || statusChecker.isLocationPermissionDenied()
            || statusStore.wasLocationAllowAttempted() {
            statusStore.setShowLocationDeniedScreen(true)
        }
    }

    private func handleLocationResults(fineGranted: Bool, backgroundGranted: Bool) {
        let adequate = backgroundGranted || (fineGranted && !requiresBackgroundPermission())
        if adequate {
            locationSkippedThisSession = false
            statusStore.setShowLocationDeniedScreen(false)
        } else {
            statusStore.setShowLocationDeniedScreen(true)
        }
        state.step = computeStep()
        Task { await refreshFlow() }
    }

    private func computeStep() -> PermissionFlowStep {
        PermissionFlowStepResolver.resolve(
            PermissionFlowInput(
                limitedFeaturesAcknowledged: statusStore.isLimitedFeaturesAcknowledged(),
                hasAdequateLocationAccess: statusChecker.hasAdequateLocationAccess(),
                showLocationDeniedRecovery: shouldShowLocationDeniedRecovery(),
                locationSkippedThisSession: locationSkippedThisSession,
                notificationSkippedThisSession: notificationSkippedThisSession,
                notificationPromptRequired: statusChecker.isNotificationPromptRequired(),
                notificationGranted: statusChecker.isNotificationGranted(),
            ),
        )
    }

    private func shouldShowLocationDeniedRecovery() -> Bool {
        statusStore.shouldShowLocationDeniedScreen() || statusChecker.isLocationPermissionDenied()
    }

    private func requiresBackgroundPermission() -> Bool {
        true
    }

    static func resetSessionForTests() {
        PermissionAppSession.resetForTests()
    }
}

private final class UITestCompletedPermissionChecker: PermissionStatusChecker {
    func hasAdequateLocationAccess() -> Bool { true }
    func isLocationPermissionDenied() -> Bool { false }
    func isNotificationPromptRequired() -> Bool { true }
    func isNotificationGranted() -> Bool { true }
    func refreshNotificationStatus() async {}
}

private final class UITestDeniedPermissionChecker: PermissionStatusChecker {
    func hasAdequateLocationAccess() -> Bool { false }
    func isLocationPermissionDenied() -> Bool { true }
    func isNotificationPromptRequired() -> Bool { true }
    func isNotificationGranted() -> Bool { false }
    func refreshNotificationStatus() async {}
}

enum PermissionFlowViewModelFactory {
    @MainActor
    static func makePermissionFlowViewModel() -> PermissionFlowViewModel {
        let arguments = ProcessInfo.processInfo.arguments
        if arguments.contains("-UITestSkipPermissions") {
            return PermissionFlowViewModel(
                statusStore: CompletedPermissionStatusStore(),
                statusChecker: UITestCompletedPermissionChecker(),
            )
        }
        if arguments.contains("-UITestPermissionDenied") {
            return PermissionFlowViewModel(
                statusStore: FreshPermissionStatusStore(),
                statusChecker: UITestDeniedPermissionChecker(),
            )
        }
        return PermissionFlowViewModel(statusStore: PermissionPreferences())
    }
}

final class CompletedPermissionStatusStore: PermissionStatusStore {
    func clearLegacyDeferFlags() {}
    func wasLocationAllowAttempted() -> Bool { false }
    func setLocationAllowAttempted() {}
    func isLimitedFeaturesAcknowledged() -> Bool { false }
    func setLimitedFeaturesAcknowledged() {}
    func shouldShowLocationDeniedScreen() -> Bool { false }
    func setShowLocationDeniedScreen(_ show: Bool) {}
}

final class FreshPermissionStatusStore: PermissionStatusStore {
    private var limitedFeatures = false
    private var showDenied = false
    private var allowAttempted = false

    func clearLegacyDeferFlags() {}
    func wasLocationAllowAttempted() -> Bool { allowAttempted }
    func setLocationAllowAttempted() { allowAttempted = true }
    func isLimitedFeaturesAcknowledged() -> Bool { limitedFeatures }
    func setLimitedFeaturesAcknowledged() { limitedFeatures = true }
    func shouldShowLocationDeniedScreen() -> Bool { showDenied }
    func setShowLocationDeniedScreen(_ show: Bool) { showDenied = show }
}
