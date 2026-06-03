import Foundation

enum OnboardingPage: Int, CaseIterable, Identifiable {
    case linkTasks = 0
    case detectArrival = 1
    case autoSurface = 2

    var id: Int { rawValue }

    static let count = allCases.count

    var headline: String {
        switch self {
        case .linkTasks:
            "Create tasks linked to places"
        case .detectArrival:
            "Moventiq detects when you arrive"
        case .autoSurface:
            "The right tasks appear automatically"
        }
    }

    var body: String {
        switch self {
        case .linkTasks:
            "Add a to-do and pin it to a spot — home, work, the gym, anywhere it belongs."
        case .detectArrival:
            "With lightweight geofencing, it quietly notices the moment you reach a saved place."
        case .autoSurface:
            "No searching, no digging. Your place-based tasks surface exactly when they matter."
        }
    }

    var ctaTitle: String {
        switch self {
        case .linkTasks, .detectArrival:
            "Continue"
        case .autoSurface:
            "Get started"
        }
    }
}
