---
name: moventiq-ui-architecture
description: >-
  Moventiq UI clean architecture, SOLID rules, file splits, and preview
  requirements for Compose and SwiftUI. Use when implementing, reviewing, or
  testing any UI screen or component.
---

# Moventiq UI architecture

## Layer model

| Layer | Location | Depends on |
|---|---|---|
| Domain | `shared/feature/*/domain` (interim `:sharedLogic`) | Nothing platform-specific |
| Data | `shared/feature/*/data` | Domain interfaces only |
| Presentation | `:androidApp`, `:iosApp` | Use cases only — **never DAOs/Room** |

## SOLID (UI)

| Principle | Rule |
|---|---|
| Single responsibility | `FooScreen` wires VM; `FooContent` renders; components are dumb |
| Open/closed | Extend via `FooEvent` variants or component params, not screen forks |
| Liskov | `*Content(state, onEvent)` same contract in preview, test, production |
| Interface segregation | Small `sealed interface FooEvent`, not one mega-callback |
| Dependency inversion | Platform ViewModels inject use cases from `shared/feature/*`, not DAOs |

## Mandatory file split

### Android (`androidApp/.../ui/<feature>/`)

```
HomeScreen.kt       // koinViewModel(), collect state
HomeContent.kt      // stateless: HomeContent(state, onEvent)
HomeViewModel.kt    // StateFlow + onEvent → use cases
HomeUiState.kt
HomeEvent.kt
HomePreview.kt      // @Preview light + dark
```

Components live in `ui/components/` with co-located `*Preview.kt`.

### iOS (`iosApp/iosApp/Features/<Feature>/`)

```
HomeView.swift          // binds ViewModel
HomeContentView.swift   // init(state:onEvent:)
HomeViewModel.swift
HomeUiState.swift
HomeEvent.swift
HomeView+Preview.swift  // #Preview light + dark
```

## Preview rules (required)

Every **component** and every **`*Content`** must have previews:

| Requirement | Detail |
|---|---|
| Themes | Light **and** dark wrapped in `MoventiqTheme` |
| Data | Fake `UiState` fixtures — no ViewModel, Koin, or Room |
| States | Default, empty, loading, error where applicable |
| Tokens | Only theme accessors — no hardcoded hex |

See [reference.md](reference.md) for code templates and fixture patterns.

## Unidirectional state

```kotlin
// Android
data class HomeUiState(val tasks: List<TaskItem> = emptyList())

sealed interface HomeEvent {
    data class CompleteTask(val id: String) : HomeEvent
}

class HomeViewModel(
    private val observeTasks: ObserveAllActiveTasks,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CompleteTask -> { /* call use case */ }
        }
    }
}
// Koin: viewModel { HomeViewModel(get()) }
```

```swift
// iOS
@Observable
final class HomeViewModel {
    var state = HomeUiState()
}
```

## UI test hooks

Add stable `Modifier.testTag(...)` / accessibility identifiers on interactive nodes used in UI tests. Do not assert raw pixel colors.

## Verify pipeline gate

When `moventiq-pipeline` **verify** includes UI changes, audit the diff against this checklist **before** unit/UI tests. Fail the gate (❌) if any required item is missing; report ⏭️ skip when no UI files changed.

### Per screen / feature (Android + iOS parity when both platforms ship)

- [ ] `*Screen` / `*View` wires ViewModel; `*Content` / `*ContentView` is stateless `(state, onEvent)`
- [ ] `*ViewModel`, `*UiState`, `*Event` (or Swift equivalents) present
- [ ] ViewModel calls use cases or app-local stores — **never** DAOs/Room directly
- [ ] Unidirectional flow: events in, state out (`StateFlow` / `@Observable`)
- [ ] Light **and** dark previews for every `*Content` / component touched
- [ ] `testTag` / `accessibilityIdentifier` on screen root and interactive controls used in tests
- [ ] Theme tokens only — no hardcoded hex/dp/font sizes in UI code

### Components

- [ ] Stateless — props + callback only
- [ ] Co-located previews (light + dark)

See [reference.md](reference.md) for folder templates and testTag naming.

## Component checklist (new component)

- [ ] Stateless — receives state + event callback
- [ ] Uses `MoventiqTheme` tokens only
- [ ] Light + dark `@Preview` / `#Preview`
- [ ] Touch targets ≥44dp; contentDescription on icon-only controls
- [ ] Matches `Moventiq.pen` component (fetch screenshot first)

## Related skills

- Orchestrator: `moventiq-pipeline`
- Platform UI (Compose / SwiftUI): [platforms.md](platforms.md)
- Design file: `moventiq-pencil-design`
- Tests: `moventiq-unit-tests`, `moventiq-ui-tests`
- Review: `moventiq-code-review`
