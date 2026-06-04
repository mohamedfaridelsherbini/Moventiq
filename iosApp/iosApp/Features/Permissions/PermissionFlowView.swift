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
        .onReceive(NotificationCenter.default.publisher(for: UIApplication.willEnterForegroundNotification)) { _ in
            viewModel.handle(.refresh)
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
        ) {
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
                UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
                    Task { @MainActor in
                        onEvent(.notificationResult(granted: granted))
                    }
                }
            },
            primaryAccessibilityId: PermissionAccessibility.notificationAllow,
            secondaryTitle: PermissionStrings.notificationSkip,
            secondaryAction: { onEvent(.notificationSkip) },
            secondaryAccessibilityId: PermissionAccessibility.notificationSkip,
        ) {
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
        ) {
            PermissionHeroIcon(
                name: "ic_permission_shield_off",
                iconColor: colors.error,
                containerColor: colors.errorContainer,
            )
            PermissionTextBlock(
                headline: PermissionStrings.deniedHeadline,
                bodyText: PermissionStrings.deniedBody,
            )
            PermissionCard {
                VStack(spacing: 0) {
                    PermissionStepRow(step: 1, text: PermissionStrings.deniedStep1)
                    PermissionStepRow(step: 2, text: PermissionStrings.deniedStep2)
                    PermissionStepRow(step: 3, text: PermissionStrings.deniedStep3)
                }
            }
        }
    }
}

private struct PermissionScreenLayout<Content: View>: View {
    let screenId: String
    var centered = false
    let primaryTitle: String
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
                )
                Button(action: secondaryAction) {
                    Text(secondaryTitle)
                        .font(typography.labelMedium)
                        .foregroundStyle(colors.textMuted)
                        .frame(maxWidth: .infinity, minHeight: 44)
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
                size: 44,
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
                .tracking(-0.6)
            Text(bodyText)
                .font(typography.bodyMedium)
                .multilineTextAlignment(.center)
                .foregroundStyle(colors.textSecondary)
                .lineSpacing(4)
        }
    }
}

private struct PermissionTrustRow: View {
    let text: String

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqTypography) private var typography
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        HStack(spacing: spacing.sm + spacing.xs) {
            ZStack {
                RoundedRectangle(cornerRadius: 10, style: .continuous)
                    .fill(colors.primaryContainer)
                    .frame(width: 34, height: 34)
                OnboardingAssetIcon(name: "ic_onboarding_checkmark", tint: colors.primary, size: 16)
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

    var body: some View {
        HStack(spacing: spacing.sm + spacing.xs) {
            ZStack {
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .fill(colors.primaryContainer)
                    .frame(width: 32, height: 32)
                OnboardingAssetIcon(name: "ic_onboarding_sparkles", tint: colors.primary, size: 16)
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
                    .frame(width: 28, height: 28)
                Text("\(step)")
                    .font(.system(size: 13, weight: .bold))
                    .foregroundStyle(colors.primary)
            }
            Text(text)
                .font(typography.bodyMedium)
                .foregroundStyle(colors.textPrimary)
                .frame(maxWidth: .infinity, alignment: .leading)
        }
        .padding(.horizontal, spacing.md)
        .padding(.vertical, spacing.sm + 6)
    }
}

private struct PermissionCard<Content: View>: View {
    @ViewBuilder let content: () -> Content

    @Environment(\.moventiqColors) private var colors
    @Environment(\.moventiqSpacing) private var spacing

    var body: some View {
        VStack(alignment: .leading) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(spacing.md + 2)
        .background(colors.surfaceElevated, in: RoundedRectangle(cornerRadius: spacing.lg, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: spacing.lg, style: .continuous)
                .strokeBorder(colors.skeleton, lineWidth: 1),
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
