# AGENT.md — Moventiq engineering guide

Guidance for AI agents and developers working in this repository. Read this with [MVP.md](MVP.md) (what to build), [ARCHITECTURE.md](ARCHITECTURE.md) (how modules/layers/DB are structured), and [DESIGN.md](DESIGN.md) (visual source of truth).

---

## 1. What this is

**Moventiq** is a location-aware productivity app — tasks are linked to places and surface on arrival via geofencing. Brand promise: *"The right task. At the right place."*

This is a **Kotlin Multiplatform** project: **native UIs** (Android Compose, iOS SwiftUI) over **shared logic + Room 3**.

**MVP is local-only:** no profile page, no sign-in, no cloud sync. All locations/tasks/settings persist in Room on device. The Settings tab is **app preferences**, not a user profile.

See [ARCHITECTURE.md](ARCHITECTURE.md) for the full module diagram, package layout, Room schema, navigation graphs, and migration plan.

---

## 2. Architecture & modules

| Module | Path | Responsibility |
|---|---|---|
| `:androidApp` | `androidApp/` | Jetpack Compose UI, Navigation, Hilt, Android geofencing/notifications |
| `:iosApp` | `iosApp/` | SwiftUI UI, NavigationStack, CoreLocation, links `SharedLogic.framework` |
| `:sharedLogic` | `sharedLogic/` | Domain, use cases, **Room 3** DB, repositories, `expect`/`actual` platform APIs |
| `:sharedUI` | `sharedUI/` | *(deprecated — remove after native UI migration)* |

**Base package:** `com.mohamedfaridelsherbini.moventiq`
**Source sets:** `commonMain` (shared), `androidMain`/`iosMain` (platform), `commonTest`/`androidHostTest`/`iosTest`.

### Layering rule
```
androidApp (Compose, ViewModels)  ─depends on→  sharedLogic (Room, repos, use cases)
iosApp (SwiftUI, ViewModels)      ─depends on→  sharedLogic (via SharedLogic.framework)
```
UI never talks to Room DAOs directly — ViewModels call use cases in `sharedLogic`.

---

## 3. Tech stack (from `gradle/libs.versions.toml`)

- Kotlin **2.3.21**, Compose Multiplatform **1.11.0**, Material3 **1.11.0-alpha07**
- AGP **9.0.1**, minSdk **24**, compile/target **36**
- Lifecycle ViewModel/runtime Compose, `compose.components.resources`
- Add new deps to the **version catalog** (`gradle/libs.versions.toml`), never hardcode versions in module `build.gradle.kts`.

---

## 4. Build / run / test

```bash
# Android debug build
./gradlew :androidApp:assembleDebug

# Run unit tests
./gradlew :sharedUI:testAndroidHostTest :sharedLogic:testAndroidHostTest
./gradlew :sharedLogic:iosSimulatorArm64Test

# iOS: open iosApp/ in Xcode and run
```
Gradle config-cache and build-cache are on. Prefer IDE run configs for day-to-day.

---

## 5. Design system → code

Tokens live in `DESIGN.md` (YAML front-matter) and are the canonical values. Implement them once as a Compose theme in `sharedUI` and **never hardcode hex/spacing in screens**.

### Color tokens (map to a `MoventiqColors` + `MaterialTheme`)
| Token | Light | Dark |
|---|---|---|
| primary | `#4F46E5` | `#4F46E5` |
| secondary | `#2563EB` | `#2563EB` |
| accent (cyan) | `#06B6D4` | `#06B6D4` |
| success / warning / error | `#10B981` / `#F59E0B` / `#EF4444` | same |
| bg | `#F8FAFC` | `#0F172A` |
| card | `#FFFFFF` | `#1E293B` |
| text-primary | `#0F172A` | `#F8FAFC` |
| text-secondary | `#475569` | `#94A3B8` |
| text-muted | `#64748B` | `#64748B` |
| hairline (border) | `#E2E8F0` | `#334155` |
| primary-container | `#EEF2FF` | (tinted) |

Provide light/dark via a `MoventiqTheme(darkTheme) { }` wrapper exposing `MaterialTheme` + an extended `LocalMoventiqColors`/`LocalMoventiqSpacing`.

### Typography
- Font: **Plus Jakarta Sans** (SemiBold/Bold for headings). Bundle via `compose.components.resources`.
- Scale: `display-lg, h1, h2, title, body-md, body-sm, caption` (see `DESIGN.md`).

### Shape / spacing / radius
- Radius scale: `sm, md, lg, xl, full` → map to `Shapes`.
- Spacing scale: `xs…xxl`. 8pt rhythm.

