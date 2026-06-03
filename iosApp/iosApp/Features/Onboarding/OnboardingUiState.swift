import Foundation

struct OnboardingUiState: Equatable {
    var currentPage: Int = 0
    var hasCompletedOnboarding: Bool?
    var isFinished = false

    var shouldShowOnboarding: Bool {
        hasCompletedOnboarding == false && !isFinished
    }

    var currentPageData: OnboardingPage {
        let clamped = min(max(currentPage, 0), OnboardingPage.count - 1)
        return OnboardingPage(rawValue: clamped) ?? .linkTasks
    }

    static func preview(
        currentPage: Int = 0,
        hasCompletedOnboarding: Bool? = false,
        isFinished: Bool = false,
    ) -> OnboardingUiState {
        OnboardingUiState(
            currentPage: currentPage,
            hasCompletedOnboarding: hasCompletedOnboarding,
            isFinished: isFinished,
        )
    }
}
