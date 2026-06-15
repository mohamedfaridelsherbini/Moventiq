---
name: moventiq-unit-tests
description: >-
  Writes and runs Moventiq unit tests for use cases, repositories, DAOs, and
  ViewModels. Use when adding or fixing business-logic tests — not UI/instrumented
  tests.
---

# Moventiq unit tests

Strategy table: [ARCHITECTURE.md](../../../ARCHITECTURE.md) §13.

**Feature workflow:** For new features, write tests in **Phase B** (before production code). See `moventiq-feature-workflow`.

**Not in scope:** Compose UI tests, XCUITest — see `moventiq-ui-tests`.

## Test naming

Use **snake_case** with **2–3 segments** separated by a single `_`:

```text
{subject}_{outcome}_{condition}
```

| Segment | Unit test | Example |
|---|---|---|
| subject | method, event, property, or state | `initial_state`, `on_event_content_drawn`, `enter_window` |
| outcome | expected result | `is_visible_and_not_complete`, `transitions_to_exiting_then_complete` |
| condition | given/when context | `when_phase_is_exiting`, `elapsed` — omit for default/happy path |

Examples: `initial_state_is_visible_and_not_complete`, `on_event_content_drawn_does_not_change_state`, `enter_window_elapsed_transitions_to_exiting_then_complete`.

**How to rename**

1. **Android Studio / Cursor:** place caret on the test name → **Refactor → Rename** (⇧F6). Updates the `@Test` method only; class/file names stay `{TypeUnderTest}Test`.
2. **Gradle filter** after rename: `./gradlew :androidApp:testDebugUnitTest --tests "com.mohamedfaridelsherbini.moventiq.ui.splash.SplashViewModelTest.enter_window_elapsed_*"`
3. Do **not** use Kotlin backtick names (`` `given x when y` ``) — harder to filter in CI and inconsistent with Moventiq UI tests.
4. Class name = `{TypeUnderTest}Test` (e.g. `SplashViewModelTest`); one class per production type.

Cross-ref: UI test naming in `moventiq-ui-tests`.

## Scope by layer

| Layer | Module | Source set | What to test |
|---|---|---|---|
| Use case | `shared/feature/*` or `:sharedLogic` | `commonTest` | Given/when/then with fake repos |
| Mapper | `shared/feature/*/data` | `commonTest` | Entity ↔ domain mapping |
| Repository | `shared/feature/*` | `androidHostTest` / `iosTest` | Room integration |
| DAO | `shared/core/database` | `androidHostTest` / `iosTest` | Queries, FK, Flow |
| Shared store | `shared/feature/*/presentation` | `commonTest` | Turbine on state |
| ViewModel | `:androidApp` | `test` | Event → state with fake use cases |
| ViewModel | `:iosApp` | `*Tests` | Same with fake use cases |

## Commands

```bash
# sharedLogic — primary
./gradlew :sharedLogic:testAndroidHostTest
./gradlew :sharedLogic:iosSimulatorArm64Test    # Mac only

# androidApp ViewModels (once test source set configured)
./gradlew :androidApp:testDebugUnitTest
```

## Use case test pattern

```kotlin
// sharedLogic/src/commonTest/.../CreateTaskUseCaseTest.kt
class CreateTaskUseCaseTest {
    private val taskRepo = FakeTaskRepository()
    private val settingsRepo = FakeSettingsRepository(defaultPriority = Priority.MEDIUM)
    private val useCase = CreateTask(taskRepo, settingsRepo)

    @Test
    fun createTask_setsDefaultPriorityFromSettings() = runTest {
        useCase(CreateTaskParams(title = "Buy milk", locationId = "loc-1"))
        assertEquals(1, taskRepo.upserted.size)
        assertEquals(Priority.MEDIUM, taskRepo.upserted.first().priority)
    }
}
```

Keep fakes in `commonTest` — not in production source.

## DAO / repository test pattern

```kotlin
// sharedLogic/src/androidHostTest/.../TaskDaoTest.kt
@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var db: MoventiqDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MoventiqDatabase::class.java,
        ).build()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun observeActiveForLocation_excludesCompleted() = runTest {
        // insert entities, collect Flow with Turbine, assert
    }
}
```

Add `androidx.room:room-testing` for in-memory DB on Android host tests.

## ViewModel test pattern (Android)

No Koin in unit tests — construct ViewModel with fake use cases directly:

```kotlin
@Test
fun onCompleteTask_marksTaskCompleted() = runTest {
    val vm = HomeViewModel(fakeObserveTasks, fakeCompleteTask, /* … */)
    vm.onEvent(HomeEvent.CompleteTask("task-1"))

    assertTrue(vm.state.value.tasks.first { it.id == "task-1" }.isCompleted)
    // or assert fake was called
}
```

Use **Turbine** (`testIn`, `awaitItem`) for `StateFlow`/`Flow` assertions.

## Definition of done

- [ ] Every new **use case**: happy path + at least one error/edge case
- [ ] Every new **repository method**: DAO integration test
- [ ] Every new **ViewModel**: event → state test per significant event
- [ ] Fakes implement domain repository interfaces only
- [ ] Tests run green locally before PR

## Priority test targets (M1–M2)

| Use case | Cases |
|---|---|
| `CreateLocation` | valid coords, default radius |
| `DeleteLocation` | cascades task unlink (SET NULL) |
| `CreateTask` | defaults from settings |
| `CompleteTask` | sets completedAt |
| `ClearAllData` | wipes all tables |
| `SyncGeofences` | calls manager with active locations only |

## Related skills

- Data layer: `moventiq-room-kmp`
- Review: `moventiq-code-review`
- UI tests: `moventiq-ui-tests`
