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
2. [ARCHITECTURE.md](../../../ARCHITECTURE.md) — **feature-first** modules, dependencies, geofencing, testing
3. [AGENT.md](../../../AGENT.md) — conventions, build commands
4. [DESIGN.md](../../../DESIGN.md) — color/typography/spacing tokens
5. [RESOURCES.md](../../../RESOURCES.md) — Koin KMP ([setup guide](https://insert-koin.io/docs/reference/koin-core/kmp-setup/)) and other links

Agent skills and rules: `.cursor/skills/`, `.cursor/rules/`.

**Run skills in order:** `moventiq-pipeline` (verify · implement · review · data).

## Modules

| Module | Path | Role |
|---|---|---|
| `:androidApp` | `androidApp/` | Compose UI, navigation, **platform ViewModels**, Koin app module |
| `:iosApp` | `iosApp/` | SwiftUI, platform ViewModels, `SharedLogic.framework` |
| `shared/feature/*` | *(target)* | home, tasks, locations, arrival, settings — domain/data/presentation |
| `shared/core/*` | *(target)* | database, geofencing, notifications, network (future), common |
| `:sharedLogic` | `sharedLogic/` | **Interim** monolith — migrate per ARCHITECTURE §14 |
| `:sharedUI` | `sharedUI/` | **Deprecated** — do not add code |

**Package:** `com.mohamedfaridelsherbini.moventiq`

**Layering:** Platform UI → use cases → repositories → database. UI never imports DAOs. Features do not depend on each other's `data`.

## Hard constraints

- **Local-only:** no profile, auth, cloud sync, integrations (MVP)
- **Native UIs:** Compose on Android, SwiftUI on iOS
- **Feature-first:** avoid monolithic `shared/domain` + `shared/data` modules
- **Both themes:** light + dark for every shipped screen
- **Design file:** Pencil MCP for `Moventiq.pen` (see `moventiq-pencil-only.mdc`)
- **Deps:** `gradle/libs.versions.toml` only

## Milestones (MVP.md §9)

| # | Focus |
|---|---|
| M0 | Theme, design-system components, nav scaffold |
| M1 | Database + `feature/tasks`, `feature/locations`, `feature/settings` (interim: `:sharedLogic`) |
| M2 | CRUD UI — Home, Places, Tasks, Settings |
| M3 | `core/geofencing` + notifications + arrival |
| M4 | Onboarding, empty states, polish, accessibility |
| M5 | iOS parity |

## Build / test

```bash
./gradlew :androidApp:assembleDebug
./gradlew :sharedLogic:testAndroidHostTest
./gradlew :sharedLogic:iosSimulatorArm64Test   # Mac only
./gradlew :androidApp:pixel6Api36DebugAndroidTest
```

## Definition of done (per change)

- `./gradlew :androidApp:assembleDebug` green
- Tests pass for touched modules
- UI matches `Moventiq.pen` within tokens, light + dark
- No hardcoded design values; no platform APIs in shared `domain`
- Accessibility: labels + ≥44dp touch targets
- New use cases / platform ViewModels have unit tests

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
