import SwiftUI

struct OnboardingContentView: View {
    let state: OnboardingUiState
    let onEvent: (OnboardingEvent) -> Void

    @Environment(\.moventiqColors) private var colors
    @State private var selectedPage: Int

    init(state: OnboardingUiState, onEvent: @escaping (OnboardingEvent) -> Void) {
        self.state = state
        self.onEvent = onEvent
        _selectedPage = State(initialValue: state.currentPage)
    }

    var body: some View {
        VStack(spacing: 0) {
            VStack(spacing: 36) {
                HStack {
                    Spacer()
                    Button("Skip") {
                        onEvent(.skip)
                    }
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(colors.textMuted)
                    .accessibilityIdentifier(OnboardingAccessibility.skip)
                }

                TabView(selection: $selectedPage) {
                    ForEach(OnboardingPage.allCases) { page in
                        OnboardingPageContent(page: page)
                            .tag(page.rawValue)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))
                .frame(maxWidth: .infinity)
            }

            Spacer(minLength: 0)

            VStack(spacing: 24) {
                OnboardingProgressDots(
                    currentPage: state.currentPage,
                    pageCount: OnboardingPage.count,
                )
                MoventiqPrimaryButton(
                    title: state.currentPageData.ctaTitle,
                    action: { onEvent(.continue) },
                    accessibilityIdentifier: OnboardingAccessibility.continue,
                )
            }
        }
        .padding(.horizontal, 20)
        .padding(.top, 8)
        .padding(.bottom, 28)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(colors.surface.ignoresSafeArea())
        .accessibilityElement(children: .contain)
        .accessibilityIdentifier(OnboardingAccessibility.screen)
        .onChange(of: state.currentPage) { _, newValue in
            guard selectedPage != newValue else { return }
            selectedPage = newValue
        }
        .onChange(of: selectedPage) { _, newValue in
            guard state.currentPage != newValue else { return }
            onEvent(.pageChanged(newValue))
        }
    }
}

private struct OnboardingPageContent: View {
    let page: OnboardingPage

    @Environment(\.moventiqColors) private var colors

    var body: some View {
        VStack(spacing: 36) {
            OnboardingIllustration(page: page)

            VStack(spacing: 12) {
                Text(page.headline)
                    .font(.system(size: 27, weight: .bold))
                    .multilineTextAlignment(.center)
                    .foregroundStyle(colors.textPrimary)
                    .tracking(-0.6)

                Text(page.body)
                    .font(.system(size: 15))
                    .multilineTextAlignment(.center)
                    .foregroundStyle(colors.textSecondary)
                    .lineSpacing(4)
            }
        }
        .frame(maxWidth: .infinity)
    }
}
