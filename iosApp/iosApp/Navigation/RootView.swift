import SwiftUI

private enum AppPhase: Equatable {
    case splash
    case onboarding
    case main
}

struct RootView: View {
    let splashViewModel: SplashViewModel
    let onboardingViewModel: OnboardingViewModel

    private var phase: AppPhase {
        if !splashViewModel.state.isComplete {
            return .splash
        }
        if onboardingViewModel.state.shouldShowOnboarding {
            return .onboarding
        }
        return .main
    }

    var body: some View {
        Group {
            switch phase {
            case .splash:
                SplashView(viewModel: splashViewModel)
                    .transition(.opacity)
            case .onboarding:
                OnboardingView(viewModel: onboardingViewModel)
                    .transition(.opacity.combined(with: .scale(scale: 0.98)))
            case .main:
                HomeContentView()
                    .transition(.opacity.combined(with: .scale(scale: 0.98)))
            }
        }
        .animation(.easeInOut(duration: SplashBranding.appCrossfadeDuration), value: phase)
    }
}

enum SplashViewModelFactory {
    @MainActor
    static func makeSplashViewModel() -> SplashViewModel {
        let arguments = ProcessInfo.processInfo.arguments
        if arguments.contains("-UITestInstantSplash") {
            return SplashViewModel(enterWindow: 0, exitDuration: 0)
        }
        if arguments.contains("-UITestLongSplash") {
            return SplashViewModel(enterWindow: 60, exitDuration: 60)
        }
        return SplashViewModel()
    }
}

#Preview("Root — Splash") {
    RootView(
        splashViewModel: SplashViewModel(enterWindow: 60, exitDuration: 60),
        onboardingViewModel: OnboardingViewModel(statusStore: OnboardingPreferences()),
    )
}
