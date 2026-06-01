---
name: moventiq-ui-tests
description: >-
  Writes Moventiq UI and instrumented tests — Compose UI tests, navigation flows,
  XCUITest. Use for end-to-screen behavior tests, not unit tests or previews.
---

# Moventiq UI tests

UI architecture (testTag, content split): `moventiq-ui-architecture`.

**Previews ≠ UI tests:** Previews validate design in Android Studio/Xcode. UI tests validate behavior in CI on device/simulator.

## Scope

| Type | Tool | When |
|---|---|---|
| Screen flow | Compose `ui-test-junit4` | Navigation, form submit, tab switching |
| Component semantics | Compose test | Optional — previews cover visual design |
| Screenshot regression | Roborazzi / Paparazzi | Optional visual CI |
| iOS flows | XCUITest | Critical paths on simulator |
| iOS structure | ViewInspector | Optional component assertions |

## Prerequisites

- `*Content(state, onEvent)` split — test content with fake state OR full flow via activity
- Stable selectors: `Modifier.testTag("task_row_buy_milk")` or semantics text
- Do not assert pixel colors — assert visibility, text, state

See `moventiq-ui-architecture/reference.md` for testTag conventions.

## Android setup

Dependencies (add to `gradle/libs.versions.toml`, apply in `:androidApp`):

```kotlin
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
androidTestImplementation(libs.koin.test.junit4)
```

### Content test (no navigation)

```kotlin
@Composable
private fun TestHost(state: HomeUiState) {
    MoventiqTheme { HomeContent(state = state, onEvent = {}) }
}

@Test
fun home_showsEmptyState_whenNoTasks() {
    composeTestRule.setContent { TestHost(HomeUiState.previewEmpty()) }
    composeTestRule.onNodeWithTag("home_empty_state").assertIsDisplayed()
}
```

### Flow test (with activity)

```kotlin
class CreateTaskFlowTest : KoinTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        stopKoin()
        startKoin { modules(testModule, sharedLogicModule) }
    }

    @After
    fun tearDown() = stopKoin()

    @Test
    fun createTask_appearsInTasksList() {
        composeRule.onNodeWithTag("tab_tasks").performClick()
        composeRule.onNodeWithTag("fab_add").performClick()
        composeRule.onNodeWithTag("task_title_input").performTextInput("Buy milk")
        composeRule.onNodeWithTag("save_task_button").performClick()
        composeRule.onNodeWithText("Buy milk").assertIsDisplayed()
    }
}
```

Use a `testModule` with fakes or in-memory Room overrides for isolated instrumented tests.

## Priority MVP flows

| # | Flow |
|---|---|
| 1 | Onboarding → permission → main |
| 2 | Create location → appears in Places list |
| 3 | Create task → linked to location → visible in Tasks |
| 4 | Tab navigation (Home, Tasks, Places, Settings) |
| 5 | Settings → appearance theme toggle |
| 6 | Mock geofence ENTER → Arrival screen visible |

## Commands

```bash
# Android — requires emulator or device
./gradlew :androidApp:connectedDebugAndroidTest

# iOS
xcodebuild test \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16'
```

Verify Xcode scheme name in `iosApp.xcodeproj` before hardcoding in CI.

## iOS (XCUITest)

```swift
func testTabBarNavigation() {
    let app = XCUIApplication()
    app.launch()
    app.buttons["tab_places"].tap()
    XCTAssertTrue(app.navigationBars["Places"].exists)
}
```

Add accessibility identifiers matching Android `testTag` names where possible.

## Definition of done

- [ ] Each MVP milestone screen has at least one flow or content test before milestone closes
- [ ] Interactive nodes in tested flows have `testTag` or stable accessibility ID
- [ ] Light/dark covered by previews; UI tests focus on behavior
- [ ] `./gradlew :androidApp:connectedDebugAndroidTest` green on CI emulator

## Related skills

- Previews / structure: `moventiq-ui-architecture`
- Unit tests: `moventiq-unit-tests`
- Review: `moventiq-code-review`
- CI: `moventiq-ci`
