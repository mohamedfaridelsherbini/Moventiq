import Foundation

enum SplashPhase {
    case visible
    case exiting
}

struct SplashUiState: Equatable {
    var phase: SplashPhase = .visible
    var isComplete = false

    var isExiting: Bool { phase == .exiting }

    static func preview(phase: SplashPhase = .visible) -> SplashUiState {
        SplashUiState(phase: phase)
    }
}

enum SplashEvent {
    case contentDrawn
}

enum SplashAccessibility {
    static let screen = "splash_screen"
    static let wordmark = "splash_wordmark"
    static let tagline = "splash_tagline"
}

enum SplashBranding {
    static let markSize: CGFloat = 112
    static let glowSize: CGFloat = 440
    static let wordmarkTopSpacing: CGFloat = 28
    static let taglineTopSpacing: CGFloat = 10
    static let taglineAlpha: CGFloat = 0.6
    static let glowAlphaLight: CGFloat = 0.14
    static let glowAlphaDark: CGFloat = 0.45

    static let markEnterDuration: TimeInterval = 0.65
    static let backgroundFadeDuration: TimeInterval = 0.68
    static let glowEnterDuration: TimeInterval = 0.9
    static let wordmarkEnterDuration: TimeInterval = 0.55
    static let taglineEnterDuration: TimeInterval = 0.55
    static let glowEnterDelay: TimeInterval = 0.1
    static let wordmarkEnterDelay: TimeInterval = 0.26
    static let taglineEnterDelay: TimeInterval = 0.42
    static let splashHold: TimeInterval = 0.9
    static let splashEnterWindow: TimeInterval = glowEnterDelay + glowEnterDuration + splashHold
    static let splashExitDuration: TimeInterval = 0.5
    static let appCrossfadeDuration: TimeInterval = 0.55

    static let markInitialScale: CGFloat = 0.86
    static let markExitScale: CGFloat = 0.94
    static let textSlide: CGFloat = 18
    static let glowInitialScale: CGFloat = 0.75

    static func markImageName(darkTheme: Bool) -> String {
        darkTheme ? "MoventiqSplashMarkDark" : "MoventiqSplashMarkLight"
    }

    static func glowAlpha(darkTheme: Bool) -> CGFloat {
        darkTheme ? glowAlphaDark : glowAlphaLight
    }
}
