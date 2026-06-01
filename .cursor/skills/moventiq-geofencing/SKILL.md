---
name: moventiq-geofencing
description: >-
  Implements Moventiq geofencing and arrival flow — GeofenceManager expect/actual,
  permissions, notifications, Arrival screen. Use before M3 (Android) and M5 (iOS).
---

# Moventiq geofencing (M3 / M5)

Sequence diagram and data flow: [ARCHITECTURE.md](../../../ARCHITECTURE.md) §8–9. Contract: [AGENT.md](../../../AGENT.md) §6.

## Core contract (`sharedLogic/commonMain`)

```kotlin
interface GeofenceManager {
    suspend fun sync(locations: List<Location>)   // register active, remove stale
    val events: Flow<GeofenceEvent>               // ENTER/EXIT with locationId
}

data class GeofenceEvent(val locationId: String, val type: GeofenceEventType)
enum class GeofenceEventType { ENTER, EXIT }
```

Geofences are **not** stored in Room. `SyncGeofences` use case reads active locations from repository and calls `GeofenceManager.sync()`.

## When to sync

- App start (after DB ready)
- Location created, updated, deleted, or active flag toggled
- Device boot (`BOOT_COMPLETED` on Android)
- App returns to foreground (optional refresh)

## ENTER flow

1. OS fires geofence ENTER
2. `GeofenceManager` emits `GeofenceEvent(ENTER, locationId)`
3. Update `lastTriggeredAt` on location
4. Query incomplete tasks linked to that location
5. Show **Arrival** screen (in-app takeover)
6. Post notification via `NotificationScheduler` (respect quiet hours from settings)

## Android (`androidMain` + `:androidApp`)

| Piece | Implementation |
|---|---|
| API | `com.google.android.gms.location.GeofencingClient` |
| Delivery | `PendingIntent` → `GeofenceBroadcastReceiver` |
| Boot | `BootReceiver` re-registers geofences |
| Permissions | `ACCESS_FINE_LOCATION` + `ACCESS_BACKGROUND_LOCATION` (API 29+) |
| Notifications | `ArrivalNotificationManager` + channels |

Files: `service/GeofenceBroadcastReceiver.kt`, `service/BootReceiver.kt`, `platform/GeofenceManager.android.kt`

## iOS (`iosMain` + `:iosApp`) — M5

| Piece | Implementation |
|---|---|
| API | `CLLocationManager` + `CLCircularRegion` |
| Auth | "Always" location authorization |
| Notifications | `UNUserNotificationCenter` |
| Background | Location background mode in capabilities |

Files: `Platform/GeofenceService.swift`, `platform/GeofenceManager.ios.kt`

## Permission screens (from `Moventiq.pen`)

Implement in order during onboarding:

1. **Location Permission** — trust-first rationale, foreground then background
2. **Notification Permission** — push rationale
3. **Permission Denied** — deep link to system Settings, non-blocking recovery

Denial must not crash — degrade gracefully (manual task access still works).

## NotificationScheduler (expect/actual)

```kotlin
expect class NotificationScheduler {
    suspend fun showArrivalNotification(locationName: String, taskCount: Int)
    suspend fun cancelAll()
}
```

Respect `SettingsEntity`: `arrivalAlerts`, `quietHoursEnabled`, `quietStartMinutes`, `quietEndMinutes`.

## Testing

- Unit test `SyncGeofences` use case with fake `GeofenceManager`
- Unit test ENTER handler: updates `lastTriggeredAt`, filters incomplete tasks
- UI test: mock geofence event → Arrival screen visible (see `moventiq-ui-tests`)

## M3 checklist (Android)

- [ ] `GeofenceManager` expect/actual wired
- [ ] `SyncGeofences` called on location changes + boot
- [ ] Permission flow (all three screens)
- [ ] ENTER → notification + Arrival screen
- [ ] Physical device test (emulator geofencing is unreliable)

## Related skills

- Data layer: `moventiq-room-kmp`
- Arrival UI: `moventiq-compose-ui` / `moventiq-swiftui-ui`
- UI tests: `moventiq-ui-tests`
