---
name: moventiq-ui-tests
description: >-
  Writes Moventiq UI and instrumented tests — Compose UI tests, navigation flows,
  XCUITest. Use for end-to-screen behavior tests, not unit tests or previews.
---

# Moventiq UI tests

**Feature workflow:** Write UI tests in **Phase B** before production code (`moventiq-feature-workflow`).

**Previews ≠ UI tests.** testTag / accessibility IDs: `../moventiq-ui-architecture/reference.md`.

## Naming

snake_case `{feature}_{visibleBehavior}_{condition}`

Examples: `app_showsNotification_afterLocationLater`, `permissionDenied_limitedFeatures_emitsEvent`.

Use behavior verbs: `shows`, `navigates`, `reaches` — not `renders`, `emits`.

## Scope

| Type | Tool | When |
|---|---|---|
| Content smoke | `createComposeRule` + fake state | Headlines, testTags |
| Flow | `MoventiqApp` + fake ViewModels | Onboarding → permission → home |
| iOS | XCUITest + `launchArguments` | Same critical paths |

Do not assert pixel colors.

## Android patterns

**Content test** — no ViewModel:

```kotlin
composeTestRule.setContent {
    MoventiqTheme { LocationPermissionContent(onEvent = {}) }
}
composeTestRule.onNodeWithTag(PermissionTestTags.LOCATION_SCREEN).assertIsDisplayed()
```

**Flow test** — hoist ViewModel **before** `setContent`:

```kotlin
val splashViewModel = SplashViewModel(enterWindowMs = 0L, exitDurationMs = 0L)
val permissionViewModel = PermissionFlowViewModel(
    statusStore = FreshPermissionStatusStore(),
    statusChecker = FakePermissionStatusChecker(),
)
composeTestRule.setContent {
    MoventiqApp(
        splashViewModel = splashViewModel,
        permissionViewModel = permissionViewModel,
        onSplashDrawn = {},
    )
}
```

Use `createComposeRule()` (v2). `createAndroidComposeRule` only when activity/Koin required.

After pager clicks, `waitUntil` next screen tag before assert.

## iOS patterns

Launch args for timing: `-UITestInstantSplash`, `-UITestFreshOnboarding`, `-UITestPermissionDenied`.

```swift
let screen = app.descendants(matching: .any)["permission_location_screen"]
XCTAssertTrue(screen.waitForExistence(timeout: 5))
```

Root views: `.accessibilityElement(children: .contain)`.

## MVP priority flows

1. Onboarding → permissions → main
2. Create location → Places list
3. Create task → Tasks list
4. Tab navigation
5. Mock geofence → Arrival (M3)

## Commands

Gradle `--tests` works for **unit tests only**, not instrumented.

```bash
# Android UI — managed emulator (CI parity)
./gradlew :androidApp:pixel6Api36DebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.mohamedfaridelsherbini.moventiq.ui.permissions

# Android UI — filter by class
./gradlew :androidApp:pixel6Api36DebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.mohamedfaridelsherbini.moventiq.ui.permissions.PermissionFlowTest

# iOS
cd iosApp && xcodebuild test -project iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17,OS=latest' CODE_SIGNING_ALLOWED=NO
```

More CI detail: `moventiq-ci`.

## Definition of done

- [ ] Critical flow has content or flow test
- [ ] `testTag` / accessibility ID on tested controls
- [ ] `./gradlew :androidApp:pixel6Api36DebugAndroidTest` green
- [ ] `xcodebuild test` green (Mac)

## Related

`moventiq-unit-tests` · `moventiq-ui-architecture` · `moventiq-code-review` · `moventiq-ci`
