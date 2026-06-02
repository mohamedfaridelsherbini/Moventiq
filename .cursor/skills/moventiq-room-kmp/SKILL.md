---
name: moventiq-room-kmp
description: >-
  Implements persistence in Moventiq — Room 3 (MVP) in core/database or interim
  :sharedLogic, feature data layers, repositories, use cases. Use for M1 data work.
---

# Moventiq data layer (M1)

Full architecture: [ARCHITECTURE.md](../../../ARCHITECTURE.md). Schema summary: Appendix A.

## Target layout (feature-first)

```
shared/core/database/           Room (MVP) or SQLDelight (scale), drivers, migrations
shared/feature/tasks/data/      TaskLocalDataSource, TaskRepositoryImpl
shared/feature/locations/data/  LocationLocalDataSource, LocationRepositoryImpl
shared/feature/settings/data/ Settings store
```

Each feature:

```
feature/<name>/
├── domain/model|repository|usecase/
└── data/local|mapper|repository/
```

## Interim (`:sharedLogic`)

Until Gradle modules split, use packages:

```
com.mohamedfaridelsherbini.moventiq.feature.tasks.domain
com.mohamedfaridelsherbini.moventiq.feature.tasks.data
```

Or legacy flat `domain/` + `data/` — migrate to feature packages before extracting modules.

## Dependency rules

- **Domain** never imports Room, SQLDelight, Android, or iOS APIs
- **Data** implements domain repository interfaces; uses `core/database` only
- **UI** (apps) never imports DAOs — only use cases
- **Features** do not import another feature's `data`

## Gradle setup

Add to `gradle/libs.versions.toml` only:

```toml
room = "2.7.0"
sqlite = "2.5.0"
koin = "4.0.3"
```

- `implementation(libs.koin.core)` in shared `commonMain`
- Room + KSP on database module (`:sharedLogic` today → `shared/core/database`)

## Koin

```kotlin
// shared/feature/tasks/di/TasksModule.kt
val tasksModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get(), get()) }
    factory { CreateTask(get()) }
}
```

Aggregated in `initKoin()` — see [RESOURCES.md](../../../RESOURCES.md) ([Koin KMP setup](https://insert-koin.io/docs/reference/koin-core/kmp-setup/)).

## Tests

```bash
./gradlew :sharedLogic:testAndroidHostTest
./gradlew :sharedLogic:iosSimulatorArm64Test
```

- DAO / migration: `androidHostTest` / `iosTest`
- Repository: fakes in `commonTest` + integration in host tests
- Use case: `commonTest` only

## Checklist

- [ ] Entities + DAOs in `core/database` (or interim `sharedLogic`)
- [ ] Per-feature `*RepositoryImpl` + domain interfaces
- [ ] Use cases in `feature/*/domain/usecase`
- [ ] Feature Koin modules + `platformModule` actuals
- [ ] No UI imports of DAOs
