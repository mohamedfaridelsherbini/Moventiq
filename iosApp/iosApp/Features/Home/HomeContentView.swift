import SwiftUI

enum HomeAccessibility {
    static let screen = "home_screen"
}

struct HomeContentView: View {
    @Environment(\.moventiqColors) private var colors

    var body: some View {
        ZStack {
            colors.background.ignoresSafeArea()
            Text("Home")
                .font(.title2.weight(.semibold))
                .foregroundStyle(colors.foreground)
        }
        .accessibilityElement(children: .contain)
        .accessibilityIdentifier(HomeAccessibility.screen)
    }
}

#Preview("Home — Light") {
    MoventiqTheme(darkTheme: false) {
        HomeContentView()
    }
}

#Preview("Home — Dark") {
    MoventiqTheme(darkTheme: true) {
        HomeContentView()
    }
}
