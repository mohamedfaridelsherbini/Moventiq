---
name: moventiq-code-review
description: >-
  Local Moventiq PR/diff review built on CodeRabbit + Gemini concepts — diff-scoped,
  chill profile (1–5 inline), walkthrough summary, path rules, pre-merge gates.
  Invoke via @moventiq-code-review or moventiq-pipeline review. All rules in this skill.
  Moventiq References §: state flow, tokens, VM init, enum counts, i18n.
---

# Moventiq code review

**Source of truth:** this skill + [AGENT.md](../../../AGENT.md), [MVP.md](../../../MVP.md), [DESIGN.md](../../../DESIGN.md), [ARCHITECTURE.md](../../../ARCHITECTURE.md).

Cross-ref: `moventiq-ui-architecture`, `moventiq-context`, `moventiq-unit-tests`, `moventiq-ui-tests`, [RESOURCES.md](../../../RESOURCES.md), `.cursor/rules/`.

**How to run:** `@moventiq-code-review review my current branch diff` · `run moventiq-pipeline review`

## Design philosophy — local skill on CodeRabbit + Gemini concepts

This skill is a **local Cursor agent review** that copies the *behavior* of strong AI PR bots — not their repo config files. One skill file replaces scattered bot YAML; you invoke it when you want a review **before push** or **alongside** GitHub bots.

### What you get locally (same feel as bots)

| Output | CodeRabbit-like | Gemini-like | This skill |
|---|---|---|---|
| PR summary | Walkthrough + high-level summary | PR opened summary | § Walkthrough template — Summary bullets |
| Sparse inline | `chill` profile, few comments | `comment_severity_threshold` | 1–5 inline on chill; minor → walkthrough |
| Severity labels | critical / major / minor | MEDIUM / HIGH / LOW | critical / major / minor / nit |
| Path-specific rules | `path_instructions` | `styleguide.md` sections | § Path instructions |
| Skip noise | `path_filters` | `ignore_patterns` | § Skip paths |
| Merge readiness | Pre-merge checks table | Severity gates | § Pre-merge checks in walkthrough |
| Re-review | Incremental on new commits | Re-run on push | § Incremental re-review |
| Tone | Direct, fix-oriented | Style guide constraints | § Inline format — **Fix:** required |

### Concept mapping (borrowed patterns)

| Idea | From CodeRabbit | From Gemini Code Assist | Local implementation (this file) |
|---|---|---|---|
| **Low-noise default** | `profile: chill` — nits suppressed | `comment_severity_threshold: MEDIUM` or `HIGH` | **chill** mode; minor/nit walkthrough-only |
| **Deep audit when needed** | `profile: assertive` | Lower threshold / more comments | **assertive** mode; 3–8 inline |
| **Diff-only scope** | Changed lines + call sites | Respects `ignore_patterns` | Workflow step 1; § Skip paths |
| **Module rules** | `path_instructions` per glob | `styleguide.md` prose rules | § Path instructions per path |
| **Global policy** | `instructions` block | Style guide intro + sections | § Global rules + § Do NOT comment |
| **One root cause** | Merge duplicate findings | — | One comment per fix |
| **Pre-merge gate** | `custom_checks` in YAML | Dashboard / severity | § Pre-merge checks table |
| **No docstring theater** | (often misconfigured) | — | § Do NOT — no KDoc for coverage |
| **Actionable comments** | Fix in comment | Style guide violations | **Fix:** or **Why not:** required |

### Local call flow (when you `@` this skill)

```text
You: @moventiq-code-review review my diff
        ↓
Agent loads this SKILL.md + AGENT.md + MVP/ARCHITECTURE/DESIGN
        ↓
git diff develop...HEAD  →  match paths  →  apply chill budget
        ↓
Walkthrough (summary + pre-merge table + verdict)
+ 1–5 inline findings (critical/major only on chill)
```

GitHub CodeRabbit / Gemini on a PR run **separately** on their hosts. This skill does **not** call them — it **emulates** their best patterns so local and remote reviews feel consistent.

### External docs (concepts only — not required in repo)

Read these to understand *why* this skill is shaped this way:

