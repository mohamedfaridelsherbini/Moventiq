---
name: moventiq-context
description: >-
  Moventiq project entry point — modules, constraints, doc map, milestones, and
  definition of done. Use for any implementation task in this repository.
---

# Moventiq context

## Product

**Moventiq** is a location-aware productivity app. Users attach tasks to places; on arrival, geofencing surfaces the right tasks. Brand: *"The right task. At the right place."*

Core loop (MVP success criteria):

```
Create Location → Create Task → Link to Location → Arrive → Task appears + notification → Complete
```

## Doc read order

1. [MVP.md](../../../MVP.md) — scope, screens, milestones
2. [ARCHITECTURE.md](../../../ARCHITECTURE.md) — modules, layers, Room schema, navigation
3. [AGENT.md](../../../AGENT.md) — conventions, build commands, geofencing contract
4. [DESIGN.md](../../../DESIGN.md) — color/typography/spacing tokens

Agent skills and rules: `.cursor/skills/`, `.cursor/rules/`.

**Run skills in order:** `moventiq-pipeline` (verify · implement · review · data).

## Modules

| Module | Path | Role |
|---|---|---|
| `:androidApp` | `androidApp/` | Jetpack Compose, Koin, Navigation, Android geofencing |
| `:iosApp` | `iosApp/` | SwiftUI, CoreLocation, links `SharedLogic.framework` |
| `:sharedLogic` | `sharedLogic/` | Domain, use cases, Room 3, repositories, expect/actual |
| `:sharedUI` | `sharedUI/` | **Deprecated** — do not add new UI here |

**Package:** `com.mohamedfaridelsherbini.moventiq`

**Layering:** UI → use cases → repositories → Room. UI never imports DAOs.

## Hard constraints

- **Local-only:** no profile, auth, cloud sync, integrations
- **Native UIs:** Compose on Android, SwiftUI on iOS
- **Both themes:** light + dark for every shipped screen
- **Design file:** use Pencil MCP for `Moventiq.pen` (see `.cursor/rules/moventiq-pencil-only.mdc`)
- **Deps:** add versions only in `gradle/libs.versions.toml`

## Milestones (MVP.md §9)

| # | Focus |
|---|---|
| M0 | Theme, design-system components, nav scaffold |
| M1 | Room schema, repositories, use cases in `:sharedLogic` |
| M2 | CRUD UI — Home, Places, Tasks, Settings wired to data |
| M3 | Android geofencing, permissions, arrival + notifications |
| M4 | Onboarding, empty states, polish, accessibility |
| M5 | iOS parity — SwiftUI + CoreLocation |

## Build / test

```bash
./gradlew :androidApp:assembleDebug
./gradlew :sharedLogic:testAndroidHostTest
./gradlew :sharedLogic:iosSimulatorArm64Test   # Mac only
```

## Definition of done (per change)

- `./gradlew :androidApp:assembleDebug` green
- Tests pass for touched modules
- UI matches `Moventiq.pen` within tokens, light + dark
- No hardcoded design values; no platform APIs in `commonMain` domain
- Accessibility: labels + ≥44dp touch targets
- New use cases/ViewModels have unit tests; new components have previews

## Related skills

| Task | Skill |
|---|---|
| **Run all skills in order** | `moventiq-pipeline` |
| Design file | `moventiq-pencil-design` |
| Room / data | `moventiq-room-kmp` |
| Android UI | `moventiq-compose-ui` |
| iOS UI | `moventiq-swiftui-ui` |
| Geofencing | `moventiq-geofencing` |
| UI structure / previews | `moventiq-ui-architecture` |
| Code review | `moventiq-code-review` |
| Unit tests | `moventiq-unit-tests` |
| UI tests | `moventiq-ui-tests` |
| CI | `moventiq-ci` |
