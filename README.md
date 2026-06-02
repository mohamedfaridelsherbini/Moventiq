# Moventiq

**The right task. At the right place.** — Location-aware productivity for Android and iOS.

## Architecture

- **Native UI:** Jetpack Compose (Android), SwiftUI (iOS)
- **Shared logic:** Kotlin Multiplatform, **feature-first** modules (`shared/feature/*`, `shared/core/*`)
- **Offline-first:** local database (Room 3 for MVP; SQLDelight optional at scale)
- **DI:** [Koin KMP](https://insert-koin.io/docs/reference/koin-core/kmp-setup/)

Full design: [ARCHITECTURE.md](ARCHITECTURE.md) · Product: [MVP.md](MVP.md) · Agents: [AGENT.md](AGENT.md)

## Repository layout

| Path | Role |
|---|---|
| `androidApp/` | Compose app, navigation, platform ViewModels |
| `iosApp/` | SwiftUI app, links `SharedLogic.framework` |
| `sharedLogic/` | **Interim** shared KMP (migrating to `shared/`) |
| `sharedUI/` | Deprecated — do not use |

**Target structure** (see ARCHITECTURE.md):

```
shared/core/      common, database, geofencing, notifications, …
shared/feature/   home, tasks, locations, arrival, settings
```

## Run

```bash
./gradlew :androidApp:assembleDebug
```

iOS: open `iosApp/` in Xcode, run scheme **iosApp**.

## Test

```bash
./gradlew :sharedLogic:testAndroidHostTest :sharedLogic:iosSimulatorArm64Test
./gradlew :androidApp:pixel6Api36DebugAndroidTest
cd iosApp && xcodebuild test -project iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro,OS=latest' CODE_SIGNING_ALLOWED=NO
```

## Docs

- [ARCHITECTURE.md](ARCHITECTURE.md) — modules, dependencies, geofencing, testing
- [DESIGN.md](DESIGN.md) — design tokens
- [MVP.md](MVP.md) — scope and milestones
- [RESOURCES.md](RESOURCES.md) — external references ([Koin KMP setup](https://insert-koin.io/docs/reference/koin-core/kmp-setup/), testing, CI)
