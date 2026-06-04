# Moventiq Cursor skills

Orchestrator: **`moventiq-pipeline`** — always start here for implement / verify / review.

Feature order: **`moventiq-feature-workflow`** — UML → approved tests → code.

## Active skills (12)

| Skill | Use when |
|---|---|
| `moventiq-context` | Any task — modules, constraints, definition of done |
| `moventiq-pipeline` | Run checks in order, pre-push, PR-ready |
| `moventiq-feature-workflow` | New feature — UML, test plan, tests-before-code |
| `moventiq-ui-architecture` | UI structure, previews, testTag; **platform impl:** [platforms.md](moventiq-ui-architecture/platforms.md) |
| `moventiq-pencil-design` | Read/implement from `Moventiq.pen` via Pencil MCP |
| `moventiq-room-kmp` | Room, repositories, use cases (M1+) |
| `moventiq-geofencing` | Geofence, arrival, notifications (M3+) |
| `moventiq-unit-tests` | ViewModel, use case, repository unit tests |
| `moventiq-ui-tests` | Compose instrumented + XCUITest |
| `moventiq-code-review` | Pre-merge diff review |
| `moventiq-ci` | CI commands, static analysis, Gradle/Xcode setup |

## Rules (`.cursor/rules/`)

| Rule | Always |
|---|---|
| `moventiq-no-profile.mdc` | No auth/profile UI |
| `moventiq-design-tokens.mdc` | Theme tokens only |
| `moventiq-pencil-only.mdc` | `.pen` via Pencil MCP only |
| `moventiq-feature-workflow.mdc` | UML → tests → code for new features |

## Removed (merged elsewhere)

| Was | Now |
|---|---|
| `moventiq-compose-ui` | `moventiq-ui-architecture/platforms.md` § Android |
| `moventiq-swiftui-ui` | `moventiq-ui-architecture/platforms.md` § iOS |

## Pipelines (short)

**implement:** context → feature-workflow A → pencil (UI) → feature-workflow B → ui-architecture → room / geofencing (if needed) → **code** → unit + UI tests green → review → ci

**verify:** context → ui-architecture → unit → UI → review → ci

**review:** context → code-review

**data:** context → room-kmp → unit-tests → review → ci
