# Platform UI implementation

Read [SKILL.md](SKILL.md) first (file split, previews, testTag). This file is platform-specific stack and wiring.

Full module layout: [ARCHITECTURE.md](../../../ARCHITECTURE.md) §7 (Android) · §8 (iOS).

---

## Android — Compose (`:androidApp`)

### Stack

| Concern | Library |
|---|---|
| UI | Compose + Material 3 |
| Navigation | Navigation Compose (typed routes) |
| ViewModel | `lifecycle-viewmodel-compose` |
| DI | Koin (`koin-android`, `koin-compose-viewmodel`) |
| Flow | `collectAsStateWithLifecycle()` |
| Permissions | `ActivityResultContracts` |

### Screen pattern

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(state = state, onEvent = viewModel::onEvent)
}
```

ViewModels in `:androidApp` call **use cases** from `shared/feature/*` — never DAOs.

### Theme

`ui/theme/` — `MoventiqTheme`, `LocalMoventiqColors`, `LocalMoventiqSpacing` from [DESIGN.md](../../../DESIGN.md).

### Navigation graph

```
splash → onboarding? → permissions → main
main (Scaffold + BottomBar): home · tasks · (+) · places · settings
arrival (full-screen on geofence ENTER)
```

TabBar FAB is **context-aware** (Places → add location, Tasks → add task). No duplicate FAB on list screens.

### Build

```bash
./gradlew :androidApp:assembleDebug
```

**Do not** add UI to deprecated `:sharedUI`.

---

## iOS — SwiftUI (`:iosApp`)

### Stack

| Concern | Framework |
|---|---|
| UI | SwiftUI (iOS 17+) |
| Navigation | `NavigationStack` + `NavigationPath` |
| State | `@Observable` ViewModels |
| Logic | `SharedLogic.framework` use cases |
| DI | `KoinInitIosKt.doInitKoinIos()` |

### View pattern

```swift
struct HomeView: View {
    @Bindable var viewModel: HomeViewModel
    var body: some View {
        HomeContentView(state: viewModel.state, onEvent: viewModel.handle)
            .task { viewModel.start() }
    }
}
```

ViewModels call Kotlin **use cases** — never Room from Swift.

### Flow bridge

`Bridge/SharedLogic+Async.swift` — Kotlin `Flow` → `AsyncStream` (SKIE or hand-rolled).

### Theme

Mirror Android token names: `Color.moventiqPrimary`, `.moventiqTheme(colorScheme:)`, Jakarta Sans bundled.

### Component naming

| Android | iOS |
|---|---|
| `TaskRow` | `TaskRowView` |
| `LocationCard` | `LocationCardView` |
| `MoventiqBottomBar` | `MoventiqBottomBar` |

### Build

```bash
cd iosApp && xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17,OS=latest' build
```

---

## Design → code

Visual spec: `moventiq-pencil-design` + `Moventiq.pen`. Maps in Pencil are mockups — Android: Google Maps Compose; iOS: MapKit.
