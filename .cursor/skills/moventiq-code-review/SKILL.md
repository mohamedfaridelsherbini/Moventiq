---
name: moventiq-code-review
description: >-
  Local Moventiq PR/diff review — diff-scoped, chill profile (1–5 inline),
  walkthrough, path rules, pre-merge gates. Invoke via @moventiq-code-review
  or moventiq-pipeline review.
---

# Moventiq code review

**Sources:** [AGENT.md](../../../AGENT.md), [MVP.md](../../../MVP.md), [DESIGN.md](../../../DESIGN.md), [ARCHITECTURE.md](../../../ARCHITECTURE.md).

**Run:** `@moventiq-code-review review my diff` · `moventiq-pipeline review`

**Default:** chill — 1–5 inline comments; minor/nit in walkthrough only. Diff + direct call sites only. Prefer PRs ≤400 lines.

## Modes

| Mode | Inline budget |
|---|---|
| **chill** (default) | 1–5 |
| **assertive** | 3–8 |
| **incremental** | New issues only |

## Workflow

1. `git diff develop...HEAD` (or PR files)
2. Skip § Skip paths
3. Match paths → § Path instructions
4. Apply § Global rules; stay within comment budget
5. Output § Walkthrough template + verdict

## Skip paths

`**/build/**`, `**/.gradle/**`, `**/DerivedData/**`, `**/*.pen`, `**/reports/tests/**`, generated assets

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

| Topic | Fix |
|---|---|
| Stale pager `LaunchedEffect` | Dedupe in VM; use `settledPage` |
| VM initial state | Store fields in `StateFlow` constructor, not `init { update }` |
| Enum page count | `entries.size` / `allCases.count` |
| iOS dark text | Use dark `textPrimary` token, not light repurposed |

## Inline format

```markdown
**major** `path/File.kt:42` — issue.
Fix: …
```

## Walkthrough template

```markdown
## Walkthrough
**Mode:** chill · **Scope:** N files · intent

## Summary
- …

## Pre-merge checks
| Check | Status |
| Architecture | ✅/❌ |
| MVP scope | ✅/❌ |
| Previews light+dark | ✅/❌ |
| Unit tests for new logic | ✅/❌ |

## Inline findings
(critical/major with Fix)

## Verdict
✅ Approve / 🔄 Request changes
```

## Related

`moventiq-ui-architecture` · `moventiq-unit-tests` · `moventiq-ui-tests` · `moventiq-pipeline`
