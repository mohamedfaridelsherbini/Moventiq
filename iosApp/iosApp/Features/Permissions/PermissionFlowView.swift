import SwiftUI
import UserNotifications

struct PermissionFlowView: View {
    @Bindable var viewModel: PermissionFlowViewModel

    var body: some View {
        Group {
            switch viewModel.state.step {
            case .location:
                LocationPermissionView(onEvent: viewModel.handle)
            case .notification:
                NotificationPermissionView(onEvent: viewModel.handle)
            case .denied:
                PermissionDeniedView(onEvent: viewModel.handle)
            case .none:
                EmptyView()
            }
        }
    }
}

private struct LocationPermissionView: View {
    let onEvent: (PermissionEvent) -> Void

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing
    @State private var requester = LocationPermissionRequester()

    var body: some View {
        PermissionScreenLayout(
            screenId: PermissionAccessibility.locationScreen,
            primaryTitle: PermissionStrings.locationAllow,
            primaryAction: {
                onEvent(.locationAllow)
                requester.requestAccess { fine, background in
                    onEvent(.locationResults(fineGranted: fine, backgroundGranted: background))
                }
            },
            primaryAccessibilityId: PermissionAccessibility.locationAllow,
            secondaryTitle: PermissionStrings.locationLater,
            secondaryAction: { onEvent(.locationLater) },
            secondaryAccessibilityId: PermissionAccessibility.locationLater,
            content: {
                PermissionHeroIcon(name: "ic_onboarding_map_pin")
                PermissionTextBlock(
                    headline: PermissionStrings.locationHeadline,
                    bodyText: PermissionStrings.locationBody,
                )
                PermissionCard {
                    VStack(alignment: .leading, spacing: spacing.md) {
                        PermissionTrustRow(text: PermissionStrings.locationTrust1)
                        PermissionTrustRow(text: PermissionStrings.locationTrust2)
                        PermissionTrustRow(text: PermissionStrings.locationTrust3)
                    }
                }
            }
        )
    }
}

private struct NotificationPermissionView: View {
    let onEvent: (PermissionEvent) -> Void

    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        PermissionScreenLayout(
            screenId: PermissionAccessibility.notificationScreen,
            primaryTitle: PermissionStrings.notificationAllow,
            primaryAction: {
                onEvent(.notificationAllow)
                let center = UNUserNotificationCenter.current()
                center.getNotificationSettings { settings in
                    Task { @MainActor in
                        switch settings.authorizationStatus {
                        case .notDetermined:
                            // First prompt: ask the OS.
                            center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
                                Task { @MainActor in onEvent(.notificationResult(granted: granted)) }
                            }
                        case .denied:
                            // iOS won't re-present once denied; send the user to Settings instead.
                            if let url = URL(string: UIApplication.openSettingsURLString) {
                                UIApplication.shared.open(url)
                            }
                        default:
                            onEvent(.notificationResult(granted: true))
                        }
                    }
                }
            },
            primaryAccessibilityId: PermissionAccessibility.notificationAllow,
            secondaryTitle: PermissionStrings.notificationSkip,
            secondaryAction: { onEvent(.notificationSkip) },
            secondaryAccessibilityId: PermissionAccessibility.notificationSkip,
            content: {
                PermissionHeroIcon(name: "ic_permission_bell")
                PermissionTextBlock(
                    headline: PermissionStrings.notificationHeadline,
                    bodyText: PermissionStrings.notificationBody,
                )
                VStack(alignment: .leading, spacing: spacing.sm + spacing.xs) {
                    PermissionBulletRow(text: PermissionStrings.notificationBullet1)
                    PermissionBulletRow(text: PermissionStrings.notificationBullet2)
                    PermissionBulletRow(text: PermissionStrings.notificationBullet3)
                }
            }
        )
    }
}

private struct PermissionDeniedView: View {
    let onEvent: (PermissionEvent) -> Void

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        PermissionScreenLayout(
            screenId: PermissionAccessibility.deniedScreen,
            centered: true,
            primaryTitle: PermissionStrings.deniedOpenSettings,
            primaryIconName: "ic_permission_external_link",
            primaryAction: {
                onEvent(.deniedOpenSettings)
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            },
            primaryAccessibilityId: PermissionAccessibility.deniedOpenSettings,
            secondaryTitle: PermissionStrings.deniedLimited,
            secondaryAction: { onEvent(.deniedLimitedFeatures) },
            secondaryAccessibilityId: PermissionAccessibility.deniedLimited,
            content: {
                PermissionHeroIcon(
                    name: "ic_permission_shield_off",
                    iconColor: colors.error,
                    containerColor: colors.errorContainer,
                )
                PermissionTextBlock(
                    headline: PermissionStrings.deniedHeadline,
                    bodyText: PermissionStrings.deniedBody,
                )
                PermissionCard(largeCorners: true) {
                    VStack(spacing: 0) {
                        PermissionStepRow(step: 1, text: PermissionStrings.deniedStep1)
                        PermissionStepRow(step: 2, text: PermissionStrings.deniedStep2)
                        PermissionStepRow(step: 3, text: PermissionStrings.deniedStep3)
                    }
                }
            }
        )
    }
}

