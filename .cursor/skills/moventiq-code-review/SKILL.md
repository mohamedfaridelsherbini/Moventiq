---
name: moventiq-code-review
description: >-
  Reviews Moventiq PRs with CodeRabbit performance — diff-scoped, high-signal,
  chill profile (1–5 actionable comments), incremental on re-review. Uses
  .coderabbit.yaml rules. Use for PR feedback, triaging CodeRabbit, pre-merge audit.
  External refs: RESOURCES.md (KMP/Android/iOS learning + Koin).
---

# Moventiq code review

**Source of truth:** [`.coderabbit.yaml`](../../../.coderabbit.yaml) — keep skill and YAML in sync.

Cross-ref: `moventiq-ui-architecture`, `moventiq-context`, `moventiq-unit-tests`, `moventiq-ui-tests`, [RESOURCES.md](../../../RESOURCES.md), `AGENT.md`, `.cursor/rules/`.

## CodeRabbit performance model

Match how CodeRabbit actually behaves — not a exhaustive audit checklist.

| Lever | Moventiq setting | Effect |
|---|---|---|
| Profile | `chill` | Bugs + architecture breaks inline; style nits suppressed |
| Scope | Diff + direct call sites only | Faster reviews, less noise |
| Comment budget | **1–5 inline** per typical PR | High signal; batch the rest in walkthrough |
| Incremental | `auto_incremental_review: true` | Re-review only new commits; skip resolved threads |
| Path filters | build/, assets, `.pen`, reports | Skip files that never need AI review |
| Path instructions | Per-module rules | Relevance over generic advice |
| Pre-merge checks | Custom check in YAML | Gate in summary table, not 10 inline duplicates |
| Learnings | Reply to false positives | `@coderabbitai` learns; reduces repeat noise |
| Guidelines | `AGENT.md` (auto-scanned) | Don't restate what's already in repo docs |

**PR size:** CodeRabbit slows on large PRs (~15+ min at 1000+ lines). Prefer **≤400 changed lines** per PR. If larger, note in walkthrough: "consider splitting."

## Review modes

| Mode | When | Inline budget | minor/nit |
|---|---|---|---|
| **chill** (default) | Normal PRs, agent reviews | 1–5 | Walkthrough bullets only |
| **assertive** | User asks for thorough review / pre-release audit | 3–8 | May inline |
| **incremental** | Follow-up after push | New issues only | Same as chill |

Say which mode you used in the walkthrough header.

## Severity (inline comments)

| Label | When | chill: inline? |
|---|---|---|
| **critical** | Architecture break, MVP scope, missing tests/previews for new logic, hardcoded tokens | Always |
| **major** | SOLID/a11y/test anti-pattern in diff | Yes, if clear |
| **minor** | testTag, single-theme preview, naming | Walkthrough only |
| **nit** | Optional refactor | Walkthrough only (or omit) |

One root cause → **one comment**. Do not file separate comments for the same fix.

## Workflow

### CodeRabbit (automated)

1. Reads `.coderabbit.yaml` + `AGENT.md` → walkthrough + sparse inline + pre-merge checks
2. Author fixes **critical** / **major** (or replies — creates learnings)
3. Push → **incremental** review (delta only)
4. Merge: CI green + critical/major resolved + pre-merge checks pass

Trigger manually: `@coderabbitai review` · Pre-merge: `@coderabbitai run pre-merge checks`

### Agent review (match CodeRabbit performance)

1. **Read the diff first** — `git diff` / PR files changed; ignore untouched modules
2. Match each changed path to `.coderabbit.yaml` `path_instructions`
3. Apply global `instructions` — respect **Do NOT comment** list
4. Count inline comments — stay within budget; demote extras to walkthrough
5. Output walkthrough template (short)
6. **Verdict:** Approve if zero critical; Request changes if any critical/unresolved major

### Incremental re-review

1. Skip resolved / outdated threads
2. Only comment on **new commits** or still-open critical/major
3. Do not re-list previously fixed items
4. If all prior critical/major fixed → "No new issues" + Approve

### Triage CodeRabbit comments

1. Unresolved threads only
2. False positive → one-line reply (feeds learnings); resolve after push if applicable
3. Fix critical/major in scoped commits
4. Never weaken `.coderabbit.yaml` or CI to greenwash a PR

## Architecture quick check (diff only)

```
✅ UI → ViewModel → UseCase → Repository → DAO → Room
❌ UI → DAO  ❌ ViewModel → RoomDatabase  ❌ Domain → android.* / platform.*
```

For platform conventions and official patterns, see [RESOURCES.md](../../../RESOURCES.md) — **Kotlin Multiplatform learning**, **Android official**, **iOS official**.

## Tests & previews (only for new/changed symbols)

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

No filler ("Nice work!", "Consider maybe…"). Every inline comment must have **Fix:** or **Why not:**.

## Walkthrough template (keep short — CodeRabbit style)

```markdown
## Walkthrough
**Mode:** chill · **Scope:** <N files, ~N lines> · <1 sentence intent + main risk>

## Summary
<2–3 bullets max — what changed, not a file list>

## Pre-merge checks
| Check | Status |
|---|---|
| Architecture (no UI→DAO) | ✅/❌ |
| MVP scope | ✅/❌ |
| Previews light+dark | ✅/❌ |
| Unit tests for new logic | ✅/❌ |

## Inline findings
| Severity | Count |
|---|---|
| critical | N |
| major | N |

### critical
- `path:line` — issue — Fix: …

### major
- …

## Walkthrough-only (minor/nit)
- …

## Verdict
✅ Approve / 🔄 Request changes
```

Omit empty sections. Do not duplicate pre-merge check failures as five separate inline comments.

## Tuning loop (when reviews feel slow or noisy)

| Symptom | Action |
|---|---|
| Too many nits | Stay on `chill`; move rules to walkthrough-only in YAML |
| Missing Moventiq rules | Add `path_instructions`, not more global bullets |
| Same false positive repeats | Reply on PR to train learnings |
| Review >15 min | Split PR; check path_filters |
| Security/architecture slips | Temporarily use **assertive** for that PR only |

## Updating rules

1. Edit `.coderabbit.yaml` first
2. Mirror performance-relevant changes here (budget, suppress list, mode)
3. Keep global `instructions` ≤10 focused bullets — path-specific detail goes in `path_instructions`

## Related skills

- UI: `moventiq-ui-architecture` · Tests: `moventiq-unit-tests`, `moventiq-ui-tests`
- PR loop: `babysit` · CI: `moventiq-ci` · Static analysis: `kotlin-static-analysis`, `swift-static-analysis`
- Learning & official docs: [RESOURCES.md](../../../RESOURCES.md) — [KMP learning index](https://kotlinlang.org/docs/multiplatform/kmp-learning-resources.html), Android, iOS
