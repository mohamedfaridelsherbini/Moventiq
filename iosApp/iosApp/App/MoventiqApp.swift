import SharedLogic
import SwiftUI

@main
struct MoventiqApp: App {
    @State private var splashViewModel = SplashViewModelFactory.makeSplashViewModel()

    init() {
        AppDependencies.bootstrap()
    }

    var body: some Scene {
        WindowGroup {
            MoventiqTheme {
                RootView(splashViewModel: splashViewModel)
            }
        }
    }
}
