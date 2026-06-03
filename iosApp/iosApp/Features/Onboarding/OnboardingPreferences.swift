import Foundation

final class OnboardingPreferences: OnboardingStatusStore {
    private let defaults: UserDefaults
    private let key = "onboarding_completed"

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    func hasCompletedOnboarding() -> Bool {
        defaults.bool(forKey: key)
    }

    func setOnboardingCompleted() {
        defaults.set(true, forKey: key)
    }
}
