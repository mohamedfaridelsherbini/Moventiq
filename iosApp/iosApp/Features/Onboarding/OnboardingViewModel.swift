import Foundation
import Observation

@MainActor
@Observable
final class OnboardingViewModel {
    private(set) var state = OnboardingUiState()

    private let statusStore: OnboardingStatusStore

    init(statusStore: OnboardingStatusStore) {
        self.statusStore = statusStore
        state.hasCompletedOnboarding = statusStore.hasCompletedOnboarding()
    }

    func handle(_ event: OnboardingEvent) {
        switch event {
        case let .pageChanged(page):
            guard (0 ..< OnboardingPage.count).contains(page) else { return }
            state.currentPage = page
        case .continue:
            if state.currentPage < OnboardingPage.count - 1 {
                state.currentPage += 1
            } else {
                finishOnboarding()
            }
        case .skip:
            finishOnboarding()
        }
    }

    private func finishOnboarding() {
        statusStore.setOnboardingCompleted()
        state.hasCompletedOnboarding = true
        state.isFinished = true
    }
}

enum OnboardingViewModelFactory {
    @MainActor
    static func makeOnboardingViewModel() -> OnboardingViewModel {
        let arguments = ProcessInfo.processInfo.arguments
        if arguments.contains("-UITestSkipOnboarding") {
            return OnboardingViewModel(statusStore: CompletedOnboardingStatusStore())
        }
        if arguments.contains("-UITestFreshOnboarding") {
            return OnboardingViewModel(statusStore: FreshOnboardingStatusStore())
        }
        return OnboardingViewModel(statusStore: OnboardingPreferences())
    }
}

private struct CompletedOnboardingStatusStore: OnboardingStatusStore {
    func hasCompletedOnboarding() -> Bool { true }
    func setOnboardingCompleted() {}
}

private final class FreshOnboardingStatusStore: OnboardingStatusStore {
    private var completed = false

    func hasCompletedOnboarding() -> Bool { completed }

    func setOnboardingCompleted() {
        completed = true
    }
}
