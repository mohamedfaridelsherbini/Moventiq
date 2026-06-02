import SwiftUI

struct SplashView: View {
    @Bindable var viewModel: SplashViewModel

    var body: some View {
        SplashContentView(
            state: viewModel.state,
            onEvent: viewModel.handle,
        )
    }
}
