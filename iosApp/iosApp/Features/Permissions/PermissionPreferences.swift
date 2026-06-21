import Foundation
import SharedLogic

final class PermissionPreferences: PermissionStatusStore {
    private let defaults: UserDefaults
    private let locationPromptKey = "location_permission_prompt_completed"
    private let notificationPromptKey = "notification_permission_prompt_completed"
    private let limitedFeaturesKey = "limited_features_acknowledged"
    private let showDeniedKey = "show_location_denied_screen"
    private let locationAllowAttemptedKey = "location_allow_attempted"

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    // Clears prompt-completion flags persisted by older app versions; the keys are
    // no longer read, only wiped on launch so stale values cannot linger.
    func clearLegacyDeferFlags() {
        defaults.set(false, forKey: locationPromptKey)
        defaults.set(false, forKey: notificationPromptKey)
    }

    func wasLocationAllowAttempted() -> Bool {
        defaults.bool(forKey: locationAllowAttemptedKey)
    }

    func setLocationAllowAttempted() {
        defaults.set(true, forKey: locationAllowAttemptedKey)
    }

    func isLimitedFeaturesAcknowledged() -> Bool {
        defaults.bool(forKey: limitedFeaturesKey)
    }

    func setLimitedFeaturesAcknowledged() {
        defaults.set(true, forKey: limitedFeaturesKey)
    }

    func shouldShowLocationDeniedScreen() -> Bool {
        defaults.bool(forKey: showDeniedKey)
    }

    func setShowLocationDeniedScreen(show: Bool) {
        defaults.set(show, forKey: showDeniedKey)
    }
}
