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
            OnboardingStrings.page1Headline
        case .detectArrival:
            OnboardingStrings.page2Headline
        case .autoSurface:
            OnboardingStrings.page3Headline
        }
    }

    var body: String {
        switch self {
        case .linkTasks:
            OnboardingStrings.page1Body
        case .detectArrival:
            OnboardingStrings.page2Body
        case .autoSurface:
            OnboardingStrings.page3Body
        }
    }

    var ctaTitle: String {
        switch self {
        case .linkTasks, .detectArrival:
            OnboardingStrings.continue
        case .autoSurface:
            OnboardingStrings.getStarted
        }
    }
}
