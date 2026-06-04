import Foundation

/// Tracks background → foreground transitions so permission defers reset at app level (not only on the permission view).
@MainActor
enum PermissionAppSession {
    private static var wasInBackground = false
    private static var hasBeenActive = false

    static var onReturnedFromBackground: (() -> Void)?

    static func onDidBecomeActive() {
        if hasBeenActive && wasInBackground {
            onReturnedFromBackground?()
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
        onReturnedFromBackground = nil
    }
}
