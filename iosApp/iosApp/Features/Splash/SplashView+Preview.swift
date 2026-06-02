import SwiftUI

#Preview("Splash — Light") {
    MoventiqTheme(darkTheme: false) {
        SplashContentView(
            state: .preview(),
            onEvent: { _ in }
        )
    }
}

#Preview("Splash — Dark") {
    MoventiqTheme(darkTheme: true) {
        SplashContentView(
            state: .preview(),
            onEvent: { _ in }
        )
    }
}

#Preview("Splash — Exiting Light") {
    MoventiqTheme(darkTheme: false) {
        SplashContentView(
            state: .preview(phase: .exiting),
            onEvent: { _ in }
        )
    }
}

#Preview("Splash — Exiting Dark") {
    MoventiqTheme(darkTheme: true) {
        SplashContentView(
            state: .preview(phase: .exiting),
            onEvent: { _ in }
        )
    }
}
