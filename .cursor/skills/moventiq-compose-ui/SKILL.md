---
name: moventiq-compose-ui
description: >-
  Builds Moventiq Android UI with Jetpack Compose — theme, components, navigation,
  ViewModels in :androidApp. Use for M0/M2 screens, Material 3, or Koin wiring.
---

# Moventiq Compose UI (M0 / M2)

Architecture and preview rules: `moventiq-ui-architecture`. Design spec: `moventiq-pencil-design`.

Full layout: [ARCHITECTURE.md](../../../ARCHITECTURE.md) §6.

## Target structure (`androidApp/`)

```
com.mohamedfaridelsherbini.moventiq/
├── MoventiqApplication.kt
├── MainActivity.kt
├── di/                    Koin modules (appModule + sharedLogicModule)
├── navigation/
│   MoventiqNavHost.kt
│   Routes.kt
│   BottomBar.kt           // Component/TabBar
├── ui/
│   theme/                 MoventiqTheme from DESIGN.md
│   components/            design-system composables
│   home/ tasks/ locations/ settings/ arrival/ …
├── service/
│   GeofenceBroadcastReceiver.kt
│   BootReceiver.kt
└── notification/
    ArrivalNotificationManager.kt
```

**Do not** add new UI to `:sharedUI` — it is deprecated.

## Stack

| Concern | Library |
|---|---|
| UI | Compose + Material 3 |
| Navigation | Navigation Compose (typed routes) |
| ViewModel | `lifecycle-viewmodel-compose` |
| DI | **Koin** (`koin-android`, `koin-compose-viewmodel`) |
| Maps | Google Maps Compose (Create/Edit Location) |
| Permissions | `ActivityResultContracts` or Accompanist |
| Flow collection | `collectAsStateWithLifecycle()` |

## Screen pattern

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(state = state, onEvent = viewModel::onEvent)
}

class HomeViewModel(
    private val observeLocations: ObserveActiveLocations,
    // … use cases only
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    fun onEvent(event: HomeEvent) { /* … */ }
}

// di/AppModule.kt
val appModule = module {
    viewModel { HomeViewModel(get(), /* … */) }
}
```

Bootstrap in `MoventiqApplication`:

```kotlin
startKoin {
    androidContext(this@MoventiqApplication)
    modules(sharedLogicModule, appModule)
}
```

ViewModels live in `:androidApp`. They call **use cases** from `:sharedLogic`, never DAOs.

## Theme (M0)

Implement once in `ui/theme/`:

- `MoventiqTheme(darkTheme) { }` wrapping `MaterialTheme`
- `MoventiqColors`, `MoventiqTypography`, `MoventiqShapes` from [DESIGN.md](../../../DESIGN.md)
- Plus Jakarta Sans via `compose.components.resources` or downloadable font
- `LocalMoventiqColors`, `LocalMoventiqSpacing` for extended tokens

## Navigation graph

```
splash → onboarding? → locationPermission → notificationPermission? → main
main (Scaffold + BottomBar):
  home
  tasks (+ taskDetail/{id}, createTask, editTask/{id}, searchTasks)
  places (+ createLocation, editLocation/{id}, locationTasks/{id}, searchPlaces)
  settings (+ notifications, geofencing, appearance, privacy, about)
arrival (full-screen overlay on geofence ENTER)
```

## TabBar / FAB

Five destinations: `Home · Tasks · (+) · Places · Settings`

Center FAB is **context-aware** by active tab:
- Places → navigate to create location
- Tasks → navigate to create task
- Home → default quick capture

No duplicate FAB on individual list screens.

## M0 deliverables

- [ ] `MoventiqTheme` + core components (TabBar, buttons, TaskRow, LocationCard, …)
- [ ] `MoventiqNavHost` + `MainActivity`
- [ ] Light + dark previews for every component

## M2 screen order

1. Home → 2. Places (list + create) → 3. Tasks (all + detail + create) → 4. Settings

Wire each screen to use cases; show loading/empty/error states from `Moventiq.pen`.

## Build gate

```bash
./gradlew :androidApp:assembleDebug
```

## Accessibility

- Interactive targets ≥44dp
- `contentDescription` on icon-only controls
- AA contrast via theme tokens

## Related skills

- UI architecture / previews: `moventiq-ui-architecture`
- Data wiring: `moventiq-room-kmp`
- Review: `moventiq-code-review`
