import SwiftUI

@Observable
@MainActor
final class SplashMotionController {
    var backgroundProgress: CGFloat = 0
    var markAlpha: CGFloat = 0
    var markScale: CGFloat = SplashBranding.markInitialScale
    var glowAlpha: CGFloat = 0
    var glowScale: CGFloat = SplashBranding.glowInitialScale
    var wordmarkAlpha: CGFloat = 0
    var wordmarkOffset: CGFloat = SplashBranding.textSlide
    var taglineAlpha: CGFloat = 0
    var taglineOffset: CGFloat = SplashBranding.textSlide
    var screenAlpha: CGFloat = 1
    var screenScale: CGFloat = 1

    func animateEnter() async {
        withAnimation(.easeInOut(duration: SplashBranding.backgroundFadeDuration)) {
            backgroundProgress = 1
        }
        withAnimation(.easeInOut(duration: SplashBranding.markEnterDuration)) {
            markAlpha = 1
        }
        withAnimation(.easeInOut(duration: SplashBranding.markEnterDuration + 0.12)) {
            markScale = 1
        }

        try? await Task.sleep(nanoseconds: UInt64(SplashBranding.glowEnterDelay * 1_000_000_000))
        withAnimation(.easeOut(duration: SplashBranding.glowEnterDuration)) {
            glowAlpha = 1
            glowScale = 1
        }

        try? await Task.sleep(nanoseconds: UInt64(SplashBranding.wordmarkEnterDelay * 1_000_000_000))
        withAnimation(.easeInOut(duration: SplashBranding.wordmarkEnterDuration)) {
            wordmarkAlpha = 1
            wordmarkOffset = 0
        }

        try? await Task.sleep(nanoseconds: UInt64(SplashBranding.taglineEnterDelay * 1_000_000_000))
        withAnimation(.easeInOut(duration: SplashBranding.taglineEnterDuration)) {
            taglineAlpha = 1
            taglineOffset = 0
        }
    }

    func animateExit() async {
        withAnimation(.easeInOut(duration: SplashBranding.splashExitDuration)) {
            screenAlpha = 0
            screenScale = SplashBranding.markExitScale
        }
    }
}
