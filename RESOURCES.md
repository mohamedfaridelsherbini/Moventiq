# Moventiq — External resources

Canonical links for implementation and review. Prefer these over ad-hoc web search when working in this repo.

## Dependency injection (Koin)

| Resource | URL | Use when |
|---|---|---|
| **Koin — Kotlin Multiplatform setup** | [insert-koin.io/docs/reference/koin-core/kmp-setup](https://insert-koin.io/docs/reference/koin-core/kmp-setup/) | Bootstrapping Koin on Android/iOS, `expect`/`actual` `platformModule`, `initKoin`, Swift entry (`KoinInitIosKt`) — catalog version **4.2.1** |
| Koin — Starting Koin | [insert-koin.io/docs/reference/koin-core/starting-koin](https://insert-koin.io/docs/reference/koin-core/starting-koin/) | Modules, `startKoin`, app declarations |
| Koin — Modules & definitions | [insert-koin.io/docs/reference/koin-core/modules](https://insert-koin.io/docs/reference/koin-core/modules) | `single`, `factory`, qualifiers |
| Koin — Testing | [insert-koin.io/docs/reference/koin-test/testing](https://insert-koin.io/docs/reference/koin-test/testing) | `koin-test`, test modules (prefer fakes in unit tests) |
| Koin — Android | [insert-koin.io/docs/reference/koin-android/koin-android](https://insert-koin.io/docs/reference/koin-android/koin-android) | `androidContext`, ViewModel DSL |
| Koin — Compose | [insert-koin.io/docs/reference/koin-compose/compose](https://insert-koin.io/docs/reference/koin-compose/compose) | `koinViewModel()` in `:androidApp` |

**Moventiq wiring (interim):** `sharedLogic/di/` — `KoinModules.kt`, `KoinInit.kt`, `KoinInitIos.kt`, `PlatformModule.*.kt`. See [ARCHITECTURE.md](ARCHITECTURE.md) §11.

---

## Kotlin Multiplatform — learning

**Full curated index (30+ tutorials, courses, articles):**  
[Kotlin Multiplatform — Learning resources](https://kotlinlang.org/docs/multiplatform/kmp-learning-resources.html)

Moventiq uses **native UI** (Compose + SwiftUI) and shared logic in `:sharedLogic` → `shared/feature/*`. Prefer resources marked for native UIs, not Compose Multiplatform–only UI unless exploring `:sharedUI` (deprecated).

### Recommended for this project

| Level | Resource | URL | Why |
|---|---|---|---|
| 🌱 | KMP overview | [kotlinlang.org/docs/multiplatform/kmp-overview.html](https://kotlinlang.org/docs/multiplatform/kmp-overview.html) | Mental model: shared logic, native UI |
| 🌱 | Create your first KMP app | [kotlinlang.org/docs/multiplatform/multiplatform-create-first-app.html](https://kotlinlang.org/docs/multiplatform/multiplatform-create-first-app.html) | Project setup, shared module basics |
| 🌱 | Google KMP codelab | [developer.android.com/codelabs/kmp-get-started](https://developer.android.com/codelabs/kmp-get-started) | Add shared module to Android + iOS; SKIE / Swift API |
| 🌱 | expect / actual | [kotlinlang.org/docs/multiplatform/multiplatform-expect-actual.html](https://kotlinlang.org/docs/multiplatform/multiplatform-expect-actual.html) | Platform modules (`PlatformModule`, geofencing) |
| 🌿 | Integrate KMP in existing app | [kotlinlang.org/docs/multiplatform/multiplatform-integrate-in-existing-app.html](https://kotlinlang.org/docs/multiplatform/multiplatform-integrate-in-existing-app.html) | Moventiq-style split: `:androidApp` + `:iosApp` + shared |
| 🌿 | **Migrate to Room KMP** (Google codelab) | [developer.android.com/codelabs/kmp-migrate-room](https://developer.android.com/codelabs/kmp-migrate-room) | **M1** data layer — DAOs in shared module |
| 🌳 | Kotlin/Swift Interopedia | [github.com/kotlin-hands-on/kotlin-swift-interopedia](https://github.com/kotlin-hands-on/kotlin-swift-interopedia) | Swift export, Flow → Swift, iOS interop |
| 🌳 | KMP for native mobile teams | [touchlab.co/kmp-teams-intro](https://touchlab.co/kmp-teams-intro) | Team adoption, scaling shared code |

### KMP — docs & tooling (reference)

| Resource | URL |
|---|---|
| Room in KMP | [kotlinlang.org/docs/multiplatform/room.html](https://kotlinlang.org/docs/multiplatform/room.html) |
| KMP project with Android & iOS | [kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html](https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html) |
| GitHub Actions for KMP | [kotlinlang.org/docs/multiplatform/github-actions-for-kmp.html](https://kotlinlang.org/docs/multiplatform/github-actions-for-kmp.html) |
| KMP samples | [kotlinlang.org/docs/multiplatform/multiplatform-samples.html](https://kotlinlang.org/docs/multiplatform/multiplatform-samples.html) |
| KMP FAQ | [kotlinlang.org/docs/multiplatform/faq.html](https://kotlinlang.org/docs/multiplatform/faq.html) |

---

## Android — official

| Topic | Resource | URL |
|---|---|---|
| **Platform** | Android Developers | [developer.android.com](https://developer.android.com/) |
| **UI** | Jetpack Compose | [developer.android.com/jetpack/compose](https://developer.android.com/jetpack/compose) |
| **UI** | Compose Material 3 | [developer.android.com/develop/ui/compose/designsystems/material3](https://developer.android.com/develop/ui/compose/designsystems/material3) |
| **UI** | Compose testing | [developer.android.com/develop/ui/compose/testing](https://developer.android.com/develop/ui/compose/testing) |
| **Architecture** | Guide to app architecture | [developer.android.com/topic/architecture](https://developer.android.com/topic/architecture) |
| **Architecture** | ViewModel overview | [developer.android.com/topic/libraries/architecture/viewmodel](https://developer.android.com/topic/libraries/architecture/viewmodel) |
| **Architecture** | UI layer | [developer.android.com/topic/architecture/ui-layer](https://developer.android.com/topic/architecture/ui-layer) |
| **Data** | Room | [developer.android.com/training/data-storage/room](https://developer.android.com/training/data-storage/room) |
| **Location** | Geofencing | [developer.android.com/develop/sensors-and-location/location/geofencing](https://developer.android.com/develop/sensors-and-location/location/geofencing) |
| **Testing** | Test apps on Android | [developer.android.com/training/testing](https://developer.android.com/training/testing) |
| **Testing** | UI tests | [developer.android.com/training/testing/ui-tests](https://developer.android.com/training/testing/ui-tests) |
| **Testing** | Behavior UI tests | [developer.android.com/training/testing/ui-tests/behavior](https://developer.android.com/training/testing/ui-tests/behavior) |
| **A11y** | Accessibility on Android | [developer.android.com/guide/topics/ui/accessibility](https://developer.android.com/guide/topics/ui/accessibility) |
| **Build** | Gradle-managed devices | [developer.android.com/studio/test/gradle-managed-devices](https://developer.android.com/studio/test/gradle-managed-devices) |

**Moventiq Android UI:** `:androidApp` — Compose, Navigation Compose, Koin, platform ViewModels. See [ARCHITECTURE.md](ARCHITECTURE.md) §7.

---

## iOS — official

| Topic | Resource | URL |
|---|---|---|
| **Platform** | Apple Developer Documentation | [developer.apple.com/documentation](https://developer.apple.com/documentation/) |
| **UI** | SwiftUI | [developer.apple.com/documentation/swiftui](https://developer.apple.com/documentation/swiftui) |
| **UI** | SwiftUI tutorials | [developer.apple.com/tutorials/swiftui](https://developer.apple.com/tutorials/swiftui) |
| **Design** | Human Interface Guidelines | [developer.apple.com/design/human-interface-guidelines](https://developer.apple.com/design/human-interface-guidelines) |
| **Architecture** | Model data | [developer.apple.com/documentation/swiftui/model-data](https://developer.apple.com/documentation/swiftui/model-data) |
| **Interop** | SwiftUI + UIKit/AppKit | [developer.apple.com/documentation/swiftui/swiftui-app-organization](https://developer.apple.com/documentation/swiftui/swiftui-app-organization) |
| **Location** | Core Location | [developer.apple.com/documentation/corelocation](https://developer.apple.com/documentation/corelocation) |
| **Location** | Monitoring geographic regions | [developer.apple.com/documentation/corelocation/monitoring-the-user-s-proximity-to-geographic-regions](https://developer.apple.com/documentation/corelocation/monitoring-the-user-s-proximity-to-geographic-regions) |
| **Notifications** | User Notifications | [developer.apple.com/documentation/usernotifications](https://developer.apple.com/documentation/usernotifications) |
| **Testing** | XCTest | [developer.apple.com/documentation/xctest](https://developer.apple.com/documentation/xctest) |
| **Testing** | UI testing | [developer.apple.com/documentation/xctest/user-interface-tests](https://developer.apple.com/documentation/xctest/user-interface-tests) |
| **A11y** | Accessibility | [developer.apple.com/accessibility](https://developer.apple.com/accessibility/) |
| **Language** | Swift | [docs.swift.org/swift-book/documentation/the-swift-programming-language](https://docs.swift.org/swift-book/documentation/the-swift-programming-language/) |
| **Language** | Observation (`@Observable`) | [developer.apple.com/documentation/observation](https://developer.apple.com/documentation/observation) |

**Moventiq iOS UI:** `:iosApp` — SwiftUI, `@Observable` ViewModels, `SharedLogic.framework`, Koin via `KoinInitIosKt`. See [ARCHITECTURE.md](ARCHITECTURE.md) §8.

---

## Local code review (Cursor skill)

Built on **CodeRabbit + Gemini concepts**, implemented locally in one skill — no bot config files in repo.

| Resource | URL / path | Use when |
|---|---|---|
| **Moventiq code review skill** | `.cursor/skills/moventiq-code-review/SKILL.md` | `@moventiq-code-review review my diff` — chill walkthrough + path rules |
| **Pipeline review step** | `.cursor/skills/moventiq-pipeline/SKILL.md` | `run moventiq-pipeline review` or verify step 5 |
| **Cursor Agent Skills** | [cursor.com/docs/context/skills](https://cursor.com/docs/context/skills) | How `@` skills load and run |
| **CodeRabbit concepts** | [docs.coderabbit.ai/getting-started/yaml-configuration](https://docs.coderabbit.ai/getting-started/yaml-configuration) | chill profile, path instructions (reference only) |
| **Gemini concepts** | [developers.google.com/gemini-code-assist/docs/code-review-style-guide](https://developers.google.com/gemini-code-assist/docs/code-review-style-guide) | Style-guide-as-rules (reference only) |
| **Google eng practices** | [google.github.io/eng-practices/review](https://google.github.io/eng-practices/review/) | Human review quality bar |

---

## In-repo docs

| Doc | Purpose |
|---|---|
| [ARCHITECTURE.md](ARCHITECTURE.md) | Modules, features, dependencies, geofencing |
| [AGENT.md](AGENT.md) | Agent conventions, commands |
| [MVP.md](MVP.md) | Product scope |
| [DESIGN.md](DESIGN.md) | Design tokens |
| `.cursor/skills/moventiq-ui-architecture/` | UI SOLID, file splits, previews |
| `.cursor/skills/moventiq-code-review/` | Local PR/diff review (Cursor skill) |
