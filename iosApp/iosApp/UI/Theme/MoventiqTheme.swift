import SwiftUI
import UIKit

extension Color {
    static let moventiqPrimary = Color(red: 0.310, green: 0.275, blue: 0.898)
    static let moventiqPrimaryDark = Color(red: 0.506, green: 0.549, blue: 0.973)
    static let moventiqPrimaryContainer = Color(red: 0.933, green: 0.949, blue: 1.0)
    static let moventiqPrimaryContainerDark = Color(red: 0.192, green: 0.180, blue: 0.506)
    static let moventiqAccent = Color(red: 0.024, green: 0.714, blue: 0.831)
    static let moventiqBackgroundLight = Color.white
    static let moventiqBackgroundDark = Color(red: 0.059, green: 0.090, blue: 0.165)
    static let moventiqSurfaceLight = Color(red: 0.973, green: 0.980, blue: 0.988)
    static let moventiqSurfaceDark = Color(red: 17.0 / 255.0, green: 24.0 / 255.0, blue: 39.0 / 255.0)
    static let moventiqTextOnDark = Color.white
    static let moventiqSurfaceDarkElevated = Color(red: 0.118, green: 0.161, blue: 0.231)
    static let moventiqTextPrimary = Color(red: 0.059, green: 0.090, blue: 0.165)
    static let moventiqTextSecondary = Color(red: 0.278, green: 0.333, blue: 0.412)
    static let moventiqTextMuted = Color(red: 0.392, green: 0.455, blue: 0.545)
    static let moventiqTextMutedDark = Color(red: 0.580, green: 0.639, blue: 0.722)
    static let moventiqBorderLight = Color(red: 0.886, green: 0.910, blue: 0.941)
    static let moventiqBorderDark = Color(red: 0.200, green: 0.255, blue: 0.333)
    static let moventiqProgressInactive = Color(red: 0.796, green: 0.835, blue: 0.882)
    static let moventiqError = Color(red: 0.937, green: 0.267, blue: 0.267)
    static let moventiqErrorContainer = Color(red: 0.996, green: 0.949, blue: 0.949)

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

struct MoventiqTypography {
    let headlineSmall: Font
    let bodyMedium: Font
    let labelMedium: Font

    /// Values aligned with Android `MoventiqTypography` / DESIGN.md.
    static let standard = MoventiqTypography(
        headlineSmall: .system(size: 27, weight: .bold),
        bodyMedium: .system(size: 15, weight: .regular),
        labelMedium: .system(size: 15, weight: .semibold),
    )
}

struct MoventiqSpacing {
    let xs: CGFloat
    let sm: CGFloat
    let md: CGFloat
    let lg: CGFloat
    let xl: CGFloat
    let xxl: CGFloat

    /// Values aligned with Android `MoventiqSpacing` / DESIGN.md.
    static let standard = MoventiqSpacing(
        xs: 4,
        sm: 8,
        md: 16,
        lg: 24,
        xl: 32,
        xxl: 48,
    )
}

private struct MoventiqTypographyKey: EnvironmentKey {
    static let defaultValue = MoventiqTypography.standard
}

private struct MoventiqSpacingKey: EnvironmentKey {
    static let defaultValue = MoventiqSpacing.standard
}

extension EnvironmentValues {
    var moventiqTypography: MoventiqTypography {
        get { self[MoventiqTypographyKey.self] }
        set { self[MoventiqTypographyKey.self] = newValue }
    }

    var moventiqSpacing: MoventiqSpacing {
        get { self[MoventiqSpacingKey.self] }
        set { self[MoventiqSpacingKey.self] = newValue }
    }
}

struct MoventiqColors {
    let background: Color
    let foreground: Color
    let surface: Color
    let surfaceElevated: Color
    let textPrimary: Color
    let textSecondary: Color
    let textMuted: Color
    let primary: Color
    let primaryContainer: Color
    let accent: Color
    let textOnPrimary: Color
    let progressInactive: Color
    let skeleton: Color
    let skeletonMuted: Color
    let error: Color
    let errorContainer: Color

    static let light = MoventiqColors(
        background: .moventiqBackgroundLight,
        foreground: .moventiqBackgroundDark,
        surface: .moventiqSurfaceLight,
        surfaceElevated: .moventiqBackgroundLight,
        textPrimary: .moventiqTextPrimary,
        textSecondary: .moventiqTextSecondary,
        textMuted: .moventiqTextMuted,
        primary: .moventiqPrimary,
        primaryContainer: .moventiqPrimaryContainer,
        accent: .moventiqAccent,
        textOnPrimary: .moventiqBackgroundLight,
        progressInactive: .moventiqProgressInactive,
        skeleton: .moventiqBorderLight,
        skeletonMuted: .moventiqPrimaryContainer,
        error: .moventiqError,
        errorContainer: .moventiqErrorContainer,
    )

    static let dark = MoventiqColors(
        background: .moventiqBackgroundDark,
        foreground: .moventiqBackgroundLight,
        surface: .moventiqSurfaceDark,
        surfaceElevated: .moventiqSurfaceDarkElevated,
        textPrimary: .moventiqTextOnDark,
        textSecondary: .moventiqTextMutedDark,
        textMuted: .moventiqTextMuted,
        primary: .moventiqPrimaryDark,
        primaryContainer: .moventiqPrimaryContainerDark,
        accent: .moventiqAccent,
        textOnPrimary: .moventiqBackgroundLight,
        progressInactive: .moventiqProgressInactive,
        skeleton: .moventiqBorderDark,
        skeletonMuted: .moventiqPrimaryContainerDark,
        error: .moventiqError,
        errorContainer: .moventiqErrorContainer,
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
            .environment(\.moventiqTypography, .standard)
            .environment(\.moventiqSpacing, .standard)
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
