import SwiftUI

@main
struct MoventiqApp: App {
    @State private var splashViewModel = SplashViewModelFactory.makeSplashViewModel()

    var body: some Scene {
        WindowGroup {
            MoventiqTheme {
                RootView(splashViewModel: splashViewModel)
            }
        }
    }
}
