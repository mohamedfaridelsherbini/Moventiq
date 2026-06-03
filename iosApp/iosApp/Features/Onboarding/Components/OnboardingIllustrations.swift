import SwiftUI

struct OnboardingIllustration: View {
    let page: OnboardingPage

    @Environment(\.moventiqColors) private var colors

    private let height: CGFloat = 250
    private let maxWidth: CGFloat = 310

    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 32, style: .continuous)
                .fill(colors.primaryContainer)

            switch page {
            case .linkTasks:
                LinkTasksIllustration()
            case .detectArrival:
                DetectArrivalIllustration()
            case .autoSurface:
                AutoSurfaceIllustration()
            }
        }
        .frame(maxWidth: maxWidth)
        .frame(height: height)
        .clipShape(RoundedRectangle(cornerRadius: 32, style: .continuous))
    }
}

private struct LinkTasksIllustration: View {
    @Environment(\.moventiqColors) private var colors

    var body: some View {
        VStack(spacing: 14) {
            TaskCardSkeleton(checked: true)
                .shadow(color: colors.primary.opacity(0.12), radius: 9, y: 6)
            ConnectorDots()
            LocationPin(size: 56)
                .shadow(color: colors.primary.opacity(0.25), radius: 9, y: 6)
        }
    }
}

private struct DetectArrivalIllustration: View {
    @Environment(\.moventiqColors) private var colors

    var body: some View {
        ZStack {
            GeofenceRing(size: 200, alpha: 0.14)
            GeofenceRing(size: 150, alpha: 0.25)
            GeofenceRing(size: 100, alpha: 0.46)
            LocationPin(size: 64)
                .shadow(color: colors.primary.opacity(0.3), radius: 10, y: 8)
            Circle()
                .fill(colors.accent)
                .frame(width: 14, height: 14)
                .overlay(Circle().strokeBorder(colors.textOnPrimary, lineWidth: 3))
                .offset(x: 52, y: -28)
        }
    }
}

private struct AutoSurfaceIllustration: View {
    @Environment(\.moventiqColors) private var colors

    var body: some View {
        ZStack {
            TaskListCard()
                .frame(width: 220)
                .offset(y: 8)
                .shadow(color: colors.primary.opacity(0.12), radius: 10, y: 8)

            ZStack {
                Circle()
                    .fill(colors.primary)
                    .frame(width: 48, height: 48)
                    .overlay(Circle().strokeBorder(colors.textOnPrimary, lineWidth: 3))
                SparkleIcon()
            }
            .shadow(color: colors.primary.opacity(0.3), radius: 8, y: 6)
            .offset(x: 91, y: -78)
        }
    }
}

private struct TaskCardSkeleton: View {
    let checked: Bool

    @Environment(\.moventiqColors) private var colors

    var body: some View {
        HStack(spacing: 12) {
            RoundedRectangle(cornerRadius: 7, style: .continuous)
                .fill(checked ? colors.primary : colors.skeleton)
                .frame(width: 24, height: 24)
                .overlay {
                    if checked {
                        CheckMark(size: 15, tint: colors.textOnPrimary)
                    }
                }

            VStack(alignment: .leading, spacing: 7) {
                SkeletonLine(width: 132, color: colors.skeleton)
                SkeletonLine(width: 84, color: colors.skeletonMuted)
            }
        }
        .padding(14)
        .background(colors.surfaceElevated, in: RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}

private struct TaskListCard: View {
    @Environment(\.moventiqColors) private var colors

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            TaskRowSkeleton(checked: true, lineWidth: 120)
            TaskRowSkeleton(checked: false, lineWidth: 96)
            TaskRowSkeleton(checked: false, lineWidth: 76)
        }
        .padding(16)
        .background(colors.surfaceElevated, in: RoundedRectangle(cornerRadius: 18, style: .continuous))
    }
}

private struct TaskRowSkeleton: View {
    let checked: Bool
    let lineWidth: CGFloat

    @Environment(\.moventiqColors) private var colors

    var body: some View {
        HStack(spacing: 10) {
            Circle()
                .fill(checked ? colors.primary : colors.skeleton)
                .frame(width: 22, height: 22)
                .overlay {
                    if checked {
                        CheckMark(size: 13, tint: colors.textOnPrimary)
                    }
                }
            SkeletonLine(width: lineWidth, color: checked ? colors.skeleton : colors.skeletonMuted)
        }
    }
}

private struct SkeletonLine: View {
    let width: CGFloat
    let color: Color

