# AGENT.md — Moventiq engineering guide

Guidance for AI agents and developers. Read with [MVP.md](MVP.md), [ARCHITECTURE.md](ARCHITECTURE.md), [DESIGN.md](DESIGN.md), and [RESOURCES.md](RESOURCES.md) (external references).

**Skills & rules:** `.cursor/skills/`, `.cursor/rules/`. Run **`moventiq-pipeline`** (verify · implement · review · data) before merge.

---

## 1. What this is

**Moventiq** — location-aware productivity. Tasks link to places; geofencing surfaces them on arrival. *"The right task. At the right place."*

**Kotlin Multiplatform** with **native UIs** (Android Compose, iOS SwiftUI) and **feature-first shared modules**.

**MVP is local-only:** no profile, sign-in, or cloud. Settings = app preferences only. See [MVP.md](MVP.md).

---

## 2. Architecture & modules

### Target (feature-first)

| Area | Path | Role |
|---|---|---|
| `:androidApp` | `androidApp/` | Compose UI, navigation, **platform ViewModels**, Koin app module |
| `:iosApp` | `iosApp/` | SwiftUI, **platform ViewModels**, `SharedLogic.framework` |
| `shared/core/*` | `shared/core/` | database, geofencing, notifications, network (future), common |
| `shared/feature/*` | `shared/feature/` | home, tasks, locations, arrival, settings (domain/data/presentation) |
| `:sharedLogic` | `sharedLogic/` | **Interim monolith** — migrate into `shared/` per [ARCHITECTURE.md](ARCHITECTURE.md) §14 |
| `:sharedUI` | `sharedUI/` | **Deprecated** — do not add code |

### Layering

```
androidApp / iosApp (native UI + ViewModels)
        ↓ use cases only
shared/feature/*/domain
        ↓
shared/feature/*/data  →  shared/core/database
shared/core/geofencing, notifications, location
```

**Rules:** UI never imports DAOs. Domain never imports Room/SQLDelight/Ktor. Features do not depend on each other’s `data`.

**Package:** `com.mohamedfaridelsherbini.moventiq`

---

## 3. Tech stack

| Layer | Technology |
|---|---|
| Shared | Kotlin Multiplatform, Coroutines, Flow, **Koin**, kotlinx.serialization |
| Persistence (MVP) | **Room 3** → `core/database` |
| Persistence (scale) | SQLDelight (optional migration) |
| Network (post-MVP) | **Ktor** → `core/network` |
| Android | Jetpack Compose, Material 3, Navigation Compose |
| iOS | SwiftUI, NavigationStack |

Versions: `gradle/libs.versions.toml` only — never hardcode in module `build.gradle.kts`.

**Koin (KMP):** [Kotlin Multiplatform setup](https://insert-koin.io/docs/reference/koin-core/kmp-setup/) — full link list in [RESOURCES.md](RESOURCES.md)

---

## 4. Build / run / test

```bash
./gradlew :androidApp:assembleDebug
./gradlew :sharedLogic:testAndroidHostTest :sharedLogic:iosSimulatorArm64Test
./gradlew :androidApp:testDebugUnitTest
./gradlew :androidApp:pixel6Api36DebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.mohamedfaridelsherbini.moventiq.ui.splash

cd iosApp && xcodebuild test -project iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro,OS=latest' CODE_SIGNING_ALLOWED=NO
```

iOS: open `iosApp/` in Xcode. Use **shared** scheme in `xcshareddata/xcschemes/` (not empty `xcuserdata` overrides).

---

## 5. Design system → code

Tokens: **DESIGN.md** only — no hardcoded hex/spacing in screens.

| Layer | Android | iOS | Shared |
|---|---|---|---|
| Numeric tokens | `MoventiqTheme` | `MoventiqTheme` | `core/designsystem` |
| Components | `androidApp/.../components/` | `iosApp/.../Components/` | — |
| Screens | `androidApp/.../screens/` | `iosApp/.../Screens/` | — |

Both **light and dark** for every shipped screen.

---

## 6. Geofencing

| Piece | Owner |
|---|---|
| Contract + OS sync | `shared/core/geofencing` (`GeofenceRegistry`, `GeofenceEventSource`) |
| Location persistence | `shared/feature/locations` |
| ENTER handling + arrival UX | `shared/feature/arrival` |
| Android receiver | `androidApp` + `core/geofencing` androidMain |
| iOS monitoring | `iosApp` + `core/geofencing` iosMain |

On ENTER → update `lastTriggeredAt`, show Arrival, notify (respect settings).

**Interim:** `GeofenceManager` expect in `:sharedLogic` — move to `core/geofencing`.

---

## 7. Conventions

- **State:** `XUiState`, `XEvent` / `XAction`, `StateFlow` in platform ViewModels.
- **Persistence:** Room in shared data layer; repositories expose `Flow`.
- **No profile UI** — no avatar, email, sign-in, cloud sync.
- **Accessibility:** ≥44dp targets, semantics on icon-only controls.
- **commonMain:** platform-agnostic; `expect`/`actual` in `core/*` only.
- **Splash / onboarding:** app-local (`androidApp` / `iosApp`), not `shared/feature`.

---

## 8. Design file

- `Moventiq.pen` — Pencil MCP only (encrypted).
- Layout from `.pen`; numeric tokens from `DESIGN.md`.

---

## 9. Definition of done

- `./gradlew :androidApp:assembleDebug` green
- Tests pass for touched modules
- UI matches `.pen` + tokens, light + dark
- New logic has unit tests; new UI has previews + behavior tests where applicable
- Update docs if conventions change

---

## 10. Current state

- **Done:** Splash (Android + iOS), Koin bootstrap (`sharedLogic/di`), theme scaffolding.
- **Next (M1):** Room schema, feature packages (tasks, locations, settings) inside `:sharedLogic` or extracted modules.
- **Design:** complete in `.pen` / `DESIGN.md`.

See [ARCHITECTURE.md](ARCHITECTURE.md) for full folder trees, diagrams, and migration steps.