private struct PermissionScreenLayout<Content: View>: View {
    let screenId: String
    var centered = false
    let primaryTitle: String
    var primaryIconName: String?
    let primaryAction: () -> Void
    let primaryAccessibilityId: String
    let secondaryTitle: String
    let secondaryAction: () -> Void
    let secondaryAccessibilityId: String
    @ViewBuilder let content: () -> Content

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(spacing: spacing.lg) {
                    content()
                }
                .frame(maxWidth: .infinity)
                .padding(.top, centered ? spacing.xxl : spacing.lg)
            }

            VStack(spacing: spacing.sm + spacing.xs) {
                MoventiqPrimaryButton(
                    title: primaryTitle,
                    action: primaryAction,
                    accessibilityIdentifier: primaryAccessibilityId,
                    iconName: primaryIconName,
                )
                Button(action: secondaryAction) {
                    Text(secondaryTitle)
                        .font(typography.labelMedium)
                        .foregroundStyle(colors.textMuted)
                        .frame(maxWidth: .infinity, minHeight: spacing.iconHero)
                }
                .accessibilityIdentifier(secondaryAccessibilityId)
            }
            .padding(.top, spacing.md)
        }
        .padding(.horizontal, spacing.lg)
        .padding(.bottom, spacing.lg + spacing.xs)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(colors.surface.ignoresSafeArea())
        .accessibilityElement(children: .contain)
        .accessibilityIdentifier(screenId)
    }
}

private struct PermissionHeroIcon: View {
    let name: String
    var iconColor: Color?
    var containerColor: Color?

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        ZStack {
            Circle()
                .fill(containerColor ?? colors.primaryContainer)
                .frame(width: spacing.xxl + spacing.md, height: spacing.xxl + spacing.md)
            OnboardingAssetIcon(
                name: name,
                tint: iconColor ?? colors.primary,
                size: spacing.iconHero,
            )
        }
    }
}

private struct PermissionTextBlock: View {
    let headline: String
    let bodyText: String

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        VStack(spacing: spacing.sm + spacing.xs) {
            Text(headline)
                .font(typography.headlineSmall)
                .multilineTextAlignment(.center)
                .foregroundStyle(colors.textPrimary)
                .tracking(typography.trackingTightHeadline)
            Text(bodyText)
                .font(typography.bodyMedium)
                .multilineTextAlignment(.center)
                .foregroundStyle(colors.textSecondary)
                .lineSpacing(typography.lineSpacingBody)
        }
    }
}

private struct PermissionTrustRow: View {
    let text: String

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing
    @Environment(\.moventiqRounded) private var rounded

    var body: some View {
        HStack(spacing: spacing.sm + spacing.xs) {
            ZStack {
                RoundedRectangle(cornerRadius: rounded.sm, style: .continuous)
                    .fill(colors.primaryContainer)
                    .frame(width: spacing.iconContainerTrust, height: spacing.iconContainerTrust)
                OnboardingAssetIcon(name: "ic_onboarding_checkmark", tint: colors.primary, size: spacing.iconInline)
            }
            Text(text)
                .font(typography.bodyMedium)
                .foregroundStyle(colors.textPrimary)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

private struct PermissionBulletRow: View {
    let text: String

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing
    @Environment(\.moventiqRounded) private var rounded

    var body: some View {
        HStack(spacing: spacing.sm + spacing.xs) {
            ZStack {
                RoundedRectangle(cornerRadius: rounded.md, style: .continuous)
                    .fill(colors.primaryContainer)
                    .frame(width: spacing.xl, height: spacing.xl)
                OnboardingAssetIcon(name: "ic_onboarding_sparkles", tint: colors.primary, size: spacing.iconInline)
            }
            Text(text)
                .font(typography.bodyMedium)
                .foregroundStyle(colors.textSecondary)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}

private struct PermissionStepRow: View {
    let step: Int
    let text: String

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        HStack(spacing: spacing.sm + spacing.xs) {
            ZStack {
                Circle()
                    .fill(colors.primaryContainer)
                    .frame(width: spacing.iconStepBadge, height: spacing.iconStepBadge)
                Text("\(step)")
                    .font(typography.labelSmall)
                    .foregroundStyle(colors.primary)
            }
            Text(text)
                .font(typography.bodyMedium)
                .foregroundStyle(colors.textPrimary)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(.horizontal, spacing.md)
        .padding(.vertical, spacing.smPlus)
    }
}

private struct PermissionCard<Content: View>: View {
    var largeCorners = false
    @ViewBuilder let content: () -> Content

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqSpacing) private var spacing
    @Environment(\.moventiqStroke) private var stroke

    var body: some View {
        let cornerRadius = largeCorners ? spacing.xl : spacing.lg

        VStack(alignment: .leading) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(spacing.cardPaddingInset)
        .background(colors.surfaceElevated, in: RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                .strokeBorder(colors.skeleton, lineWidth: stroke.hairline),
        )
    }
}

private struct OnboardingAssetIcon: View {
    let name: String
    let tint: Color
    let size: CGFloat

    var body: some View {
        Image(name)
            .renderingMode(.template)
            .resizable()
            .scaledToFit()
            .frame(width: size, height: size)
            .foregroundStyle(tint)
    }
}
