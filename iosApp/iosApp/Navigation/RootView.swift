import SwiftUI

struct RootView: View {
    @State private var splashViewModel: SplashViewModel

    init(splashViewModel: SplashViewModel = RootView.makeSplashViewModel()) {
        _splashViewModel = State(initialValue: splashViewModel)
    }

    var body: some View {
        Group {
            if splashViewModel.state.isComplete {
                HomeContentView()
                    .transition(
                        .opacity.combined(with: .scale(scale: 0.98))
                    )
            } else {
                SplashView(viewModel: splashViewModel)
                    .transition(.opacity)
            }
        }
        .animation(
            .easeInOut(duration: SplashBranding.appCrossfadeDuration),
            value: splashViewModel.state.isComplete,
        )
    }
}

extension RootView {
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
    RootView()
}
