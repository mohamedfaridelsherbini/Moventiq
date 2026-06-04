import CoreLocation
import UserNotifications

final class IOSPermissionStatusChecker: PermissionStatusChecker {
    private(set) var cachedNotificationGranted = false

    func hasAdequateLocationAccess() -> Bool {
        CLLocationManager().authorizationStatus == .authorizedAlways
    }

    func isNotificationPromptRequired() -> Bool {
        true
    }

    func isNotificationGranted() -> Bool {
        cachedNotificationGranted
    }

    func refreshNotificationStatus() async {
        let settings = await UNUserNotificationCenter.current().notificationSettings()
        cachedNotificationGranted = settings.authorizationStatus == .authorized
    }
}
