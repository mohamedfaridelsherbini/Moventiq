# `<feature-name>` — test plan

Status: **draft** | tests-red | tests-green  
Design: [uml.md](./uml.md)

## Case coverage

| Case # | Description | Unit | UI Android | UI iOS |
|---|---|:---:|:---:|:---:|
| 1 | | ☐ | ☐ | ☐ |
| 2 | | ☐ | ☐ | ☐ |

## Unit tests

| Test name | Class | Case # | Platform |
|---|---|---|---|
| | `FeatureViewModelTest` | 1 | Android |
| | `FeatureViewModelTests` | 1 | iOS |

### Commands

```bash
./gradlew :androidApp:testDebugUnitTest --tests "com.mohamedfaridelsherbini.moventiq.<package>.*"
cd iosApp && xcodebuild test -project iosApp.xcodeproj -scheme iosApp \
  -only-testing:iosAppTests/FeatureViewModelTests CODE_SIGNING_ALLOWED=NO
```

## UI tests

| Test name | Class | Case # | Platform |
|---|---|---|---|
| | `FeatureFlowTest` | 1 | Android |
| | `FeatureFlowUITests` | 1 | iOS |

### Commands

```bash
./gradlew :androidApp:pixel6Api36DebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.mohamedfaridelsherbini.moventiq.ui.<feature>
```

## Out of scope

- Items intentionally covered only at unit layer (e.g. lifecycle / foreground simulation)

## Red → green log

| Date | Notes |
|---|---|
| | Tests added, expected failures: … |
| | Implementation complete, all green |
