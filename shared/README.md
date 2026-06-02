# Shared Kotlin Multiplatform

Target module graph — see [ARCHITECTURE.md](../ARCHITECTURE.md).

```
core/       common, database, network, preferences, location, geofencing, notifications, analytics, designsystem
feature/    home, tasks, locations, arrival, settings
platform/   optional Koin aggregators (android, ios)
```

**Interim:** business logic still lives in [`../sharedLogic`](../sharedLogic) until M6 migration.
