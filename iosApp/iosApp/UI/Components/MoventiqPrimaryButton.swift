import SwiftUI

struct MoventiqPrimaryButton: View {
    let title: String
    let action: () -> Void
    var accessibilityIdentifier: String?

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(typography.labelMedium)
                .foregroundStyle(colors.textOnPrimary)
                .frame(maxWidth: .infinity)
                .frame(minHeight: spacing.xxl)
                .background(
                    colors.primary,
                    in: RoundedRectangle(cornerRadius: spacing.md, style: .continuous),
                )
        }
        .buttonStyle(.plain)
        .accessibilityIdentifier(accessibilityIdentifier ?? "")
    }
}
