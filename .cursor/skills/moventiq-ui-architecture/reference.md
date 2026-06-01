# Moventiq UI architecture — reference

## Folder templates

### Android feature module

```
androidApp/src/main/kotlin/com/mohamedfaridelsherbini/moventiq/
├── ui/
│   ├── theme/           MoventiqTheme, Color, Type, Shape
│   ├── components/      TaskRow, LocationCard, MoventiqBottomBar, …
│   ├── home/
│   ├── tasks/
│   ├── locations/
│   ├── settings/
│   └── …
├── navigation/
│   MoventiqNavHost.kt, Routes.kt, BottomBar.kt
└── di/
```

### iOS feature module

```
iosApp/iosApp/
├── Features/Home/
├── UI/Theme/
├── UI/Components/
└── Bridge/SharedLogic+Async.swift
```

## Preview fixtures

### Android — UiState companion

```kotlin
data class TaskRowUiState(
    val id: String,
    val title: String,
    val locationName: String?,
    val isCompleted: Boolean,
    val priority: Priority,
) {
    companion object {
        fun preview(
            title: String = "Buy milk",
            isCompleted: Boolean = false,
        ) = TaskRowUiState(
            id = "preview-id",
            title = title,
            locationName = "Supermarket",
            isCompleted = isCompleted,
            priority = Priority.MEDIUM,
        )
    }
}
```

### Android — preview composable

```kotlin
@Preview(name = "TaskRow — Light", showBackground = true)
@Preview(
    name = "TaskRow — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun TaskRowPreview() {
    MoventiqTheme {
        TaskRow(
            state = TaskRowUiState.preview(),
            onEvent = {},
        )
    }
}
```

### Android — PreviewParameterProvider (optional, multi-state)

```kotlin
private class TaskRowPreviewProvider : PreviewParameterProvider<TaskRowUiState> {
    override val values = sequenceOf(
        TaskRowUiState.preview(),
        TaskRowUiState.preview(isCompleted = true),
        TaskRowUiState.preview(title = "Very long task title that wraps to two lines"),
    )
}

@Preview
@Composable
private fun TaskRowPreview(@PreviewParameter(TaskRowPreviewProvider::class) state: TaskRowUiState) {
    MoventiqTheme { TaskRow(state = state, onEvent = {}) }
}
```

### iOS — static preview fixture

```swift
extension HomeUiState {
    static let preview = HomeUiState(
        activeLocation: .init(name: "Supermarket", taskCount: 3),
        todayTasks: [.preview],
        isLoading: false
    )
}

#Preview("Home — Light") {
    HomeContentView(state: .preview, onEvent: { _ in })
        .moventiqTheme(colorScheme: .light)
}

#Preview("Home — Dark") {
    HomeContentView(state: .preview, onEvent: { _ in })
        .moventiqTheme(colorScheme: .dark)
}
```

## Screen content pattern

### Android

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeContent(state = state, onEvent = viewModel::onEvent)
}

@Composable
fun HomeContent(state: HomeUiState, onEvent: (HomeEvent) -> Unit) {
    // Pure UI — previewable, testable
}
```

### iOS

```swift
struct HomeView: View {
    @Bindable var viewModel: HomeViewModel
    var body: some View {
        HomeContentView(state: viewModel.state, onEvent: viewModel.handle)
    }
}
```

## Design-system components (build first in M0)

| Component | Key props / behavior |
|---|---|
| `MoventiqBottomBar` | 5 tabs, floating, center FAB, active state |
| `PrimaryButton` / `SecondaryButton` | Loading, disabled states |
| `TaskRow` | Checkbox, title, location tag, swipe actions, drag handle |
| `TaskChip` | Priority / reminder badge |
| `LocationCard` | Status pill, radius, task count, last-trigger |
| `ActiveLocationCard` | Live geofence status, triggered preview |
| `OfflineBanner` | Dismissible offline indicator |
| `MoventiqDialog` | Confirm/cancel destructive actions |
| `SectionHeader` | Title + optional action |

## testTag conventions

```
home_empty_state
task_row_{id}
location_card_{id}
tab_home / tab_tasks / tab_places / tab_settings
fab_add
```

Use semantics text when stable; prefer `testTag` for dynamic lists.
