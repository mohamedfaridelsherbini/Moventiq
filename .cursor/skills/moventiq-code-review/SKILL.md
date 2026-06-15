---
name: moventiq-code-review
description: >-
  Local Moventiq PR/diff review — diff-scoped, correctness pass + Android/iOS
  parity, assertive profile (3–8 inline), walkthrough, path rules, pre-merge
  gates. Invoke via @moventiq-code-review or moventiq-pipeline review.
---

# Moventiq code review

**Sources:** [AGENT.md](../../../AGENT.md), [MVP.md](../../../MVP.md), [DESIGN.md](../../../DESIGN.md), [ARCHITECTURE.md](../../../ARCHITECTURE.md).

**Run:** `@moventiq-code-review review my diff` · `moventiq-pipeline review`

**Default:** assertive — 3–8 inline comments; minor/nit in walkthrough only. Diff + direct call sites only. Prefer PRs ≤400 lines.

**Relation to built-in `/code-review`:** the built-in skill is the generic bug/cleanup finder (ad-hoc, deeper for one-offs). This skill is the *Moventiq-aware* gate run by `moventiq-pipeline` before merge — it adds project rules, MVP scope, parity, and the pre-merge checklist. CI (`.github/workflows/build.yml`) enforces detekt/ktlint/SwiftLint/Lint + tests; **do not** re-flag linter-enforced style here.

## Modes

| Mode                    | Inline budget   |
|-------------------------|-----------------|
| **chill**               | 1–5             |
| **assertive** (default) | 3–8             |
| **incremental**         | New issues only |

Correctness findings (§ CORRECTNESS) are **exempt from the budget** — always report every confirmed logic bug, in any mode.

## Workflow

1. `git diff develop...HEAD` (or PR files)
2. Skip § Skip paths
3. **Correctness pass** — read each touched hunk *and* its enclosing function; apply § CORRECTNESS. For any logic shared Android↔iOS (resolvers, status checkers, ViewModels), diff the two implementations against each other (§ Parity).
4. Match paths → § Path instructions; apply § Global rules; stay within comment budget
5. Output § Walkthrough template + verdict

## Skip paths

`**/build/**`, `**/.gradle/**`, `**/DerivedData/**`, `**/*.pen`, `**/reports/tests/**`, generated assets

## Global rules — CORRECTNESS (block merge, budget-exempt)

Logic bugs, not conventions. Each finding must name a concrete trigger → wrong result.

- **Stale/cached state drives UI** — a `computeStep`/state read in `init` or a synchronous path that consumes an async-refreshed cache (e.g. iOS `cachedNotificationGranted` default-false before `refreshNotificationStatus`), causing a wrong screen to flash on launch. Seed sync, or gate the phase until first refresh.
- **Permission-result mislabel** — reporting a result field that doesn't match the OS check that gates the flow (e.g. coarse/Approximate grant emitted as `fineGranted = true` while the checker requires FINE), so the resolver and the live checker disagree and bounce the user.
- **Inverted / off-by-one condition; wrong-variable copy-paste; swallowed error in `catch`.**
- **Missing `await` / race** — UI reads VM state before an `async` init/refresh has run.
- **Persisted flag never cleared** when its lifecycle says it should reset (verify against the `clear*`/reset path).
- **Dead control** — an action button whose handler is a no-op on the second invocation (e.g. iOS notif re-`requestAuthorization` after `.denied` never re-prompts) with no alternate escape.

### Parity (Android ↔ iOS)

For any feature with mirrored logic on both platforms:

- Shared resolver **inputs and branch order** must match (`PermissionFlowStepResolver`, etc.).
- Status-checker **semantics** must match the platform OS contract *and* each other's intent (e.g. "adequate location" = background-capable on both).
- A behavior fixed on one platform but not the other is a finding. Name the diverging file:line on both sides.

## Global rules — CRITICAL (block merge)

- UI imports Room DAO / Entity / Database
- Platform APIs in shared domain/data
- Feature → another feature's data layer
- MVP scope creep: profile, auth, cloud sync, integrations
- Hardcoded hex / dp / font sizes in UI
- New `*Content` or screen missing light **and** dark preview
- Business logic in `@Composable` / View body
- New use case or ViewModel with **zero** unit tests
- New code in deprecated `:sharedUI`

## Global rules — MAJOR

- ViewModel > ~200 lines mixing concerns
- Preview uses real ViewModel / Koin / Room
- Missing a11y on icon-only controls; touch < 44dp
- VM constructed inside `@Composable` (incl. androidTest)
- Duplicate TabBar FAB; hardcoded user-facing strings

## Do NOT comment on

Unchanged code, lock files, linter-enforced style, missing KDoc, duplicate findings.

## Path instructions

### `sharedLogic/**` / `shared/**`

Feature-first layering. Domain never imports Room/platform. New use cases: `commonTest`. Repos: host tests.

### `androidApp/**/ui/**`

Screen/Content/ViewModel/UiState/Event. Stateless `*Content`. Previews light+dark. `testTag` = iOS accessibility IDs. `stringResource` + `moventiqSpacing()` tokens. Pager: `settledPage` + VM dedupe.

### `iosApp/**`

View + ContentView + ViewModel. `#Preview` light+dark. `@Observable` `@MainActor`. `Localizable.strings`.

### Tests

snake_case `{subject}_{outcome}_{condition}`. Hoist ViewModel before `setContent`. UI: `-Pandroid.testInstrumentationRunnerArguments.package=…`

## Moventiq references

| Topic                                       | Fix                                                            |
|---------------------------------------------|----------------------------------------------------------------|
| Stale pager `LaunchedEffect`                | Dedupe in VM; use `settledPage`                                |
| VM initial state                            | Store fields in `StateFlow` constructor, not `init { update }` |
| Enum page count                             | `entries.size` / `allCases.count`                              |
| iOS dark text                               | Use dark `textPrimary` token, not light repurposed             |
| iOS cached perm status flashes wrong screen | Seed cache sync or gate phase until first `refresh*`           |
| Coarse grant treated as fine                | `fineGranted` from FINE result only, not coarse                |
| New `CLLocationManager()` per status read   | Retain one instance; reading status is cheap, alloc isn't      |

## Inline format

Rank findings most-severe first (correctness → critical → major). Link the location as a clickable Markdown link whose href is the repo-relative path plus `:line` — pattern `[File.kt:NN](relative/path/File.kt:NN)`.

```text
**correctness** `File.kt:NN` — one-line issue; concrete trigger → wrong result.
Trigger: inputs/state that fire it.
Fix: the change.
```

## Walkthrough template

```markdown
## Walkthrough
**Mode:** assertive · **Scope:** N files · intent

## Summary
- …

## Pre-merge checks
| Check | Status |
| Correctness (no logic bugs) | ✅/❌ |
| Android↔iOS parity | ✅/❌ |
| Architecture | ✅/❌ |
| MVP scope | ✅/❌ |
| Previews light+dark | ✅/❌ |
| Unit tests for new logic | ✅/❌ |

## Findings
(correctness first, then critical/major — each with Trigger + Fix)

## Verdict
✅ Approve / 🔄 Request changes
```

## Related

`moventiq-ui-architecture` · `moventiq-unit-tests` · `moventiq-ui-tests` · `moventiq-pipeline`
