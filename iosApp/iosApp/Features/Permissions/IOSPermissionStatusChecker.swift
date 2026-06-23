import CoreLocation
import SharedLogic
import UserNotifications

/// A shared `PermissionStatusReader` that can also refresh its async notification
/// status. Lets the ViewModel await the refresh without that method leaking into the
/// Kotlin protocol (which stays fully synchronous).
protocol NotificationStatusLoader: PermissionStatusReader {
    func loadNotificationStatus() async
}

/// iOS implementation of the shared `PermissionStatusReader`. All protocol reads are
/// synchronous; the async notification query lives in `loadNotificationStatus()`
/// (Swift-only), which the ViewModel awaits before triggering a store refresh.
final class IOSPermissionStatusChecker: NotificationStatusLoader {
    // Retain a single manager: Apple discourages allocating CLLocationManager just to
    // read authorization status, and the reducer queries it several times per pass.
    private let locationManager = CLLocationManager()
    private var cachedNotificationGranted = false

    // Geofencing requires "Always" on iOS, the equivalent of Android's background grant.
    var requiresBackgroundLocation: Bool { true }

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

    /// Refreshes the cached notification grant. Not part of the Kotlin protocol — the
    /// ViewModel calls this, then asks the store to recompute.
    func loadNotificationStatus() async {
        let settings = await UNUserNotificationCenter.current().notificationSettings()
        cachedNotificationGranted = settings.authorizationStatus == .authorized
    }
}
