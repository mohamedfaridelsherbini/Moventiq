import SwiftUI

struct SplashContentView: View {
    let state: SplashUiState
    let onEvent: (SplashEvent) -> Void

    @Environment(\.colorScheme) private var colorScheme
    @State private var motion = SplashMotionController()

    private var darkTheme: Bool { colorScheme == .dark }
    private var colors: MoventiqColors { darkTheme ? .dark : .light }

    var body: some View {
        ZStack {
            Color.moventiqFadeBackground(
                darkTheme: darkTheme,
                targetBackground: colors.background,
                progress: motion.backgroundProgress,
            )
            .ignoresSafeArea()

            VStack(spacing: 0) {
                ZStack {
                    Circle()
                        .fill(
                            RadialGradient(
                                colors: [
                                    Color.moventiqPrimary.opacity(SplashBranding.glowAlpha(darkTheme: darkTheme)),
                                    .clear,
                                ],
                                center: .center,
                                startRadius: 0,
                                endRadius: SplashBranding.glowSize / 2,
                            ),
                        )
                        .frame(width: SplashBranding.glowSize, height: SplashBranding.glowSize)
                        .scaleEffect(motion.glowScale)
                        .opacity(motion.glowAlpha)

                    Image(SplashBranding.markImageName(darkTheme: darkTheme))
                        .resizable()
                        .scaledToFit()
                        .frame(width: SplashBranding.markSize, height: SplashBranding.markSize)
                        .opacity(motion.markAlpha)
                        .scaleEffect(motion.markScale)
                        .accessibilityHidden(true)
                }

                Spacer()
                    .frame(height: SplashBranding.wordmarkTopSpacing)

                Text("Moventiq")
                    .font(.title.weight(.semibold))
                    .foregroundStyle(colors.foreground)
                    .opacity(motion.wordmarkAlpha)
                    .offset(y: motion.wordmarkOffset)
                    .accessibilityIdentifier(SplashAccessibility.wordmark)

                Spacer()
                    .frame(height: SplashBranding.taglineTopSpacing)

                Text("The right task, at the right place")
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(colors.foreground.opacity(SplashBranding.taglineAlpha))
                    .multilineTextAlignment(.center)
                    .opacity(motion.taglineAlpha)
                    .offset(y: motion.taglineOffset)
                    .accessibilityIdentifier(SplashAccessibility.tagline)
            }
            .opacity(motion.screenAlpha)
            .scaleEffect(motion.screenScale)
        }
        .accessibilityElement(children: .contain)
        .accessibilityIdentifier(SplashAccessibility.screen)
        .task {
            onEvent(.contentDrawn)
            await motion.animateEnter()
        }
        .onChange(of: state.isExiting) { _, isExiting in
            guard isExiting else { return }
            Task { await motion.animateExit() }
        }
    }
}
