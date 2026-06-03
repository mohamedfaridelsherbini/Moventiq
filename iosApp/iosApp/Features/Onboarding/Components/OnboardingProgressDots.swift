import SwiftUI

struct OnboardingProgressDots: View {
    let currentPage: Int
    let pageCount: Int

    @Environment(\.moventiqColors) private var colors

    var body: some View {
        HStack(spacing: 8) {
            ForEach(0 ..< pageCount, id: \.self) { index in
                Capsule()
                    .fill(index == currentPage ? colors.primary : colors.progressInactive)
                    .frame(width: index == currentPage ? 24 : 8, height: 8)
            }
        }
        .accessibilityIdentifier(OnboardingAccessibility.progress)
    }
}
