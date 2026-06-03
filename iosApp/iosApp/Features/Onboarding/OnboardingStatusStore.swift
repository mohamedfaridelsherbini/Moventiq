import Foundation

protocol OnboardingStatusStore {
    func hasCompletedOnboarding() -> Bool
    func setOnboardingCompleted()
}
