import Foundation
@testable import Moventiq

final class FakePermissionStatusChecker: PermissionStatusChecker {
    var adequateLocation = false
    var locationPermissionDenied = false
    var notificationGranted = false
    var notificationRequired = true

    func hasAdequateLocationAccess() -> Bool {
        adequateLocation
    }

    func isLocationPermissionDenied() -> Bool {
        locationPermissionDenied
    }

    func isNotificationPromptRequired() -> Bool {
        notificationRequired
    }

    func isNotificationGranted() -> Bool {
        notificationGranted
    }

    func refreshNotificationStatus() async {}
}
