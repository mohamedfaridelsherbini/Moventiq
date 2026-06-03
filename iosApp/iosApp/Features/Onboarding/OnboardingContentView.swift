import SwiftUI

struct OnboardingContentView: View {
    let state: OnboardingUiState
    let onEvent: (OnboardingEvent) -> Void

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing
    @State private var selectedPage: Int

    init(state: OnboardingUiState, onEvent: @escaping (OnboardingEvent) -> Void) {
        self.state = state
        self.onEvent = onEvent
        _selectedPage = State(initialValue: state.currentPage)
    }

    var body: some View {
        VStack(spacing: 0) {
            VStack(spacing: spacing.xl + spacing.xs) {
                HStack {
                    Spacer()
                    Button(OnboardingStrings.skip) {
                        onEvent(.skip)
                    }
                    .font(typography.labelMedium)
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

            VStack(spacing: spacing.lg) {
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
        .padding(.horizontal, spacing.md + spacing.xs)
        .padding(.top, spacing.sm)
        .padding(.bottom, spacing.lg + spacing.xs)
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
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        VStack(spacing: spacing.xl + spacing.xs) {
            OnboardingIllustration(page: page)

            VStack(spacing: spacing.sm + spacing.xs) {
                Text(page.headline)
                    .font(typography.headlineSmall)
                    .multilineTextAlignment(.center)
                    .foregroundStyle(colors.textPrimary)
                    .tracking(-0.6)

                Text(page.body)
                    .font(typography.bodyMedium)
                    .multilineTextAlignment(.center)
                    .foregroundStyle(colors.textSecondary)
                    .lineSpacing(4)
            }
        }
        .frame(maxWidth: .infinity)
    }
}
