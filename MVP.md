# Moventiq — MVP Specification

> **The right task. At the right place.**
> Moventiq is a location-aware productivity app. Users attach tasks to places; when they arrive, the relevant tasks surface automatically via geofencing + notifications.

This document defines the **Minimum Viable Product** derived from the product brief and the finished design system (`pencil-new.pen`, tokens in `DESIGN.md`). It is the source of truth for *what* to build first. For *how* to build it, see `AGENT.md`.

---

## 1. MVP goal

Ship the smallest product that proves the core loop end-to-end on **Android first**, then iOS:

```
Create Location → Create Task → Link Task to Location → Arrive → Task appears + notification → Complete
```

If a user can save "Supermarket", add "Buy milk", drive there, and get the task surfaced on arrival — the MVP is successful.

---

## 2. Scope

### In scope (MVP)
- Location-based task triggering via **geofencing**
- Location management (create, edit, delete) with **radius control**
- Tasks: create, edit, complete, delete, reorder; **linked to a location**
- Task grouping **by place**
- **Arrival notifications** + in-app arrival experience
- **Offline-first** local persistence — all data lives in a **local DB only** (no account, no cloud, no sign-in)
- **Light + dark mode**
- Onboarding + **location permission** flow (foreground + background)
- Settings: app preferences only (location behavior, notifications, appearance, privacy, support)

### Out of scope (post-MVP)
- **Profile page**, user avatar, sign-in, accounts, auth, cloud sync, sharing
- Third-party **integrations** (Calendar, Maps, etc.)
- Time-based reminders as a primary feature (secondary only)
- Global cross-entity search, widgets, wearables
- Recurring tasks, sub-tasks, attachments
- Analytics dashboards / insights beyond the simple arrival insight chip

---

## 3. Platforms

| Target | Entry point | UI |
|---|---|---|
| Android (minSdk 24, target 36) | `:androidApp` | **Jetpack Compose** + Material 3 |
| iOS | `:iosApp` | **SwiftUI** |
| Shared logic + DB | `:sharedLogic` | Kotlin Multiplatform + **Room 3** |

> Full module/layer/package design: [ARCHITECTURE.md](ARCHITECTURE.md). `:sharedUI` (Compose Multiplatform) is deprecated — native UIs per platform.

---

## 4. Screen inventory (designed → MVP status)

All screens exist in `pencil-new.pen` in **light + dark**. Status indicates MVP inclusion.

| # | Screen (in .pen) | Purpose | MVP |
|---|---|---|---|
| 1 | `Screen/Splash` (+ Light) | Brand splash, init | ✅ |
| 2 | `Screen/Onboarding 1–3` (+ Dark) | Explain the core concept (3 steps) | ✅ |
| 3 | `Screen/Location Permission` (+ Dark) | Trust-first foreground/background permission | ✅ |
| 4 | `Screen/First Location Setup` (+ Dark) | One-tap suggested first place | ✅ |
| 5 | `Screen/Home Main` (+ Dark) | Active-location hero, triggered tasks, today, quick actions | ✅ |
| 6 | `Screen/Locations` (+ Dark) | Saved places: status, radius, task count, last-trigger | ✅ |
| 7 | `Screen/Create Location` (+ Dark) | Map picker, radius selector, trigger-zone preview | ✅ |
| 8 | `Screen/Location Tasks` (+ Dark) | Tasks for a place; complete/edit/delete/reorder; progress | ✅ |
| 9 | `Screen/Create Task` (+ Dark) | Title, notes, location, priority, due, reminder, visibility preview | ✅ |
| 10 | `Screen/Arrival` (+ Dark) | Geofence-triggered arrival takeover with triggered tasks | ✅ |
| 11 | `Screen/Notification Preview` (+ Dark) | Lock-screen push preview + alert toggles | ✅ |
| 12 | `Screen/Empty State` (+ Dark) | No-tasks empty state | ✅ |
| 13 | `Screen/Settings` (+ Dark) | App preferences: location, notifications, tasks, appearance, privacy, support | ✅ |
| 14 | `Screen/All Tasks` (+ Dark) | Tasks tab: all tasks grouped by place | ✅ |
| 15 | `Screen/Task Detail` (+ Dark) | View/complete/edit a single task | ✅ |
| 16 | `Screen/Edit Location` / `Edit Task` (+ Dark) | Edit mode + delete | ✅ |
| 17 | `Screen/Notification Permission` (+ Dark) | Push permission rationale | ✅ |
| 18 | `Screen/Permission Denied` (+ Dark) | Enable location in Settings | ✅ |
| 19 | `Screen/Search Locations` / `Search Tasks` (+ Dark) | Scoped list search | ✅ |
| 20 | Settings sub-screens (+ Dark) | Notifications, Geofencing, Appearance, Privacy, About | ✅ |

