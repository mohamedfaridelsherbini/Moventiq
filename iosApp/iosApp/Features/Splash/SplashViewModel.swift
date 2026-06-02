import Foundation
import Observation

@Observable
@MainActor
final class SplashViewModel {
    private(set) var state = SplashUiState()

    private let enterWindow: TimeInterval
    private let exitDuration: TimeInterval
    private var splashTask: Task<Void, Never>?

    init(
        enterWindow: TimeInterval = SplashBranding.splashEnterWindow,
        exitDuration: TimeInterval = SplashBranding.splashExitDuration,
    ) {
        self.enterWindow = enterWindow
        self.exitDuration = exitDuration
        startSplashSequence()
    }

    func handle(_ event: SplashEvent) {
        switch event {
        case .contentDrawn:
            break
        }
    }

    private func startSplashSequence() {
        splashTask?.cancel()
        splashTask = Task {
            let enterNs = UInt64(enterWindow * 1_000_000_000)
            let exitNs = UInt64(exitDuration * 1_000_000_000)
            if enterNs > 0 {
                try? await Task.sleep(nanoseconds: enterNs)
            }
            guard !Task.isCancelled else { return }
            state.phase = .exiting
            if exitNs > 0 {
                try? await Task.sleep(nanoseconds: exitNs)
            }
            guard !Task.isCancelled else { return }
            state.isComplete = true
        }
    }
}
