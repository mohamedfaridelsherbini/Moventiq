---
name: moventiq-room-kmp
description: >-
  Implements Room 3 KMP data layer in :sharedLogic — entities, DAOs, repositories,
  use cases, and data tests. Use when adding persistence, migrations, or M1 data
  layer work.
---

# Moventiq Room KMP (M1)

Full schema and DAO signatures: [ARCHITECTURE.md](../../../ARCHITECTURE.md) §5.

## Package layout (`sharedLogic/`)

```
com.mohamedfaridelsherbini.moventiq/
├── domain/
│   model/           Location, Task, Priority, AppSettings
│   repository/      LocationRepository, TaskRepository, SettingsRepository (interfaces)
│   usecase/         CreateTask, ObserveLocations, SyncGeofences, ClearAllData, …
│   util/            Result, AppError
├── data/
│   local/entity/    LocationEntity, TaskEntity, SettingsEntity
│   local/dao/       LocationDao, TaskDao, SettingsDao
│   local/           MoventiqDatabase.kt, Converters.kt
│   mapper/          LocationMapper, TaskMapper
│   repository/      *RepositoryImpl
├── di/              sharedLogicModule (Koin)
└── platform/        GeofenceManager (expect), DatabaseBuilder (actual)
```

## Dependency rules

- **Domain** never imports Room, Android, or iOS APIs
- **Data** implements domain repository interfaces
- **UI** never imports DAOs — only use cases

## Gradle setup

Add to `gradle/libs.versions.toml` only (never hardcode in module files):

```toml
room = "2.7.0"
sqlite = "2.5.0"
koin = "4.0.3"
```

Apply on `:sharedLogic`:
- `implementation(libs.koin.core)` in `commonMain`
- `implementation(libs.koin.test)` in `commonTest`
- `implementation(libs.androidx.room.runtime)`
- `implementation(libs.androidx.sqlite.bundled)` (iOS)
- `ksp(libs.androidx.room.compiler)`

## Database

- Name: `moventiq.db`, version 1
- Tables: `locations`, `tasks`, `settings` (single row id = `"app"`)
- Geofences are **not** stored — derived from active locations, synced via `GeofenceManager`

## DAO conventions

- Read APIs return `Flow<>` (observe pattern)
- Writes are `suspend fun`
- FK: tasks → locations (`ON DELETE SET NULL`)
- Default radius: 200m; settings seed on first launch

## Platform builders

```kotlin
// commonMain
expect fun createDatabase(): MoventiqDatabase

// androidMain — Room.databaseBuilder(context, …)
// iosMain — BundledSQLiteDriver + Room.databaseBuilder
```

## Use cases (minimum set)

| Use case | Purpose |
|---|---|
| `ObserveLocations` / `ObserveActiveLocations` | Flow of all / active places |
| `CreateLocation` / `UpdateLocation` / `DeleteLocation` | Location CRUD |
| `ObserveAllTasksGrouped` / `ObserveTasksByLocation` | Task lists |
| `CreateTask` / `CompleteTask` / `DeleteTask` | Task CRUD |
| `UpdateSettings` / `ObserveSettings` | App preferences |
| `ClearAllData` | Privacy reset — wipe all tables |
| `SyncGeofences` | Push active locations to OS |

After location save/delete → call `SyncGeofences`.

## Testing

| Layer | Source set | Tool |
|---|---|---|
| Use case | `commonTest` | Fake repository implementations |
| Repository / DAO | `androidHostTest` | `room-testing` in-memory DB |
| Repository / DAO | `iosTest` | In-memory Room on simulator target |

```bash
./gradlew :sharedLogic:testAndroidHostTest
./gradlew :sharedLogic:iosSimulatorArm64Test
```

## M1 checklist

- [ ] `MoventiqDatabase`, entities, DAOs, mappers
- [ ] Repository interfaces + impls
- [ ] Core use cases (CRUD + observe)
- [ ] `GeofenceManager` expect/actual stubs
- [ ] `sharedLogicModule` Koin module + `appModule` for ViewModels
- [ ] Unit/integration tests for DAOs and use cases

## Related skills

- Unit tests: `moventiq-unit-tests`
- Geofence sync: `moventiq-geofencing`