| Tool | Doc | Concept borrowed |
|---|---|---|
| CodeRabbit | [YAML configuration](https://docs.coderabbit.ai/getting-started/yaml-configuration) | chill profile, path instructions, walkthrough |
| CodeRabbit | [Pre-merge checks](https://docs.coderabbit.ai/pr-reviews/pre-merge-checks) | Custom gate table in walkthrough |
| Gemini | [Customize repo review](https://developers.google.com/gemini-code-assist/docs/customize-repo-review) | Severity threshold ≈ chill vs assertive |
| Gemini | [Code review style guide](https://developers.google.com/gemini-code-assist/docs/code-review-style-guide) | Project rules in prose → § Global + Path instructions |
| Human baseline | [Google eng practices](https://google.github.io/eng-practices/review/) | Speed, respect, actionable feedback |

**Moventiq rule:** All enforceable rules live **in this skill**. Do not add `.coderabbit.yaml` or `.gemini/` to the repo unless you explicitly want those bots again.

## Review performance model

| Lever | Setting | Effect |
|---|---|---|
| Profile | **chill** (default) | Bugs + architecture breaks inline; style nits → walkthrough |
| Scope | Diff + direct call sites only | Ignore untouched modules |
| Comment budget | **1–5 inline** per typical PR | Batch extras in walkthrough |
| Incremental | Re-review after push | New issues only; skip fixed items |
| Skip paths | build/, `.pen`, assets, reports | Never review generated noise |
| Path rules | § Path instructions below | Module-specific checks |
| Pre-merge | Walkthrough table | Gate in summary, not duplicate inline comments |

**PR size:** Prefer **≤400 changed lines** per PR. If larger, note in walkthrough: "consider splitting."

## Review modes

| Mode | When | Inline budget | minor/nit |
|---|---|---|---|
| **chill** (default) | Normal PRs | 1–5 | Walkthrough only |
| **assertive** | Thorough / pre-release audit | 3–8 | May inline |
| **incremental** | Follow-up after push | New issues only | Same as chill |

Say which mode you used in the walkthrough header.

## Severity (inline comments)

| Label | When | chill: inline? |
|---|---|---|
| **critical** | Architecture break, MVP scope, missing tests/previews for new logic, hardcoded tokens | Always |
| **major** | SOLID/a11y/test anti-pattern in diff | Yes, if clear |
| **minor** | testTag, single-theme preview, naming | Walkthrough only |
| **nit** | Optional refactor | Walkthrough only (or omit) |

One root cause → **one comment**.

## Workflow

1. **Read the diff** — `git diff develop...HEAD` or PR files changed
2. **Skip** paths in § Skip paths
3. Match each changed path to § Path instructions
4. Apply § Global rules — respect **Do NOT comment**
5. Count inline comments — stay within budget
6. Output § Walkthrough template
7. **Verdict:** Approve if zero critical; Request changes if critical/unresolved major

### Incremental re-review

1. Only comment on **new commits** or open critical/major
2. Do not re-list fixed items
3. If all prior critical/major fixed → "No new issues" + Approve

## Skip paths

Do not review (unless explicitly asked):

- `**/build/**`, `**/.gradle/**`, `**/DerivedData/**`
- `**/*.xcodeproj/xcuserdata/**`, `**/gradle/wrapper/**`
- `**/brand-assets/**`, `**/*.pen`, `**/*.xcresult/**`, `**/reports/tests/**`

## Global rules

Review **changed lines and direct call sites only**. Target 1–5 actionable inline comments on chill.

### CRITICAL (inline, block merge)

- UI imports Room DAO, Entity, or Database types
- Platform APIs in shared commonMain domain/data
- Feature module depending on another feature's data layer
- MVP scope creep: profile, auth, sign-in, avatar, PRO badge, cloud sync, integrations, "Delete account"
- Hardcoded hex colors, raw dp spacing, or font sizes in UI
- New `*Content` or shipped screen missing **light AND dark** preview
- Business logic or repository calls inside `@Composable` / SwiftUI View body
- New use case or ViewModel with **zero** unit tests
- New UI in deprecated `:sharedUI`

### MAJOR (inline when clear)

- ViewModel > ~200 lines mixing mapping and domain logic
- Preview uses real ViewModel, Koin, or Room
- Icon-only control missing contentDescription / accessibility label
- Touch target < 44dp
- Geofence API in commonMain; location change without geofence sync
- ViewModel constructed inside `@Composable` (including androidTest `setContent`)
- Duplicate FAB on screen (TabBar FAB only)
- UI ↔ ViewModel feedback loop (pager/TabView sync)
- Hardcoded user-facing strings (iOS: Localizable.strings; Android: strings.xml)

### MINOR / NIT (walkthrough only on chill)

- Test naming, missing testTag, single-theme preview
- `MutableStateFlow` default + `init { update }` when no flicker
- Hardcoded enum `COUNT` instead of `entries.size` / `allCases.count`

### Do NOT comment on

- Unchanged code, lock files, generated output
- AGP/SDK XML v4 warnings, Gradle `--tests` on connectedDebugAndroidTest
- Style/naming enforced by detekt/ktlint/SwiftLint
- Duplicate findings — merge into one comment
- Subjective refactors with no bug or architecture impact
- Missing KDoc/docstrings — tests + self-explanatory UI code suffice

## Path instructions

### `sharedLogic/**`

Feature-first: domain/data/presentation per feature; core for geofencing, database. Domain never imports Room, SQLDelight, Ktor, or platform APIs. New use cases: `commonTest` happy + edge. Repositories: `androidHostTest` / `iosTest` with in-memory DB. Flag geofence side effects when locations change.

### `shared/**`

Enforce ARCHITECTURE.md: core ↛ feature; feature ↛ feature data. Presentation: state/event only — no UI framework imports.

### `androidApp/**/ui/**`

Screen/Content/ViewModel/UiState/Event split. `*Content` stateless. `@Preview` light + dark with fake `UiState`. `testTag` matches iOS accessibility IDs. Branding/timing in `*Branding` objects. Pager: `settledPage` + `distinctUntilChanged` → ViewModel. VM init from store in `StateFlow` constructor. `entries.size` for page count. `stringResource` only. `moventiqSpacing()` tokens — not literal `4.dp` / `48.dp`.

### `iosApp/**`

View + ContentView + ViewModel + UiState. `#Preview` light + dark. `@Observable` `@MainActor` ViewModels. TabView sync with current `state`; dedupe `pageChanged`. `allCases.count`. `Localizable.strings` + `String(localized:)`. `MoventiqTheme` typography tokens.

### `androidApp/src/test/**`

snake_case `{subject}_{outcome}_{condition}`. MainDispatcherRule + turbine for ViewModels.

### `androidApp/src/androidTest/**`

Behavior tests only. Hoist ViewModel before `setContent`. `createComposeRule` v2. Filter via `-Pandroid.testInstrumentationRunnerArguments.package`.

### `iosApp/iosAppTests/**`

snake_case naming. `@MainActor` + poll with timeout for real-time delays.

### `iosApp/iosAppUITests/**`

`launchArguments` for timing. `descendants(matching: .any)`. `accessibilityElement(children: .contain)`.

## Pre-merge checks (walkthrough table)

Fail walkthrough row if:

- New use case or ViewModel **without** unit tests
- New `*Content` or shipped screen **without** light+dark previews
- Any UI file imports `androidx.room` or DAO types
- MVP out-of-scope features introduced

## Architecture quick check

```text
✅ UI → ViewModel → UseCase → Repository → DAO → Room
❌ UI → DAO  ❌ ViewModel → RoomDatabase  ❌ Domain → android.* / platform.*
```

Platform docs: [RESOURCES.md](../../../RESOURCES.md).

## References

Moventiq-specific recurring feedback (see prior reviews). Full detail in git history of this skill.

### State flow

| Pitfall | Severity | Fix |
|---|---|---|
| Stale `state` in `LaunchedEffect(pagerState)` collector | **major** | No `page != state.currentPage` in long-lived collector; dedupe in VM |
| `snapshotFlow { currentPage }` during animation | **major** | Use `settledPage` + `distinctUntilChanged()` |
| Missing VM dedupe | **minor** | `if (event.page == currentPage) return` |

### Design tokens

| Rule | Severity | Fix |
|---|---|---|
| Hardcoded hex / raw dp / font sizes | **critical** | `MoventiqTheme`, `LocalMoventiqColors`, `LocalMoventiqSpacing`, [DESIGN.md](../../../DESIGN.md) |
| `4.dp`, `8.dp`, `24.dp`, `48.dp` when token exists | **minor** | `spacing.xs/sm/lg/xxl` |
| iOS dark surface / text-on-dark | **major** | `moventiqSurfaceDark` = `#111827`; dark `textPrimary` = `text-on-dark` (`#FFFFFF`) — do not repurpose light `moventiqTextPrimary` |
| `fillMaxWidth()` then fixed `.width(dp)` | **minor** | Use `.widthIn(max = …)` so the cap applies |

### Instrumented UI tests (pager)

After `performClick()` on a pager CTA, **wait** for the next page headline/tag (`waitUntil` / poll) before `assertIsDisplayed()` — `HorizontalPager.animateScrollToPage` races immediate assertions.

### Pipeline UI-diff gate

Use explicit path globs (see `moventiq-pipeline` skill): `androidApp/src/main/kotlin/**/ui/**`, `iosApp/iosApp/Features/**`, `iosApp/iosApp/UI/Components/**`, `iosApp/iosApp/UI/Theme/**`.

### ViewModel initial state

Pass store-derived fields into `MutableStateFlow` / `OnboardingUiState` init — not default + `init { update }`.

### Dynamic enum counts

`entries.size` / `allCases.count` — not `const val COUNT = 3`.

### Localization

Android: `strings.xml`. iOS: `Localizable.strings` for **all** user-facing copy in a file.

## Tests & previews

| File type | Required |
|---|---|
| Use case | `commonTest` happy + edge |
| Repository | `androidHostTest` / `iosTest` |
| ViewModel | event → state unit test |
| `*Content` | light + dark preview, fake `UiState` |
| Critical flow | UI test or cite existing coverage |

Naming: `{subject}_{outcome}_{condition}` — see `moventiq-unit-tests` / `moventiq-ui-tests`.

## Inline comment format

```markdown
**critical** `path/to/File.kt:42` — UI imports `TaskDao`.
Fix: call `CreateTask` from ViewModel.
```

Every inline comment must have **Fix:** or **Why not:**. No filler praise.

## Walkthrough template

```markdown
## Walkthrough
**Mode:** chill · **Scope:** <N files, ~N lines> · <intent + main risk>

## Summary
<2–3 bullets>

## Pre-merge checks
| Check | Status |
|---|---|
| Architecture (no UI→DAO) | ✅/❌ |
| MVP scope | ✅/❌ |
| Previews light+dark | ✅/❌ |
| Unit tests for new logic | ✅/❌ |
| State flow | ✅/❌ |
| i18n / design tokens | ✅/❌ |

## Inline findings
| Severity | Count |
|---|---|
| critical | N |
| major | N |

### critical / major
- `path:line` — issue — Fix: …

## Walkthrough-only (minor/nit)
- …

## Verdict
✅ Approve / 🔄 Request changes
```

## Tuning loop

| Symptom | Action |
|---|---|
| Too many nits | Stay chill; walkthrough-only for minor |
| Missing rules | Add to § Path instructions |
| Review too long | Split PR; check skip paths |
| Need depth | Use **assertive** mode once |

## Updating rules

Edit **this skill only**. Keep global bullets ≤10; path-specific detail in § Path instructions.

## Further reading

- [Google eng practices — code review](https://google.github.io/eng-practices/review/)
- [Cursor Agent Skills](https://cursor.com/docs/context/skills)
- [RESOURCES.md § Local code review](../../../RESOURCES.md)

## Related skills

- UI: `moventiq-ui-architecture` · Tests: `moventiq-unit-tests`, `moventiq-ui-tests`
- Pipeline: `moventiq-pipeline` (review / verify step 5) · CI: `moventiq-ci`
- Platform docs: [RESOURCES.md](../../../RESOURCES.md)
