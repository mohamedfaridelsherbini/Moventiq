import CoreLocation

@MainActor
final class LocationPermissionRequester: NSObject, CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    private var onComplete: ((Bool, Bool) -> Void)?
    private var hasRequestedAlways = false

    override init() {
        super.init()
        manager.delegate = self
    }

    func requestAccess(onComplete: @escaping (Bool, Bool) -> Void) {
        self.onComplete = onComplete
        hasRequestedAlways = false
        let status = manager.authorizationStatus
        switch status {
        case .authorizedAlways:
            onComplete(true, true)
            self.onComplete = nil
        case .authorizedWhenInUse:
            hasRequestedAlways = true
            manager.requestAlwaysAuthorization()
        case .denied, .restricted:
            onComplete(false, false)
            self.onComplete = nil
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
        @unknown default:
            onComplete(false, false)
            self.onComplete = nil
        }
    }

    nonisolated func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        Task { @MainActor in
            switch manager.authorizationStatus {
            case .authorizedAlways:
                onComplete?(true, true)
                onComplete = nil
                hasRequestedAlways = false
            case .authorizedWhenInUse:
                if hasRequestedAlways {
                    onComplete?(true, false)
                    onComplete = nil
                    hasRequestedAlways = false
                } else {
                    hasRequestedAlways = true
                    manager.requestAlwaysAuthorization()
                }
            case .denied, .restricted:
                onComplete?(false, false)
                onComplete = nil
                hasRequestedAlways = false
            case .notDetermined:
                break
            @unknown default:
                onComplete?(false, false)
                onComplete = nil
                hasRequestedAlways = false
            }
        }
    }
}
