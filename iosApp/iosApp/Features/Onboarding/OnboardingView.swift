import SwiftUI

struct OnboardingView: View {
    @Bindable var viewModel: OnboardingViewModel

    var body: some View {
        OnboardingContentView(
            state: viewModel.state,
            onEvent: viewModel.handle,
        )
    }
}
