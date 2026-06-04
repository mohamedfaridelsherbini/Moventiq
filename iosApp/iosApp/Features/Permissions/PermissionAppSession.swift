import Foundation

extension Notification.Name {
    static let permissionAppReturnedFromBackground = Notification.Name("PermissionAppSession.returnedFromBackground")
}

/// Tracks background → foreground transitions so permission defers reset at app level (not only on the permission view).
@MainActor
enum PermissionAppSession {
    private static var wasInBackground = false
    private static var hasBeenActive = false

    static func onDidBecomeActive() {
        if hasBeenActive && wasInBackground {
            NotificationCenter.default.post(name: .permissionAppReturnedFromBackground, object: nil)
            wasInBackground = false
        }
        hasBeenActive = true
    }

    static func onDidEnterBackground() {
        wasInBackground = true
    }

    static func resetForTests() {
        wasInBackground = false
        hasBeenActive = false
    }
}
