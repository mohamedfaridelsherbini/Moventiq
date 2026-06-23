# Shared KMP store — permission flow

> **Status: shipped** on branch `feat/permission-shared-store`. The permission flow now runs on the shared `PermissionFlowStore` (`commonMain`) on both platforms. The store is a synchronous, scope-free reducer; Android wraps it in a thin `ViewModel`, iOS wraps `PermissionFlowStoreHolder` (iosMain) which bridges `state`/`effects` Flows to Swift closures (a hand-rolled bridge — **swap to SKIE** for `AsyncSequence`/Swift-enum ergonomics once SKIE supports Kotlin 2.3.21; it currently caps at 2.3.10). The async iOS notification query lives Swift-side (`NotificationStatusLoader`) so the Kotlin reader stays fully synchronous and Swift never has to implement a Kotlin `suspend` protocol method.

---

## Original design notes

> Reference for enhancement (b): move the state-reduction logic into a single `commonMain` store so the Android and iOS ViewModels stop re-implementing the same reducer. The permission flow is the pilot because its reducer is currently written **twice** (Kotlin `PermissionFlowViewModel` + Swift `PermissionFlowViewModel`), which is exactly the parity surface `moventiq-code-review` polices.

## What exists today (this prototype)

Real, compiling, tested code in `:sharedLogic`:

```
sharedLogic/src/commonMain/.../feature/permissions/presentation/
├── PermissionFlowContract.kt      # PermissionFlowStep, State, Event, Effect,
│                                  # PermissionStatusStore, PermissionStatusReader (interfaces)
├── PermissionFlowStepResolver.kt  # the pure resolver — was duplicated in Kotlin AND Swift
└── PermissionFlowStore.kt         # the reducer: StateFlow + effects Flow + onEvent()

sharedLogic/src/commonTest/.../PermissionFlowStoreTest.kt   # 13 tests, run on Android host AND iOS
```

Verified: `:sharedLogic:testAndroidHostTest` and `:sharedLogic:iosSimulatorArm64Test` both green — **one** test file covers both platforms.

The store owns the *decisions*. The platform still owns lifecycle observation and the system calls (permission requests, opening Settings) and feeds results back as events.

## Android adapter (thin ViewModel)

The platform ViewModel shrinks to: construct the store with `viewModelScope`, expose its `state`/`effects`, forward events.

```kotlin
class PermissionFlowViewModel(
    statusStore: PermissionStatusStore,
    statusReader: PermissionStatusReader,
) : ViewModel() {
    private val store = PermissionFlowStore(statusStore, statusReader, viewModelScope)

    val state: StateFlow<PermissionFlowState> = store.state
    val effects: Flow<PermissionEffect> = store.effects

    fun onEvent(event: PermissionEvent) = store.onEvent(event)
    fun onPermissionFlowEntered() = store.onFlowEntered()
}
```

The UI is unchanged — it already collects `state` with `collectAsStateWithLifecycle()` and `effects` in a `LaunchedEffect` (added in the effects-channel work). `AndroidPermissionStatusChecker` implements `PermissionStatusReader` (add `override val requiresBackgroundLocation = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q`); `PermissionPreferences` implements `PermissionStatusStore`.

## iOS adapter (@Observable bridge)

Swift can't observe a Kotlin `StateFlow` directly, so the adapter mirrors it into an `@Observable` property and consumes effects via the existing `.task` stream.

```swift
@MainActor @Observable
final class PermissionFlowViewModel {
    private(set) var state = PermissionFlowState(step: .none)
    let effects: AsyncStream<PermissionEffect>

    private let store: PermissionFlowStore
    private var tasks: [Task<Void, Never>] = []

    init(statusStore: PermissionStatusStore, statusReader: PermissionStatusReader) {
        let scope = /* a MainScope() exported from KMP */
        store = PermissionFlowStore(statusStore: statusStore, statusReader: statusReader, scope: scope)

        let (stream, continuation) = AsyncStream<PermissionEffect>.makeStream()
        effects = stream

        // Flow → @Observable mirror (via SKIESwiftStateFlow or a Flow→AsyncSequence bridge)
        tasks.append(Task { [weak self] in
            for await s in store.state { self?.state = s }
        })
        tasks.append(Task { [weak self] in
            for await e in store.effects { continuation.yield(e) }
        })
    }

    func handle(_ event: PermissionEvent) { store.onEvent(event: event) }
    deinit { tasks.forEach { $0.cancel() } }
}
```

The `Flow` → `AsyncSequence` bridge is the one piece of plumbing iOS needs. Options, cheapest first:
1. **SKIE** (Gradle plugin) — exposes `StateFlow`/`Flow` as native Swift `AsyncSequence`; near-zero handwritten bridge. Recommended.
2. A small hand-rolled `Flow.collect { }` wrapper in `Bridge/SharedLogic+Async.swift` (the file ARCHITECTURE §8 already anticipates).

## Why this is worth it

- **One reducer, one test suite.** The 13 cases in `PermissionFlowStoreTest` replace the parallel Android `PermissionFlowViewModelTest` + iOS `PermissionFlowViewModelTests`. A behavior change is made and tested once.
- **Parity becomes structural.** The "branch order must match on both platforms" rule in `moventiq-code-review` can't be violated — there's a single branch order.
- **Platform leak removed.** `requiresBackgroundLocation` now comes from `PermissionStatusReader`, so the reducer has no `Build.VERSION` / hardcoded-`true` fork.

## Migration path (when adopting for real)

1. Add the SKIE plugin (or the Flow bridge) and export `:sharedLogic` to the framework.
2. Implement `PermissionStatusReader` / `PermissionStatusStore` on each platform against the shared interfaces (the existing `*StatusChecker` / preferences classes already have the methods).
3. Replace each platform ViewModel body with the thin adapter above; delete the duplicated reducer + the platform copies of `PermissionFlowStepResolver`.
4. Delete the platform `PermissionFlowViewModelTest` cases that the shared `PermissionFlowStoreTest` now covers; keep only platform-specific UI/launcher tests.
5. Apply the same shape to the next feature (`tasks` / `home`) as M1–M2 land.

Do it per-feature, not big-bang — the permission flow is the proof, not the whole migration.
