import SwiftUI

struct MoventiqPrimaryButton: View {
    let title: String
    let action: () -> Void
    var accessibilityIdentifier: String?

    @Environment(\.moventiqColors) private var colors

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(colors.textOnPrimary)
                .frame(maxWidth: .infinity)
                .frame(minHeight: 48)
                .background(colors.primary, in: RoundedRectangle(cornerRadius: 16, style: .continuous))
        }
        .buttonStyle(.plain)
        .accessibilityIdentifier(accessibilityIdentifier ?? "")
    }
}
