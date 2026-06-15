---
name: moventiq-ci
description: >-
  Sets up and maintains GitHub Actions CI for Moventiq — Gradle tests, Android
  APK, iOS simulator build. Use when adding PR checks or fixing CI on develop.
---

# Moventiq CI

Follows the [Kotlin KMP GitHub Actions guide](https://kotlinlang.org/docs/multiplatform/github-actions-for-kmp.html).

Adapted for Moventiq module names (`:androidApp`, `:sharedLogic`, `iosApp/`).

Sibling skills (global/optional — not under `.cursor/skills/`): `kotlin-static-analysis`, `swift-static-analysis`, `kmp-ci-code-analysis`.

## Repo layout

```
.github/
├── actions/gradle-setup/action.yml   # reusable Java 17 + setup-gradle
└── workflows/build.yml               # test → build-android → build-ios
```

Target branch: **`develop`** (not `main`).

## Composite action (official pattern)

`.github/actions/gradle-setup/action.yml`:

- `actions/setup-java@v4` — Java **17**, Temurin
- `gradle/actions/setup-gradle@v5` — caching + consistent Gradle (replaces manual `actions/cache`)

Do not duplicate Gradle cache config when using `setup-gradle`.

## Workflow env

```yaml
env:
  GRADLE_OPTS: "-Dorg.gradle.jvmargs=-Xmx4096M -Dorg.gradle.daemon=false -Dorg.gradle.parallel=true -Dorg.gradle.caching=true"
```

Matches Kotlin docs: no daemon in CI, parallel + build cache enabled.

## Jobs

| Job | Runner | Needs | Command |
|---|---|---|---|
| `static-analysis` | `ubuntu-latest` | — | `./gradlew staticAnalysis` (gates `build-android` + `build-ios`; see § Static analysis) |
| `test` | `ubuntu-latest` | — | `:sharedLogic:testAndroidHostTest` `:sharedUI:testAndroidHostTest` (interim; drop `:sharedUI` when the module is deleted) |
| `build-android` | `ubuntu-latest` | `test`, `static-analysis` | `:androidApp:assembleDebug` |
| `build-ios` | `macos-latest` | `test`, `static-analysis` | `:sharedLogic:iosSimulatorArm64Test`, SwiftLint, `xcodebuild test` |
| `android-ui-test` | `ubuntu-latest` | `build-android` | `:androidApp:pixel6Api36DebugAndroidTest` |

### Why not `jvmTest` / `allTests` on Ubuntu?

Official Jetcaster sample uses `./gradlew jvmTest`. Moventiq has no root `jvmTest` task. `allTests` includes `iosSimulatorArm64Test`, which requires a Mac — so Linux runs **Android host tests only**; iOS Kotlin tests run in the `build-ios` job.

### Moventiq module map

| Path | Gradle / Xcode target |
|---|---|
| `sharedLogic/` | `:sharedLogic` (interim; → `shared/core`, `shared/feature`) |
| `shared/core/`, `shared/feature/` | target KMP modules (when extracted) |
| `androidApp/` | `:androidApp` |
| `iosApp/` | scheme `iosApp`, project `iosApp/iosApp.xcodeproj` |
| `sharedUI/` | deprecated — remove from CI when module is deleted |

**Do not** reference `composeApp`.

## Artifacts (official pattern)

- Test reports: `**/build/reports/tests/` → artifact `test-reports`
- Android APK: `androidApp/build/outputs/apk/debug/*.apk` → `android-apk`
- iOS app: `build/Build/Products/Debug-iphonesimulator/` → `iphonesimulator-app`

## Static analysis (CI + local)

| Tool | Scope | Command |
|---|---|---|
| detekt | `androidApp`, `sharedLogic` | `./gradlew detekt` |
| ktlint | `androidApp`, `sharedLogic` | `./gradlew ktlintCheck` / `ktlintFormat` |
| Android Lint | `androidApp` | `./gradlew :androidApp:lintDebug` |
| SwiftLint | `iosApp/iosApp` | `swiftlint lint --strict --config iosApp/.swiftlint.yml iosApp/iosApp` |
| **All Kotlin** | aggregate | `./gradlew staticAnalysis` |

CI job **`static-analysis`** runs `./gradlew staticAnalysis` on Ubuntu. **`build-ios`** runs SwiftLint on macOS. Both gate `build-android` and `build-ios`.

Config: `.editorconfig`, `config/detekt/detekt.yml`, `iosApp/.swiftlint.yml`. Deprecated `sharedUI` is excluded from detekt/ktlint.

## Future: additional analysis

Optional nightly: `xcodebuild analyze` (see `swift-static-analysis` skill).

## iOS build notes

- Xcode Run Script phase calls `:sharedLogic:embedAndSignAppleFrameworkForXcode` during `xcodebuild`
- Use `CODE_SIGNING_ALLOWED=NO` for simulator builds (no secrets)
- Verify scheme name in Xcode before changing workflow YAML

## Agent workflow

1. Inspect `.github/workflows/` before editing
2. Reuse `./.github/actions/gradle-setup` in every Gradle job
3. Do not add signing secrets unless user explicitly needs device/release builds
4. Upload test reports with `if: always()` on the test job

## Local PR checklist

- [ ] `./gradlew staticAnalysis`
- [ ] `./gradlew :sharedLogic:testAndroidHostTest :sharedUI:testAndroidHostTest` (`:sharedUI` interim — drop when the module is deleted)
- [ ] `./gradlew :androidApp:assembleDebug`
- [ ] `./gradlew :androidApp:pixel6Api36DebugAndroidTest` (or CI `android-ui-test` job)
- [ ] `xcodebuild test` on `iosApp` scheme (shared `xcshareddata`, not empty xcuserdata)
- [ ] On Mac: `swiftlint lint --strict --config iosApp/.swiftlint.yml iosApp/iosApp`
- [ ] On Mac: `./gradlew :sharedLogic:iosSimulatorArm64Test`

## Related skills

- Unit tests: `moventiq-unit-tests`
- UI tests: `moventiq-ui-tests`
- Review: `moventiq-code-review`
