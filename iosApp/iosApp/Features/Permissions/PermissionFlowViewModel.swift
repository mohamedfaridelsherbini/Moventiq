import Foundation
import Observation
import SharedLogic

/// Thin iOS adapter over the shared `PermissionFlowStoreHolder` (which wraps the
/// `commonMain` `PermissionFlowStore`). The reducer lives in `:sharedLogic`; this class
/// mirrors the store's state into `@Observable`, surfaces effects as an `AsyncStream`,
/// owns platform lifecycle observation, and runs the async notification refresh.
@MainActor
@Observable
final class PermissionFlowViewModel {
    private(set) var state: PermissionFlowState

    let effects: AsyncStream<PermissionEffect>
    private let effectsContinuation: AsyncStream<PermissionEffect>.Continuation

    private let holder: PermissionFlowStoreHolder?
    private let reader: (any NotificationStatusLoader)?
    private nonisolated(unsafe) var sessionTask: Task<Void, Never>?

    init(
        statusStore: PermissionStatusStore,
        reader: any NotificationStatusLoader = IOSPermissionStatusChecker(),
    ) {
        (effects, effectsContinuation) = AsyncStream<PermissionEffect>.makeStream()
        self.reader = reader
        let holder = PermissionFlowStoreHolder(statusStore: statusStore, statusReader: reader)
        self.holder = holder
        // Seed synchronously from the store's current value so the first render has the
        // real step; otherwise the default `.none` reads as "flow complete".
        self.state = holder.currentState

        // Holder forwards on the main dispatcher, so direct state assignment is safe.
        holder.observeState(onChange: { [weak self] newState in
            MainActor.assumeIsolated { self?.state = newState }
        })
        holder.observeEffects(onEffect: { [weak self] effect in
            self?.effectsContinuation.yield(effect)
        })

        // Prime async notification status, then recompute (avoids notif screen flash).
        Task { @MainActor in
            await reader.loadNotificationStatus()
            holder.refresh()
        }

        sessionTask = Task { @MainActor [weak self] in
            for await _ in NotificationCenter.default.notifications(
                named: .permissionAppReturnedFromBackground,
            ) {
                self?.holder?.onEvent(event: PermissionEventAppReturnedFromBackground.shared)
            }
        }
    }

    private init(previewStep: PermissionFlowStep) {
        (effects, effectsContinuation) = AsyncStream<PermissionEffect>.makeStream()
        self.holder = nil
        self.reader = nil
        self.state = PermissionFlowState(step: previewStep)
    }

    static func preview(step: PermissionFlowStep) -> PermissionFlowViewModel {
        PermissionFlowViewModel(previewStep: step)
    }

    deinit {
        sessionTask?.cancel()
        holder?.dispose()
        effectsContinuation.finish()
    }

    func onPermissionFlowEntered() async {
        await reader?.loadNotificationStatus()
        holder?.onFlowEntered()
    }

    func handle(_ event: PermissionEvent) {
        holder?.onEvent(event: event)
    }

    static func resetSessionForTests() {
        PermissionAppSession.resetForTests()
    }
}

private final class UITestCompletedPermissionChecker: NotificationStatusLoader {
    var requiresBackgroundLocation: Bool { true }
    func hasAdequateLocationAccess() -> Bool { true }
    func isLocationPermissionDenied() -> Bool { false }
    func isNotificationPromptRequired() -> Bool { true }
    func isNotificationGranted() -> Bool { true }
    func loadNotificationStatus() async {}
}

private final class UITestDeniedPermissionChecker: NotificationStatusLoader {
    var requiresBackgroundLocation: Bool { true }
    func hasAdequateLocationAccess() -> Bool { false }
    func isLocationPermissionDenied() -> Bool { true }
    func isNotificationPromptRequired() -> Bool { true }
    func isNotificationGranted() -> Bool { false }
    func loadNotificationStatus() async {}
}

enum PermissionFlowViewModelFactory {
    @MainActor
    static func makePermissionFlowViewModel() -> PermissionFlowViewModel {
        let arguments = ProcessInfo.processInfo.arguments
        if arguments.contains("-UITestSkipPermissions") {
            return PermissionFlowViewModel(
                statusStore: CompletedPermissionStatusStore(),
                reader: UITestCompletedPermissionChecker(),
            )
        }
        if arguments.contains("-UITestPermissionDenied") {
            return PermissionFlowViewModel(
                statusStore: FreshPermissionStatusStore(),
                reader: UITestDeniedPermissionChecker(),
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
    func setShowLocationDeniedScreen(show: Bool) {}
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
    func setShowLocationDeniedScreen(show: Bool) { showDenied = show }
}
