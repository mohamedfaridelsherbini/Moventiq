---
name: moventiq-feature-workflow
description: >-
  Moventiq feature development order — UML design, user approval, test plan,
  failing unit/UI tests, then implementation. Use for every new feature or
  behavior change before writing production code.
---

# Moventiq feature workflow

**Mandatory order for every feature.** Do not write production code until Phase B is done and Phase A is accepted.

```
Phase A  UML + case table     →  GATE: user accepts
Phase B  Test plan + tests    →  GATE: tests compile, fail for expected reasons
Phase C  Implementation       →  GATE: tests green, pipeline verify
```

Cross-ref: `moventiq-pipeline` (implement), `moventiq-unit-tests`, `moventiq-ui-tests`, `moventiq-ui-architecture`, `moventiq-pencil-design`.

---

## Phase A — Design (UML)

### Deliverable

Create `docs/features/<feature-slug>/` with:

| File | Required | Contents |
|---|---|---|
| `uml.md` | Yes | Mermaid diagrams + short prose |
| `test-plan.md` | Yes | Case table mapped to unit vs UI (draft in A, finalized in B) |

Use kebab-case slug: `permission-flow`, `task-crud`, `geofence-arrival`.

### UML minimum (pick what applies)

| Diagram | When | Example |
|---|---|---|
| **State machine** | Flows, screens, phases | Splash → Onboarding → Permissions → Main |
| **Sequence** | User action + platform API | Allow location → OS dialog → ViewModel event |
| **Class / component** | New types, stores, resolvers | `PermissionFlowStepResolver`, `PermissionStatusStore` |

Use **Mermaid** in `uml.md` (renders in GitHub/Cursor). No proprietary UML tools required.

### Case table (in `uml.md` or `test-plan.md`)

Every row = one behavior to implement and test:

| # | User action / trigger | Expected outcome | Layer (unit / UI) |
|---|---|---|---|
| 1 | Maybe later on Location | Notification same session | unit |
| 2 | Maybe later → background → reopen | Location again | unit |

Include **happy path**, **defer/skip**, **error/deny**, **persistence**, **foreground/resume** where relevant.

### GATE — user acceptance

Stop after Phase A. Present:

1. Link to `docs/features/<slug>/uml.md`
2. Case table summary
3. Ask: **Accept design?** (yes / revise)

Do **not** create tests or production code until the user accepts (or explicitly says “proceed without approval”).

---

## Phase B — Test plan + failing tests

After acceptance, finalize `test-plan.md` and add **tests only** — no production implementation yet (stubs/interfaces OK).

### `test-plan.md` format

```markdown
# <Feature> — test plan

## Unit tests
| Test name | Class | Case # |
|---|---|---|
| locationLater_movesToNotification | PermissionFlowViewModelTest | 3 |

## UI tests
| Test name | Class | Case # |
|---|---|---|
| app_showsNotification_afterLocationLater | PermissionFlowTest | 3 |

## Out of scope
- Foreground re-prompt (unit only; lifecycle hard in UI)
```

Naming: follow `moventiq-unit-tests` and `moventiq-ui-tests` (snake_case, 2–3 segments).

### Write tests first

| Layer | Module | Action |
|---|---|---|
| Resolver / use case / ViewModel | `:androidApp` `test`, `:sharedLogic` `commonTest`, iOS `*Tests` | Assert expected state; use fakes |
| Screen content / navigation | `androidTest`, `iosAppUITests` | `testTag` / accessibility IDs |

Tests may **not compile** until minimal stubs exist (empty enum, sealed interface, test tags). That is allowed — prefer smallest stub over full implementation.

### GATE — red tests

- [ ] Every case row has ≥1 mapped test
- [ ] `./gradlew :androidApp:testDebugUnitTest --tests "…<feature>.*"` runs (failures OK)
- [ ] UI tests compile (`compileDebugAndroidTestKotlin`; iOS builds test target)
- [ ] Failures match missing behavior, not typos

Report failing test names as the implementation checklist for Phase C.

---

## Phase C — Implementation

Run `moventiq-pipeline` **implement** steps for code (room, geofencing, ui-architecture/platforms.md, etc.). **Goal: make Phase B tests pass** without deleting cases.

Order within Phase C:

1. Shared logic / resolvers / use cases (bottom-up)
2. ViewModels + stores
3. UI (`*Content`, navigation, tokens, previews)
4. Wire DI / factories / launch args for UI tests

After code: run **verify** pipeline. Update `uml.md` / `test-plan.md` if behavior changed during implementation.

---

## Feature folder examples

```
docs/features/permission-flow/
├── uml.md           # state + sequence diagrams
└── test-plan.md     # case ↔ test mapping
```

Existing copy lives in platform string resources; new features add `docs/features/<slug>/` for UML + test plan.

---

## Agent checklist (copy per feature)

```markdown
## Feature: <name>

### Phase A
- [ ] docs/features/<slug>/uml.md
- [ ] docs/features/<slug>/test-plan.md (draft cases)
- [ ] User accepted design

### Phase B
- [ ] test-plan.md finalized
- [ ] Unit tests added (red)
- [ ] UI tests added (red / compile-only OK)

### Phase C
- [ ] Production code
- [ ] All tests green
- [ ] verify pipeline pass
```

---

## Rules

- **No production code in Phase A or B** except stubs needed for test compilation.
- **Both platforms:** KMP UI features need case rows and tests for Android + iOS when both ship.
- **UI features:** read `moventiq-pencil-design` in Phase A for layout; UML covers **behavior**, Pencil covers **visual spec**.
- **Small fixes:** typos / one-liners skip UML; use judgment — new screens, state machines, or flows always use this workflow.
