import CoreLocation
import UserNotifications

final class IOSPermissionStatusChecker: PermissionStatusChecker {
    private(set) var cachedNotificationGranted = false

    func hasAdequateLocationAccess() -> Bool {
        CLLocationManager().authorizationStatus == .authorizedAlways
    }

    func isLocationPermissionDenied() -> Bool {
        if hasAdequateLocationAccess() {
            return false
        }
        switch CLLocationManager().authorizationStatus {
        case .denied, .restricted:
            return true
        default:
            return false
        }
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
