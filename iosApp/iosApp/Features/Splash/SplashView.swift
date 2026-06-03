import SwiftUI

struct SplashView: View {
    let viewModel: SplashViewModel

    var body: some View {
        SplashContentView(
            state: viewModel.state,
            onEvent: viewModel.handle
        )
    }
}
