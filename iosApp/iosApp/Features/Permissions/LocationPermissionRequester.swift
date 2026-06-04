import CoreLocation

@MainActor
final class LocationPermissionRequester: NSObject, CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    private var onComplete: ((Bool, Bool) -> Void)?

    override init() {
        super.init()
        manager.delegate = self
    }

    func requestAccess(onComplete: @escaping (Bool, Bool) -> Void) {
        self.onComplete = onComplete
        let status = manager.authorizationStatus
        switch status {
        case .authorizedAlways:
            onComplete(true, true)
        case .authorizedWhenInUse:
            manager.requestAlwaysAuthorization()
        case .denied, .restricted:
            onComplete(false, false)
        case .notDetermined:
            manager.requestWhenInUseAuthorization()
        @unknown default:
            onComplete(false, false)
        }
    }

    nonisolated func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        Task { @MainActor in
            switch manager.authorizationStatus {
            case .authorizedAlways:
                onComplete?(true, true)
                onComplete = nil
            case .authorizedWhenInUse:
                manager.requestAlwaysAuthorization()
            case .denied, .restricted:
                onComplete?(false, false)
                onComplete = nil
            case .notDetermined:
                break
            @unknown default:
                onComplete?(false, false)
                onComplete = nil
            }
        }
    }
}
