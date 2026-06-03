import SwiftUI

#Preview("Onboarding — Page 1 Light") {
    MoventiqTheme(darkTheme: false) {
        OnboardingContentView(
            state: .preview(currentPage: 0),
            onEvent: { _ in }
        )
    }
}

#Preview("Onboarding — Page 1 Dark") {
    MoventiqTheme(darkTheme: true) {
        OnboardingContentView(
            state: .preview(currentPage: 0),
            onEvent: { _ in }
        )
    }
}

#Preview("Onboarding — Page 2 Light") {
    MoventiqTheme(darkTheme: false) {
        OnboardingContentView(
            state: .preview(currentPage: 1),
            onEvent: { _ in }
        )
    }
}

#Preview("Onboarding — Page 2 Dark") {
    MoventiqTheme(darkTheme: true) {
        OnboardingContentView(
            state: .preview(currentPage: 1),
            onEvent: { _ in }
        )
    }
}

#Preview("Onboarding — Page 3 Light") {
    MoventiqTheme(darkTheme: false) {
        OnboardingContentView(
            state: .preview(currentPage: 2),
            onEvent: { _ in }
        )
    }
}

#Preview("Onboarding — Page 3 Dark") {
    MoventiqTheme(darkTheme: true) {
        OnboardingContentView(
            state: .preview(currentPage: 2),
            onEvent: { _ in }
        )
    }
}
