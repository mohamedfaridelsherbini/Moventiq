---
name: moventiq-swiftui-ui
description: >-
  Builds Moventiq iOS UI with SwiftUI — theme, components, navigation, ViewModels
  linked to SharedLogic.framework. Use before M5 iOS parity work.
---

# Moventiq SwiftUI UI (M5)

Architecture and previews: `moventiq-ui-architecture`. Design: `moventiq-pencil-design`.

Full layout: [ARCHITECTURE.md](../../../ARCHITECTURE.md) §7.

## Target structure (`iosApp/iosApp/`)

```
App/
  MoventiqApp.swift              @main, calls initKoin() from SharedLogic
  AppDependencies.swift          resolves use cases from Koin / SharedLogic
Navigation/
  RootView.swift
  MainTabView.swift              Home · Tasks · + · Places · Settings
Features/
  Home/ Tasks/ Locations/ Settings/ Arrival/ Onboarding/
UI/
  Theme/                         MoventiqTheme, MoventiqTypography
  Components/                    TaskRowView, LocationCardView, MoventiqBottomBar
Platform/
  GeofenceService.swift
  NotificationService.swift
  LocationPermissionService.swift
Bridge/
  SharedLogic+Async.swift        Flow → AsyncStream
```

## Stack

| Concern | Framework |
|---|---|
| UI | SwiftUI (iOS 17+) |
| Navigation | `NavigationStack` + `NavigationPath` |
| State | `@Observable` ViewModels |
| DB / logic | `SharedLogic.framework` (Room via KMP) |
| Geofencing | CoreLocation `CLCircularRegion` |
| Notifications | UserNotifications |
| Maps | MapKit (`Map`, circle overlay for radius) |

## ViewModel pattern

```swift
@Observable
final class HomeViewModel {
    private(set) var state = HomeUiState()
    private let observeLocations: ObserveActiveLocations
    private var task: Task<Void, Never>?

    init(observeLocations: ObserveActiveLocations) {
        self.observeLocations = observeLocations
    }

    func start() {
        task = Task {
            for await locations in observeLocations.invoke().asyncStream() {
                await MainActor.run { state.locations = locations }
            }
        }
    }

    func handle(_ event: HomeEvent) { /* … */ }
}
```

ViewModels call Kotlin **use cases** exported from `SharedLogic`. Never access Room directly from Swift.

## Flow bridge

Use [SKIE](https://skie.touchlab.co/) or hand-rolled helpers in `Bridge/SharedLogic+Async.swift`:

```swift
extension KotlinFlow {
    func asyncStream<T>() -> AsyncStream<T> { /* … */ }
}
```

Verify SKIE/FLOW interop builds before wiring all screens.

## View pattern

```swift
struct HomeView: View {
    @Bindable var viewModel: HomeViewModel
    var body: some View {
        HomeContentView(state: viewModel.state, onEvent: viewModel.handle)
            .task { viewModel.start() }
    }
}
```

`HomeContentView` is pure SwiftUI — previewable without ViewModel.

## Theme

Mirror Android token names where possible:

- `Color.moventiqPrimary`, `.moventiqBackground`, `.moventiqCard`
- Plus Jakarta Sans bundled in app target
- Light + dark via `.moventiqTheme(colorScheme:)` modifier

## TabBar

Same five destinations as Android. Center FAB context-aware (Places → add location, Tasks → add task).

Fetch `Component/TabBar` screenshot from `Moventiq.pen` before implementing.

## Component naming

| Android | iOS |
|---|---|
| `TaskRow` | `TaskRowView` |
| `LocationCard` | `LocationCardView` |
| `MoventiqBottomBar` | `MoventiqBottomBar` |
| `ActiveLocationCard` | `ActiveLocationCardView` |

## M5 checklist

- [ ] `MoventiqTheme` + core components with `#Preview` light/dark
- [ ] `MainTabView` + navigation matching Android graph
- [ ] Home, Locations, Tasks, Settings wired to SharedLogic
- [ ] Flow bridge tested
- [ ] Geofencing via `moventiq-geofencing`
- [ ] Xcode scheme name verified before CI hardcoding

## Build / run

Open `iosApp/` in Xcode and run on simulator or device.

```bash
xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' build
```

## Related skills

- Geofencing: `moventiq-geofencing`
- Unit tests: `moventiq-unit-tests`
- UI tests: `moventiq-ui-tests`
- CI: `moventiq-ci`
