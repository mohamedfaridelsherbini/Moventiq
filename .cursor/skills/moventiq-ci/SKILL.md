---
name: moventiq-ci
description: >-
  Sets up and maintains GitHub Actions CI for Moventiq — Gradle tests, detekt,
  ktlint, Android Lint, SwiftLint. Use when adding PR checks or fixing CI on
  the develop branch.
---

# Moventiq CI

Adapted for Moventiq module names (`:androidApp`, `:sharedLogic`, `iosApp/`).

Sibling skills: `kotlin-static-analysis`, `swift-static-analysis`, `kmp-ci-code-analysis`.

## Goal

One PR proves:

- Kotlin modules compile and unit tests pass
- Kotlin static analysis passes (detekt + ktlint + Android Lint)
- SwiftLint passes when Swift files change
- Optional: Xcode Analyze on schedule (slow)

## Moventiq module map

| Path | Gradle / Xcode target |
|---|---|
| `sharedLogic/` | `:sharedLogic` |
| `androidApp/` | `:androidApp` |
| `iosApp/` | Xcode scheme `iosApp` (verify in `.xcodeproj` before hardcoding) |
| `sharedUI/` | Deprecated — exclude from new CI jobs when removed |

**Do not** reference `composeApp` — Moventiq uses `:androidApp`.

## Suggested job split

| Job | Trigger | Commands |
|---|---|---|
| `kotlin` | Every PR | See Kotlin job below |
| `swiftlint` | PRs touching `iosApp/**/*.swift` | `swiftlint lint --strict` |
| `analyze` | Nightly or `main`/`develop` schedule | `xcodebuild analyze` (optional) |

## Kotlin job

```bash
./gradlew \
  :sharedLogic:testAndroidHostTest \
  :androidApp:assembleDebug \
  :androidApp:lintDebug \
  detekt \
  ktlintCheck
```

Add detekt/ktlint plugins first if not yet wired — see `kotlin-static-analysis` skill.

### Gradle caching

```yaml
- uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
    key: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
```

## SwiftLint job

```yaml
on:
  pull_request:
    paths:
      - 'iosApp/**/*.swift'
      - '.swiftlint.yml'

steps:
  - run: swiftlint lint --strict iosApp/
```

Add `.swiftlint.yml` at repo root if missing — see `swift-static-analysis` skill.

## iOS build job (optional, M5+)

```bash
xcodebuild build \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 16' \
  CODE_SIGNING_ALLOWED=NO
```

Requires macOS runner. Cache DerivedData if runtime is high.

## Agent workflow

1. Inspect existing `.github/workflows/` before adding files
2. Reuse JDK version from `gradle/libs.versions.toml` / project config
3. Do not add signing secrets unless user explicitly needs device tests
4. Keep workflow files small; one workflow file per concern is fine
5. Target branch: `develop`

## Workflow file location

```
.github/workflows/
  kotlin-ci.yml       # detekt, ktlint, tests, lint, assembleDebug
  swiftlint.yml       # path-filtered
  ios-analyze.yml     # optional nightly
```

## PR checklist (before merge)

- [ ] `./gradlew :sharedLogic:testAndroidHostTest` green
- [ ] `./gradlew :androidApp:assembleDebug` green
- [ ] detekt + ktlint pass (or baseline documented)
- [ ] SwiftLint pass if Swift changed
- [ ] No secrets in workflow files

## Related skills

- Unit tests: `moventiq-unit-tests`
- UI tests (manual/CI emulator): `moventiq-ui-tests`
- Review: `moventiq-code-review`
- Keep PR green: `babysit` (personal skill)
