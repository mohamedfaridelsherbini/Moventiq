# Moventiq — External resources

Canonical links for implementation. Prefer these over ad-hoc web search when working in this repo.

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

## Kotlin Multiplatform

| Resource | URL |
|---|---|
| KMP GitHub Actions | [kotlinlang.org/docs/multiplatform/github-actions-for-kmp.html](https://kotlinlang.org/docs/multiplatform/github-actions-for-kmp.html) |

## Android UI testing

| Resource | URL |
|---|---|
| UI tests overview | [developer.android.com/training/testing/ui-tests](https://developer.android.com/training/testing/ui-tests) |
| Compose testing | [developer.android.com/develop/ui/compose/testing](https://developer.android.com/develop/ui/compose/testing) |

## In-repo docs

| Doc | Purpose |
|---|---|
| [ARCHITECTURE.md](ARCHITECTURE.md) | Modules, features, dependencies, geofencing |
| [AGENT.md](AGENT.md) | Agent conventions, commands |
| [MVP.md](MVP.md) | Product scope |
| [DESIGN.md](DESIGN.md) | Design tokens |
