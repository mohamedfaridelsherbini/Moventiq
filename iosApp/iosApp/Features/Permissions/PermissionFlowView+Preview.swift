import SharedLogic
import SwiftUI

#Preview("Location Permission — Light") {
    MoventiqTheme(darkTheme: false) {
        PermissionFlowView(viewModel: .preview(step: .location))
    }
}

#Preview("Location Permission — Dark") {
    MoventiqTheme(darkTheme: true) {
        PermissionFlowView(viewModel: .preview(step: .location))
    }
}

#Preview("Notification Permission — Light") {
    MoventiqTheme(darkTheme: false) {
        PermissionFlowView(viewModel: .preview(step: .notification))
    }
}

#Preview("Notification Permission — Dark") {
    MoventiqTheme(darkTheme: true) {
        PermissionFlowView(viewModel: .preview(step: .notification))
    }
}

#Preview("Permission Denied — Light") {
    MoventiqTheme(darkTheme: false) {
        PermissionFlowView(viewModel: .preview(step: .denied))
    }
}

#Preview("Permission Denied — Dark") {
    MoventiqTheme(darkTheme: true) {
        PermissionFlowView(viewModel: .preview(step: .denied))
    }
}
