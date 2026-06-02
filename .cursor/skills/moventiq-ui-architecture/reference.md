# Moventiq UI architecture — reference

See [ARCHITECTURE.md](../../../ARCHITECTURE.md) for module graph and dependency rules.

## Folder templates

### Android (`androidApp`)

```
androidApp/src/main/kotlin/com/mohamedfaridelsherbini/moventiq/
├── navigation/
├── screens/             home, tasks, locations, settings, arrival, splash
├── components/
├── theme/
└── di/                  appModule — ViewModels only
```

### iOS (`iosApp`)

```
iosApp/iosApp/
├── Screens/             Home, Tasks, Locations, Settings, Arrival, Splash
├── Components/
├── Theme/
├── Navigation/
├── App/                 MoventiqApp, AppDependencies
└── Bridge/              SharedLogic+Async.swift
```

### Shared feature (state for native UI)

```
shared/feature/<name>/presentation/
├── state/
├── action/
├── event/
└── viewmodel/           optional KMP store — platform may wrap use cases instead
```

## Preview fixtures

### Android — UiState companion

```kotlin
data class TaskRowUiState(
    val id: String,
    val title: String,
    val locationName: String?,
    // …
) {
    companion object {
        fun preview() = TaskRowUiState(/* … */)
    }
}
```

### iOS — static preview state

```swift
extension TasksUiState {
    static let preview = TasksUiState(/* … */)
}
```

## testTag conventions

- Screen root: `{feature}_screen` (e.g. `splash_screen`, `home_screen`)
- Key nodes: `{feature}_{element}` (e.g. `splash_wordmark`)
- Match XCUITest `accessibilityIdentifier` on SwiftUI