    var body: some View {
        Capsule()
            .fill(color)
            .frame(width: width, height: 9)
    }
}

private struct ConnectorDots: View {
    @Environment(\.moventiqColors) private var colors

    var body: some View {
        VStack(spacing: 5) {
            ForEach(0 ..< 3, id: \.self) { index in
                Circle()
                    .fill(colors.primary.opacity(1 - Double(index) * 0.22))
                    .frame(width: 5, height: 5)
            }
        }
    }
}

private struct LocationPin: View {
    let size: CGFloat

    @Environment(\.moventiqColors) private var colors

    var body: some View {
        Circle()
            .fill(colors.primary)
            .frame(width: size, height: size)
            .overlay {
                MapPinIcon(tint: colors.textOnPrimary, size: size * 0.46)
            }
    }
}

private struct GeofenceRing: View {
    let size: CGFloat
    let alpha: Double

    @Environment(\.moventiqColors) private var colors

    var body: some View {
        Circle()
            .strokeBorder(colors.primary.opacity(alpha), lineWidth: 2)
            .frame(width: size, height: size)
    }
}

private struct CheckMark: View {
    let size: CGFloat
    let tint: Color

    var body: some View {
        Canvas { context, canvasSize in
            var path = Path()
            path.move(to: CGPoint(x: canvasSize.width * 0.18, y: canvasSize.height * 0.52))
            path.addLine(to: CGPoint(x: canvasSize.width * 0.42, y: canvasSize.height * 0.76))
            path.addLine(to: CGPoint(x: canvasSize.width * 0.82, y: canvasSize.height * 0.28))
            context.stroke(path, with: .color(tint), lineWidth: size * 0.12)
        }
        .frame(width: size, height: size)
    }
}

private struct MapPinIcon: View {
    let tint: Color
    let size: CGFloat

    var body: some View {
        Canvas { context, canvasSize in
            let width = canvasSize.width
            let height = canvasSize.height
            var path = Path()
            path.move(to: CGPoint(x: width / 2, y: height * 0.92))
            path.addCurve(
                to: CGPoint(x: width / 2, y: height * 0.18),
                control1: CGPoint(x: width * 0.12, y: height * 0.58),
                control2: CGPoint(x: width * 0.12, y: height * 0.28),
            )
            path.addCurve(
                to: CGPoint(x: width / 2, y: height * 0.92),
                control1: CGPoint(x: width * 0.88, y: height * 0.28),
                control2: CGPoint(x: width * 0.88, y: height * 0.58),
            )
            context.fill(path, with: .color(tint))
            let center = CGPoint(x: width / 2, y: height * 0.38)
            context.fill(
                Path(ellipseIn: CGRect(
                    x: center.x - width * 0.14,
                    y: center.y - width * 0.14,
                    width: width * 0.28,
                    height: width * 0.28,
                )),
                with: .color(tint),
            )
        }
        .frame(width: size, height: size)
    }
}

private struct SparkleIcon: View {
    var body: some View {
        Canvas { context, canvasSize in
            let center = CGPoint(x: canvasSize.width / 2, y: canvasSize.height / 2)
            let arm = min(canvasSize.width, canvasSize.height) * 0.34
            var horizontal = Path()
            horizontal.move(to: CGPoint(x: center.x - arm, y: center.y))
            horizontal.addLine(to: CGPoint(x: center.x + arm, y: center.y))
            var vertical = Path()
            vertical.move(to: CGPoint(x: center.x, y: center.y - arm))
            vertical.addLine(to: CGPoint(x: center.x, y: center.y + arm))
            var diagonalOne = Path()
            diagonalOne.move(to: CGPoint(x: center.x - arm * 0.7, y: center.y - arm * 0.7))
            diagonalOne.addLine(to: CGPoint(x: center.x + arm * 0.7, y: center.y + arm * 0.7))
            var diagonalTwo = Path()
            diagonalTwo.move(to: CGPoint(x: center.x - arm * 0.7, y: center.y + arm * 0.7))
            diagonalTwo.addLine(to: CGPoint(x: center.x + arm * 0.7, y: center.y - arm * 0.7))
            let stroke = StrokeStyle(lineWidth: 2.5, lineCap: .round)
            context.stroke(horizontal, with: .color(.white), style: stroke)
            context.stroke(vertical, with: .color(.white), style: stroke)
            context.stroke(diagonalOne, with: .color(.white), style: stroke)
            context.stroke(diagonalTwo, with: .color(.white), style: stroke)
        }
        .frame(width: 24, height: 24)
    }
}
