import Foundation

final class PermissionPreferences: PermissionStatusStore {
    private let defaults: UserDefaults
    private let locationPromptKey = "location_permission_prompt_completed"
    private let notificationPromptKey = "notification_permission_prompt_completed"
    private let limitedFeaturesKey = "limited_features_acknowledged"
    private let showDeniedKey = "show_location_denied_screen"

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    func isLocationPromptCompleted() -> Bool {
        defaults.bool(forKey: locationPromptKey)
    }

    func setLocationPromptCompleted() {
        defaults.set(true, forKey: locationPromptKey)
    }

    func isNotificationPromptCompleted() -> Bool {
        defaults.bool(forKey: notificationPromptKey)
    }

    func setNotificationPromptCompleted() {
        defaults.set(true, forKey: notificationPromptKey)
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

    func setShowLocationDeniedScreen(_ show: Bool) {
        defaults.set(show, forKey: showDeniedKey)
    }
}
