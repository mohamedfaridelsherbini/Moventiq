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
            if enterWindow > 0 {
                try? await Task.sleep(for: .seconds(enterWindow))
            }
            guard !Task.isCancelled else { return }
            state.phase = .exiting
            if exitDuration > 0 {
                try? await Task.sleep(for: .seconds(exitDuration))
            }
            guard !Task.isCancelled else { return }
            state.isComplete = true
        }
    }
}