**Reference boards (not shipped):** `Navigation System`, `Settings States`, `Moventiq Brand System`, color-system canvas.

---

## 5. Navigation

Floating bottom bar (`Component/TabBar`) with a center Add FAB:

```
Home  ·  Tasks  ·  ( + )  ·  Places  ·  Settings
```

- **Home** — "Where am I?" current location + triggered + today
- **Tasks** — "What should I do?" all tasks grouped by place
- **+ (FAB)** — quick task capture (most prominent action)
- **Places** — "Where do tasks appear?" manage locations
- **Settings** — app preferences (not a profile; no user identity in MVP)

Primary journey is optimized to: open → Home shows current location + triggered tasks → complete → `+` to capture a new location-based task.

---

## 6. Data model (MVP)

```
Location
  id: String
  name: String
  address: String?
  latitude: Double
  longitude: Double
  radiusMeters: Int          // default 200, presets 100/200/500/1000
  icon: String               // lucide key: house, briefcase, dumbbell, shopping-cart, map-pin
  isActive: Boolean          // active/paused geofence
  createdAt: Instant
  lastTriggeredAt: Instant?

Task
  id: String
  title: String
  notes: String?
  locationId: String?        // link to Location (nullable = inbox)
  priority: Priority         // NONE, LOW, MEDIUM, HIGH
  dueAt: Instant?
  reminderType: ReminderType // ON_ARRIVAL, TIME, NONE
  isCompleted: Boolean
  sortOrder: Int             // manual reorder
  createdAt: Instant
  completedAt: Instant?

Geofence (derived from active Locations)
  locationId, lat, lng, radiusMeters, transitions = ENTER (+ optional EXIT)
```

Settings persisted: theme (system/light/dark), arrival/reminder/sound toggles, quiet hours, default radius, default priority, default reminder, task sorting, auto-complete-on-leave.

---

## 7. Functional requirements

### Locations
- Create with name, map-selected coordinates, radius (slider + presets), icon.
- Edit / delete (deleting a location unlinks or deletes its tasks — confirm).
- Active/paused toggle stops/starts the geofence.
- List shows: status pill, radius, task count, last-trigger time (health indicators).

### Tasks
- Create/edit with title, notes, location link, priority, due date (optional), reminder (`On arrival` default).
- Complete (checkbox), delete (swipe), edit (swipe), reorder (drag).
- Grouped by place in the Tasks tab; per-location list shows completion progress.
- "When this task appears" preview explains visibility (arrival at linked place).

### Geofencing & arrival
- Register geofences for all **active** locations on app start / location change.
- On ENTER: surface triggered tasks (Arrival screen + notification).
- Respect notification settings (arrival toggle, quiet hours, sound).
- Battery-conscious registration; re-register after reboot (Android `BOOT_COMPLETED`).

### Permissions
- Request foreground then **background** location with a trust-first rationale screen.
- Handle denied / "while using" / granted states gracefully (see Settings states).
- Geofencing requires background location on Android 10+.

### Offline-first / local-only
- All reads/writes hit a **local DB** (Room KMP or SQLDelight); the app is fully usable offline.
- No network dependency, no backend, no user accounts for the core loop in MVP.
- **Reset data** via Settings → Privacy → "Clear all data" (replaces any account-deletion flow).

---

## 8. Non-functional

- **Accessibility:** ≥44pt touch targets, semantic labels on icons/toggles, WCAG-AA contrast (tokens chosen for it), dynamic type friendly.
- **Theming:** every surface is token-driven; light/dark parity verified in design.
- **Performance:** instant local data; geofence registration off the main thread.
- **Privacy:** location used only for geofencing; explained in permission screen; no third-party sharing in MVP.

---

## 9. Milestones

1. **M0 — Foundations:** theme (tokens → Compose `MaterialTheme`), typography, design-system components (TabBar, buttons, cards, task row, status bar), navigation scaffold.
2. **M1 — Data layer:** Location/Task models, local persistence, repositories, settings store (all in `:sharedLogic`).
3. **M2 — CRUD UI:** Locations list/create/edit, Tasks list/create/edit, Home, Settings — wired to data.
4. **M3 — Geofencing (Android):** permission flow, geofence registration, ENTER handling, arrival notification + Arrival screen.
5. **M4 — Onboarding + polish:** splash, onboarding, first-location setup, empty states, dark-mode QA, accessibility pass.
6. **M5 — iOS parity:** geofencing via CoreLocation (expect/actual), notifications, iOS permission flow, host Compose UI in `:iosApp`.

---

## 10. Definition of done (MVP)

- A user can complete the full core loop on a physical Android device, offline.
- Geofence ENTER reliably surfaces the linked tasks + a notification.
- Light/dark both correct on every shipped screen.
- Permission denial paths are non-blocking and recoverable.
- Locations show live health (active/paused, last-trigger, counts).
- All shipped screens match the `pencil-new.pen` design within tokens.
