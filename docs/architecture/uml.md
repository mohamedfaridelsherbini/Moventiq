# Moventiq — System UML

> Project-wide UML. Visual specs: `Moventiq.pen` / [DESIGN.md](../../DESIGN.md). Scope: [MVP.md](../../MVP.md). Module rules & folder trees: [ARCHITECTURE.md](../../ARCHITECTURE.md). Feature-level UML lives under `docs/features/<slug>/uml.md`.

All diagrams are **Mermaid** (render in GitHub / Cursor / Android Studio Markdown preview).

## Implementation status (as of this doc)

| Area | State |
|---|---|
| Splash · Onboarding · Permission flow | ✅ implemented (Android + iOS) |
| App phase shell (`MoventiqApp`) | ✅ implemented |
| Home | 🟡 placeholder screen only |
| Tasks · Locations · Arrival · Settings | ⬜ planned (M2–M4) |
| Data layer (Room, repositories, use cases) | ⬜ planned (M1) — `:sharedLogic` holds only DI + stubs today |
| Geofencing / notifications core | ⬜ planned (M3 / M5) |

Diagrams below are labelled **(current)** when they reflect shipped code and **(target)** when they describe the planned MVP architecture.

---

## 1. System context (target)

```mermaid
flowchart TB
    user([User])

    subgraph apps["Client apps (native UI)"]
        AA["androidApp\nCompose + M3"]
        IA["iosApp\nSwiftUI"]
    end

    subgraph shared["Shared (Kotlin Multiplatform)"]
        FEAT["shared/feature/*\nhome · tasks · locations · arrival · settings"]
        CORE["shared/core/*\ndatabase · geofencing · notifications\nlocation · preferences · common"]
    end

    subgraph os["Platform OS services"]
        GEO["Geofencing\nPlay Services / CoreLocation"]
        NOTIF["Notifications\nNotificationManager / UNUserNotificationCenter"]
        DB[("Local DB\nRoom — moventiq.db")]
    end

    user --> AA & IA
    AA --> FEAT
    IA --> FEAT
    FEAT --> CORE
    CORE --> GEO & NOTIF & DB
```

No backend, no auth, no cloud — fully local-only for MVP.

---

## 2. Module dependency (target)

```mermaid
flowchart TB
    subgraph apps["Applications"]
        AA[androidApp]
        IA[iosApp]
    end
    subgraph features["shared/feature"]
        FH[home]
        FT[tasks]
        FL[locations]
        FA[arrival]
        FS[settings]
    end
    subgraph core["shared/core"]
        CC[common]
        CD[database]
        CP[preferences]
        CL[location]
        CG[geofencing]
        CNo[notifications]
        CDe[designsystem]
    end

    AA --> FH & FT & FL & FA & FS
    IA --> FH & FT & FL & FA & FS
    FH --> FT & FL & FA & CC
    FT --> CD & CC & CDe
    FL --> CD & CC & CL & CG
    FA --> FT & FL & CG & CNo & CC
    FS --> CP & CD & CC
    CG --> CL & CC
    CD --> CC
    CP --> CC
    CL --> CC
    CNo --> CC
    CDe --> CC
```

Rules (CI-enforced): acyclic graph · `domain` imports no platform/DB · features never import another feature's `data` · `core/*` never depends on `feature/*`. Full list: [ARCHITECTURE.md §3](../../ARCHITECTURE.md).

---

## 3. App phase shell — state machine (current)

Reflects `androidApp/.../MoventiqApp.kt` (`AppPhase`) and the iOS `RootView`.

```mermaid
stateDiagram-v2
    [*] --> Splash
    Splash --> Onboarding: splash complete && shouldShowOnboarding
    Splash --> Permissions: splash complete && onboarding seen
    Onboarding --> Permissions: onboarding finished
    Permissions --> Main: permission flow complete
    Main --> [*]

    note right of Permissions
        Sub-flow lives in
        docs/features/permission-flow/uml.md
        (Location → Notification / Denied)
    end note
```

Phase is **derived state**, not navigation: `MoventiqApp` computes `phase` from three ViewModels' `StateFlow`s and renders via `AnimatedContent`. `MoventiqNavHost` (Compose Navigation) only takes over inside `Main`.

---

## 4. Navigation map — Main (target)

```mermaid
flowchart LR
    subgraph main["Main (Scaffold + MoventiqBottomBar)"]
        HOME[Home]
        TASKS[Tasks]
        PLACES[Places]
        SETTINGS[Settings]
    end
    FAB(["( + ) FAB\nquick capture"])

    HOME --> TASKS --> PLACES --> SETTINGS
    FAB -.create task/location.-> HOME
    PLACES --> CREATELOC[Create / Edit Location]
    PLACES --> LOCTASKS[Location Tasks]
    TASKS --> CREATETASK[Create / Edit Task]
    TASKS --> TASKDETAIL[Task Detail]
    ARRIVAL[[Arrival takeover]]:::overlay
    classDef overlay fill:#5b8def22,stroke:#5b8def,stroke-dasharray:4 3
```

