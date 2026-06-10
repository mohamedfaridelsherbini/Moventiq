import CoreLocation
import UserNotifications

final class IOSPermissionStatusChecker: PermissionStatusChecker {
    // Retain a single manager: Apple discourages allocating CLLocationManager just to
    // read authorization status, and computeStep() queries it several times per pass.
    private let locationManager = CLLocationManager()
    private(set) var cachedNotificationGranted = false

    func hasAdequateLocationAccess() -> Bool {
        locationManager.authorizationStatus == .authorizedAlways
    }

    func isLocationPermissionDenied() -> Bool {
        if hasAdequateLocationAccess() {
            return false
        }
        switch locationManager.authorizationStatus {
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
