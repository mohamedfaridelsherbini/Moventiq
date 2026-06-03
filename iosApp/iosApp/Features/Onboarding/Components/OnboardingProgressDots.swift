import SwiftUI

struct OnboardingProgressDots: View {
    let currentPage: Int
    let pageCount: Int

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        HStack(spacing: spacing.sm) {
            ForEach(0 ..< pageCount, id: \.self) { index in
                Capsule()
                    .fill(index == currentPage ? colors.primary : colors.progressInactive)
                    .frame(
                        width: index == currentPage ? spacing.lg : spacing.sm,
                        height: spacing.sm,
                    )
            }
        }
        .accessibilityIdentifier(OnboardingAccessibility.progress)
    }
}