`Arrival` is a full-screen takeover pushed on geofence ENTER, outside the tab graph. Today `MoventiqNavHost` only registers `Routes.Home` → `PlaceholderHomeScreen`.

---

## 5. Domain data model (target)

```mermaid
classDiagram
    class Location {
        +String id
        +String name
        +String? address
        +Double latitude
        +Double longitude
        +Int radiusMeters
        +String icon
        +Boolean isActive
        +Instant createdAt
        +Instant? lastTriggeredAt
    }
    class Task {
        +String id
        +String title
        +String? notes
        +String? locationId
        +Priority priority
        +Instant? dueAt
        +ReminderType reminderType
        +Boolean isCompleted
        +Int sortOrder
        +Instant createdAt
        +Instant? completedAt
    }
    class Settings {
        +String id = "app"
        +ThemeMode theme
        +Boolean arrivalAlerts
        +Boolean quietHoursEnabled
        +Int quietStartMinutes
        +Int quietEndMinutes
        +Int defaultRadius
        +Priority defaultPriority
        +ReminderType defaultReminder
    }
    class GeofenceRegion {
        +String locationId
        +Double lat
        +Double lng
        +Int radiusMeters
    }

    class Priority {
        <<enumeration>>
        NONE
        LOW
        MEDIUM
        HIGH
    }
    class ReminderType {
        <<enumeration>>
        ON_ARRIVAL
        TIME
        NONE
    }

    Location "1" o-- "0..*" Task : locationId (SET NULL on delete)
    Task --> Priority
    Task --> ReminderType
    Settings --> Priority : defaultPriority
    Settings --> ReminderType : defaultReminder
    Location ..> GeofenceRegion : derived when isActive
```

`GeofenceRegion` is **not persisted** — derived from active `Location`s and synced to the OS via `GeofenceRegistry`.

---

## 6. Clean architecture — one feature slice (target)

```mermaid
classDiagram
    direction LR
    class FooScreen {
        wires ViewModel
        collects UiState
    }
    class FooViewModel {
        +StateFlow~UiState~ state
        +onEvent(FooEvent)
    }
    class CreateTask {
        <<use case>>
        +invoke(params)
    }
    class TaskRepository {
        <<interface>>
        +observeAll() Flow
        +upsert(task)
    }
    class TaskRepositoryImpl
    class TaskLocalDataSource
    class TaskDao {
        <<Room>>
    }

    FooScreen --> FooViewModel
    FooViewModel --> CreateTask : use cases only
    CreateTask --> TaskRepository
    TaskRepositoryImpl ..|> TaskRepository
    TaskRepositoryImpl --> TaskLocalDataSource
    TaskLocalDataSource --> TaskDao
```

Reads: `DAO/Flow → repository → domain → use case → platform ViewModel → UI`. UI never imports DAOs.

---

## 7. Core loop — arrival sequence (target)

```mermaid
sequenceDiagram
    actor User
    participant OS as OS Geofence
    participant Reg as GeofenceRegistry
    participant Src as GeofenceEventSource
    participant Arr as feature/arrival
    participant Tasks as TaskRepository
    participant Notif as NotificationScheduler
    participant UI as Arrival screen

    Note over Reg: SyncGeofences registers active locations
    User->>OS: enters geofence radius
    OS-->>Src: ENTER(locationId)
    Src->>Arr: GeofenceEvent(ENTER, locationId)
    Arr->>Tasks: incomplete tasks for locationId
    Tasks-->>Arr: tasks
    Arr->>Arr: update lastTriggeredAt
    Arr->>Notif: showArrivalNotification (respect quiet hours)
    Arr-->>UI: ArrivalUiState
    User->>UI: complete task
```

---

## 8. Platform ↔ shared bootstrap (current)

```mermaid
flowchart LR
    subgraph android["Android"]
        APP[MoventiqApplication] --> KI["initKoin { androidContext; appModule }"]
        MA[MainActivity] --> MAPP[MoventiqApp composable]
    end
    subgraph ios["iOS"]
        IAPP[MoventiqApp.swift] --> DEP[AppDependencies.bootstrap]
        DEP --> KII["KoinInitIosKt.doInitKoinIos()"]
        IAPP --> ROOT[RootView]
    end
    subgraph sl[":sharedLogic"]
        KMOD[KoinModules] --> PMOD["platformModule (expect/actual)"]
    end
    KI --> KMOD
    KII --> KMOD
```

DI is Koin: shared graph in `:sharedLogic/di`; `androidApp` registers ViewModels; iOS holds a thin graph in `SharedLogic.framework`.

---

## Maintenance

- Keep diagrams labelled **(current)** in sync with shipped code; promote **(target)** sections to **(current)** as milestones land.
- New behaviour flows get their own `docs/features/<slug>/uml.md` (per `moventiq-feature-workflow`); link them here under the relevant section.
