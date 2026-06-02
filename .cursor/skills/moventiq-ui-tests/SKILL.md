---
name: moventiq-ui-tests
description: >-
  Writes Moventiq UI and instrumented tests — Compose UI tests, navigation flows,
  XCUITest. Use for end-to-screen behavior tests, not unit tests or previews.
---

# Moventiq UI tests

Follow [Automate UI tests](https://developer.android.com/training/testing/ui-tests) and [Compose testing setup](https://developer.android.com/develop/ui/compose/testing).

UI architecture (testTag, content split): `moventiq-ui-architecture`.

**Previews ≠ UI tests:** Previews validate design in Android Studio/Xcode. UI tests validate behavior in CI on device/simulator.

## Test naming

Use **snake_case** with **2–3 segments**:

```text
{feature}_{visibleBehavior}_{condition}
```

| Segment | Content | Example |
|---|---|---|
| feature | screen or flow scope | `splash`, `app`, `home`, `tasks` |
| visibleBehavior | what the user sees (not implementation) | `shows_wordmark`, `navigates_to_home` |
| condition | context | `in_dark_theme`, `after_enter_animation`, `when_exiting`, `before_navigation_completes` |

Examples: `splash_shows_screen_root`, `splash_shows_brand_text_in_dark_theme`, `app_navigates_to_home_after_splash_completes`.

**How to rename**

1. **Refactor → Rename** (⇧F6) on the `@Test` method — same as unit tests.
2. Run one instrumented test (see **Commands** — `--tests` does **not** work on `connectedDebugAndroidTest`).
3. Prefer **behavior** verbs (`shows`, `navigates`, `displays`) over implementation (`renders`, `emits`, `callsViewModel`).
4. Match `testTag` nouns where helpful: `splash_shows_wordmark_*` ↔ `SplashTestTags.WORDMARK`.

Cross-ref: unit test naming in `moventiq-unit-tests`.

## Scope

| Type | Tool | When |
|---|---|---|
| Behavior UI test | Compose `ui-test-junit4` + semantics | Navigation, visibility, text, state |
| Component semantics | Compose test + fake `*UiState` | Screen content without navigation |
| Screenshot regression | Roborazzi / Paparazzi | Optional visual CI |
| iOS flows | XCUITest | Critical paths on simulator |

Behavior tests analyze the UI hierarchy and assert on element properties — not pixels. See [Behavior UI tests](https://developer.android.com/training/testing/ui-tests/behavior).

## Prerequisites

- `*Content(state, onEvent)` split — test content with fake state OR full flow via `setContent`
- Stable selectors: `Modifier.testTag("task_row_buy_milk")` or semantics text
- Replace dependencies with test doubles (in-memory repos, fake ViewModels) via DI — see [UI test architecture](https://developer.android.com/training/testing/ui-tests#architecture-and-test-setup)
- Do not assert pixel colors — assert visibility, text, state

See `moventiq-ui-architecture/reference.md` for testTag conventions.

## Android setup (`:androidApp`)

Instrumented tests live in `src/androidTest/kotlin`. Gradle builds a test APK and runs it on a device/emulator with `AndroidJUnitRunner`.

Version catalog keys already in `gradle/libs.versions.toml`:

- `[versions]` → `androidx-compose = "1.11.1"`
- `[libraries]` → `androidx-compose-ui-test-junit4`, `androidx-compose-ui-test-manifest` (both use `version.ref = "androidx-compose"`)

Do not duplicate keys under a different version alias in docs — copy from the repo file when bumping versions.

`androidApp/build.gradle.kts`:

```kotlin
defaultConfig {
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
}
testOptions {
    animationsDisabled = true
}

// Compose testing — https://developer.android.com/develop/ui/compose/testing
debugImplementation(libs.androidx.compose.ui.test.manifest)
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
androidTestImplementation(libs.androidx.testExt.junit)
androidTestImplementation(libs.androidx.test.runner)
androidTestImplementation(libs.androidx.test.rules)
androidTestImplementation(libs.androidx.espresso.core) // 3.7+ required for API 36 emulators
```

`ui-test-manifest` registers `ComponentActivity` for `createComposeRule()` — do not hand-roll a debug manifest.

### Content test (fake state, no navigation)

```kotlin
@RunWith(AndroidJUnit4::class)
class HomeContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun TestHost(state: HomeUiState) {
        MoventiqTheme { HomeContent(state = state, onEvent = {}) }
    }

    @Test
    fun home_shows_empty_state_when_no_tasks() {
        composeTestRule.setContent { TestHost(HomeUiState.previewEmpty()) }
        composeTestRule.onNodeWithTag("home_empty_state").assertIsDisplayed()
    }
}
```

### Flow test (test-double ViewModel)

Create the ViewModel **before** `setContent { }` — never construct it inside a `@Composable` (lint: `ViewModelConstructorInComposable`).

Inject deterministic timing or fakes instead of real network/DB:

```kotlin
@RunWith(AndroidJUnit4::class)
class SplashFlowTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun app_navigates_to_home_after_splash_completes() {
        val splashViewModel = SplashViewModel(
            enterWindowMs = 0L,
            exitDurationMs = 0L,
        )

        composeTestRule.setContent {
            MoventiqApp(
                splashViewModel = splashViewModel,
                onSplashDrawn = {},
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(HomeTestTags.SCREEN)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithTag(HomeTestTags.SCREEN).assertIsDisplayed()
    }
}
```

Use `createAndroidComposeRule<MainActivity>()` only when the test needs a real activity (system permissions, Koin graph, etc.). Prefer `createComposeRule()` from `androidx.compose.ui.test.junit4.v2`.

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

Gradle `--tests` filters **JVM unit tests only** (`testDebugUnitTest`). Instrumented UI tests use `android.testInstrumentationRunnerArguments` instead.

```bash
# Android unit tests (JVM — supports --tests)
./gradlew :androidApp:testDebugUnitTest \
  --tests "com.mohamedfaridelsherbini.moventiq.ui.splash.*"

# Android UI tests — all instrumented tests (requires emulator or device)
./gradlew :androidApp:connectedDebugAndroidTest

# Android UI tests — filter by package
./gradlew :androidApp:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.mohamedfaridelsherbini.moventiq.ui.splash

# Android UI tests — filter by class (comma-separated for multiple)
./gradlew :androidApp:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.mohamedfaridelsherbini.moventiq.ui.splash.SplashContentTest,com.mohamedfaridelsherbini.moventiq.ui.splash.SplashFlowTest

# Android UI tests — single method (Class#methodName)
./gradlew :androidApp:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.mohamedfaridelsherbini.moventiq.ui.splash.SplashContentTest#splash_shows_wordmark_when_exiting
```

# iOS — use shared scheme + test plan (iosApp/iosApp.xctestplan)
cd iosApp
xcodebuild test \
  -project iosApp.xcodeproj \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17,OS=latest'

# iOS unit tests only
xcodebuild test -project iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17,OS=latest' \
  -only-testing:iosAppTests
```

Use a simulator name from `xcrun simctl list devices available`. Prefer the shared scheme in `iosApp.xcodeproj/xcshareddata/xcschemes/` — a private scheme in `xcuserdata/` overrides it and can break `xcodebuild test`.

## iOS (XCUITest)

Targets: `iosAppUITests/` (flows), `iosAppTests/` (ViewModels). Match Android `testTag` names via `accessibilityIdentifier` on SwiftUI views.

Use launch arguments for deterministic splash timing (see `SplashViewModelFactory.makeSplashViewModel()`):

- `-UITestInstantSplash` — zero delay, navigates to home immediately
- `-UITestLongSplash` — keeps splash visible for UI assertions

```swift
func test_app_navigates_to_home_after_splash_completes() {
    let app = XCUIApplication()
    app.launchArguments.append("-UITestInstantSplash")
    app.launch()

    let home = app.descendants(matching: .any)["home_screen"]
    XCTAssertTrue(home.waitForExistence(timeout: 5))
}
```

Container views need `.accessibilityElement(children: .contain)` so XCUITest can query the identifier on the root.

## Definition of done

- [ ] Each MVP milestone screen has at least one flow or content test before milestone closes
- [ ] Interactive nodes in tested flows have `testTag` or stable accessibility ID
- [ ] Light/dark covered by previews; UI tests focus on behavior
- [ ] `./gradlew :androidApp:connectedDebugAndroidTest` green on CI emulator
- [ ] `xcodebuild test -project iosApp.xcodeproj -scheme iosApp` green on Mac CI simulator

## Related skills

- Previews / structure: `moventiq-ui-architecture`
- Unit tests: `moventiq-unit-tests`
- Review: `moventiq-code-review`
- CI: `moventiq-ci`
