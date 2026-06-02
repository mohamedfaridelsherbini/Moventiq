import SwiftUI
import UIKit

extension Color {
    static let moventiqPrimary = Color(red: 0.310, green: 0.275, blue: 0.898)
    static let moventiqPrimaryContainer = Color(red: 0.933, green: 0.949, blue: 1.0)
    static let moventiqBackgroundLight = Color.white
    static let moventiqBackgroundDark = Color(red: 0.059, green: 0.090, blue: 0.165)
    static let moventiqSurfaceDarkElevated = Color(red: 0.118, green: 0.161, blue: 0.231)

    static func moventiqFadeBackground(
        darkTheme: Bool,
        targetBackground: Color,
        progress: CGFloat,
    ) -> Color {
        let start = darkTheme ? Color.moventiqSurfaceDarkElevated : Color.moventiqPrimaryContainer
        return start.mix(with: targetBackground, by: progress)
    }

    fileprivate func mix(with other: Color, by amount: CGFloat) -> Color {
        let clamped = min(max(amount, 0), 1)
        let from = UIColor(self)
        let to = UIColor(other)
        var r1: CGFloat = 0
        var g1: CGFloat = 0
        var b1: CGFloat = 0
        var a1: CGFloat = 0
        var r2: CGFloat = 0
        var g2: CGFloat = 0
        var b2: CGFloat = 0
        var a2: CGFloat = 0
        from.getRed(&r1, green: &g1, blue: &b1, alpha: &a1)
        to.getRed(&r2, green: &g2, blue: &b2, alpha: &a2)
        return Color(
            red: r1 + (r2 - r1) * clamped,
            green: g1 + (g2 - g1) * clamped,
            blue: b1 + (b2 - b1) * clamped,
            opacity: a1 + (a2 - a1) * clamped,
        )
    }
}

struct MoventiqColors {
    let background: Color
    let foreground: Color

    static let light = MoventiqColors(
        background: .moventiqBackgroundLight,
        foreground: .moventiqBackgroundDark,
    )

    static let dark = MoventiqColors(
        background: .moventiqBackgroundDark,
        foreground: .moventiqBackgroundLight,
    )
}

private struct MoventiqColorsKey: EnvironmentKey {
    static let defaultValue = MoventiqColors.light
}

extension EnvironmentValues {
    var moventiqColors: MoventiqColors {
        get { self[MoventiqColorsKey.self] }
        set { self[MoventiqColorsKey.self] = newValue }
    }
}

struct MoventiqTheme<Content: View>: View {
    var darkTheme: Bool?
    @ViewBuilder var content: () -> Content

    @Environment(\.colorScheme) private var colorScheme

    private var isDark: Bool { darkTheme ?? (colorScheme == .dark) }

    var body: some View {
        content()
            .environment(\.moventiqColors, isDark ? .dark : .light)
            .applyColorScheme(darkTheme)
    }
}

private extension View {
    @ViewBuilder
    func applyColorScheme(_ darkTheme: Bool?) -> some View {
        if let darkTheme {
            preferredColorScheme(darkTheme ? .dark : .light)
        } else {
            self
        }
    }
}