### Components (designed in `.pen` → build as composables in `sharedUI/.../ui/components/`)
| Design component | Composable |
|---|---|
| `Component/TabBar` | `MoventiqBottomBar` (Home·Tasks·FAB·Places·Settings, floating, states) |
| `Component/PrimaryButton` / `SecondaryButton` | `PrimaryButton`, `SecondaryButton` |
| `Component/TaskRow` | `TaskRow` (checkbox, title, location tag, swipe edit/delete, drag handle) |
| `Component/TaskChip` | `TaskChip` |
| `Component/LocationCard` | `LocationCard` (status pill, radius, count, last-trigger) |
| `Component/LocationBanner` | `ActiveLocationCard` (live status, triggered preview) |
| `Component/StatusBar` | system bar / scaffold inset (usually OS-provided) |
| `Component/MoventiqSymbol` | brand mark (vector asset in `brand-assets/`) |

### Screens (`sharedUI/.../ui/<feature>/`)
`splash, onboarding, permission, firstLocationSetup, home, locations, locationDetail (tasks), createLocation, createTask, arrival, settings`. Each = a stateless `XScreen(state, onEvent)` + a `XViewModel` (in `sharedLogic` or a `sharedUI` viewmodel layer).

> Brand assets (icons, splash, store art) are already generated in `brand-assets/` (SVG + Android vector drawables + iOS appiconset). Reuse them; don't regenerate.

---

## 6. Geofencing (the core feature)

Define an `expect` interface in `sharedLogic/commonMain`, implement per platform:

```kotlin
// commonMain
interface GeofenceManager {
    suspend fun sync(locations: List<Location>)   // register active, remove stale
    val events: Flow<GeofenceEvent>               // ENTER/EXIT with locationId
}
```
- **Android** (`androidMain` + `:androidApp`): `com.google.android.gms.location.GeofencingClient`, `PendingIntent` → `BroadcastReceiver`, re-register on `BOOT_COMPLETED`. Requires `ACCESS_FINE_LOCATION` + `ACCESS_BACKGROUND_LOCATION` (API 29+).
- **iOS** (`iosMain` + `:iosApp`): `CLLocationManager` region monitoring (`CLCircularRegion`), `UNUserNotificationCenter` for notifications, "Always" authorization.

On ENTER → mark `lastTriggeredAt`, surface triggered (incomplete, linked) tasks via notification + the `Arrival` screen.

---

## 7. Conventions

- **State:** unidirectional. `data class XState`, `sealed interface XEvent`, ViewModel exposes `StateFlow<XState>`. Screens are stateless and preview-able.
- **Persistence:** **Room 3** in `:sharedLogic` (entities/DAOs in `commonMain`, platform builders in `androidMain`/`iosMain`). Repositories return `Flow`. See [ARCHITECTURE.md](ARCHITECTURE.md) §5 for schema.
- **No profile UI:** do not add avatar, email, PRO badge, sign-in, or "Delete account". Use "Clear all data" in Settings → Privacy for a full local reset.
- **Naming:** screens `FooScreen`, viewmodels `FooViewModel`, composable components PascalCase, token accessors via `MoventiqTheme`.
- **No hardcoded colors/dimens** in UI — always theme tokens.
- **Accessibility:** every interactive node ≥44dp, `Modifier.semantics`/contentDescription on icon-only controls, AA contrast.
- **Both themes** must be implemented for every screen (design ships light + dark).
- Keep `commonMain` platform-agnostic; push platform calls behind `expect`/`actual`.

---

## 8. Working with the design file

- `pencil-new.pen` is the **encrypted Pencil design source**. Do **not** open/edit it with text/Read tools — use the Pencil MCP tools only.
- Treat the rendered screens as the visual spec; treat `DESIGN.md` tokens as the numeric spec. If they ever conflict, `DESIGN.md` wins for values, the `.pen` wins for layout/composition.
- Maps in the design are high-fidelity *mockups*. In code, drop a real `MapView` / Google Maps SDK (Android) or `MKMapView` (iOS) and bind the radius control to the geofence circle.

---

## 9. Definition of done (per change)

- Builds: `./gradlew :androidApp:assembleDebug` green.
- Tests pass for touched modules.
- New UI matches `.pen` within tokens, in **light and dark**.
- No hardcoded design values; no platform APIs leaked into `commonMain` UI.
- Accessibility: labels + touch targets verified.
- Update `MVP.md` milestone checkboxes / this file if conventions change.

---

## 10. Current state

Repo is a fresh KMP template (`Greeting`/`Platform` boilerplate in `sharedLogic`, starter `App.kt` in `sharedUI`). The **design is complete**; implementation starts at `MVP.md` → Milestone M0 (theme + components + nav scaffold). Replace the template boilerplate as features land.
