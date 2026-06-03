import SwiftUI

#Preview("MoventiqPrimaryButton — Light") {
    MoventiqTheme(darkTheme: false) {
        MoventiqPrimaryButton(title: "Continue", action: {})
            .padding()
    }
}

#Preview("MoventiqPrimaryButton — Dark") {
    MoventiqTheme(darkTheme: true) {
        MoventiqPrimaryButton(title: "Get started", action: {})
            .padding()
    }
}
