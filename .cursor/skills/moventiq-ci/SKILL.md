---
name: moventiq-ci
description: >-
  Sets up and maintains GitHub Actions CI for Moventiq — Gradle tests, Android
  APK, iOS simulator build. Use when adding PR checks or fixing CI on develop.
---

# Moventiq CI

Follows the [Kotlin KMP GitHub Actions guide](https://kotlinlang.org/docs/multiplatform/github-actions-for-kmp.html).

Adapted for Moventiq module names (`:androidApp`, `:sharedLogic`, `iosApp/`).

Sibling skills: `kotlin-static-analysis`, `swift-static-analysis`, `kmp-ci-code-analysis`.

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
| `test` | `ubuntu-latest` | — | `:sharedLogic:testAndroidHostTest` `:sharedUI:testAndroidHostTest` |
| `build-android` | `ubuntu-latest` | `test` | `:androidApp:assembleDebug` |
| `build-ios` | `macos-latest` | `test` | `:sharedLogic:iosSimulatorArm64Test`, `xcodebuild build` |

### Why not `jvmTest` / `allTests` on Ubuntu?

Official Jetcaster sample uses `./gradlew jvmTest`. Moventiq has no root `jvmTest` task. `allTests` includes `iosSimulatorArm64Test`, which requires a Mac — so Linux runs **Android host tests only**; iOS Kotlin tests run in the `build-ios` job.

### Moventiq module map

| Path | Gradle / Xcode target |
|---|---|
| `sharedLogic/` | `:sharedLogic` |
| `androidApp/` | `:androidApp` |
| `iosApp/` | scheme `iosApp`, project `iosApp/iosApp.xcodeproj` |
| `sharedUI/` | deprecated — remove from CI when module is deleted |

**Do not** reference `composeApp`.

## Artifacts (official pattern)

- Test reports: `**/build/reports/tests/` → artifact `test-reports`
- Android APK: `androidApp/build/outputs/apk/debug/*.apk` → `android-apk`
- iOS app: `build/Build/Products/Debug-iphonesimulator/` → `iphonesimulator-app`

## Future: static analysis jobs

Add when detekt/ktlint/SwiftLint are wired (see `kotlin-static-analysis` skill):

```bash
./gradlew detekt ktlintCheck :androidApp:lintDebug
swiftlint lint --strict iosApp/
```

Prefer a separate workflow or extra job — keep `build.yml` fast for PR gating.

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

- [ ] `./gradlew :sharedLogic:testAndroidHostTest :sharedUI:testAndroidHostTest`
- [ ] `./gradlew :androidApp:assembleDebug`
- [ ] On Mac: `./gradlew :sharedLogic:iosSimulatorArm64Test`

## Related skills

- Unit tests: `moventiq-unit-tests`
- UI tests: `moventiq-ui-tests`
- Review: `moventiq-code-review`
