---
name: moventiq-pipeline
description: >-
  Runs Moventiq project skills in order — implement, verify, or review pipelines.
  Use when the user asks to run all skills, run skills in order, full workflow,
  pre-push check, PR-ready, or complete pipeline.
---

# Moventiq pipeline

Orchestrator for `.cursor/skills/`. **Read each skill's `SKILL.md` fully before that step**, then execute it. Do not skip reading — skills contain commands and gates.

Report after every step: `✅ pass` · `⏭️ skip (reason)` · `❌ fail (fix)`.

**Stop** on ❌ at a **gate** step unless the user says continue. Gate steps are marked **GATE**.

## Pick a pipeline

| Pipeline | When |
|---|---|
| **verify** | Pre-push, PR-ready, "run all checks", CI parity |
| **implement** | New feature end-to-end (UI + data + tests + review) |
| **review** | Code review only on a diff or PR |
| **data** | sharedLogic / Room / repositories only |

If the user does not specify, default to **verify**.

---

## Pipeline: verify (default)

Run in this exact order:

| # | Skill | Action | Gate |
|---|---|---|---|
| 1 | `moventiq-context` | Confirm scope, modules touched, definition of done | |
| 2 | `moventiq-ui-architecture` | Audit UI diff against architecture checklist (file split, SOLID, previews, test tags) | **GATE** if UI changed |
| 3 | `moventiq-unit-tests` | Run unit tests for touched modules; add missing tests for new logic | **GATE** |
| 4 | `moventiq-ui-tests` | Run UI tests if UI changed; add missing flow/content tests | **GATE** if UI changed |
| 5 | `moventiq-code-review` | Chill self-review on diff (1–5 inline findings max) | **GATE** if critical |

Invoke: `@moventiq-code-review review my diff` — local skill built on CodeRabbit + Gemini review concepts (see skill § Design philosophy).
| 6 | `moventiq-ci` | Run CI-equivalent commands locally | **GATE** |

Skip step 2 with ⏭️ when the diff has **no UI** (`androidApp/…/ui`, `iosApp/…/Features`, `Components`, `Theme`).

### verify — commands (after skills 3–4)

```bash
# Unit (adjust package filter to touched area)
./gradlew :androidApp:testDebugUnitTest --tests "com.mohamedfaridelsherbini.moventiq.<feature>.*"
./gradlew :sharedLogic:testAndroidHostTest

# Android UI (instrumented — no --tests flag)
./gradlew :androidApp:connectedDebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.mohamedfaridelsherbini.moventiq.<feature>

# iOS (Mac only) — shared scheme in xcshareddata; delete xcuserdata/iosApp.xcscheme overrides
cd iosApp && xcodebuild test -project iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 17 Pro,OS=latest' CODE_SIGNING_ALLOWED=NO

# Android UI (managed emulator — no physical device required)
./gradlew :androidApp:pixel6Api36DebugAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=com.mohamedfaridelsherbini.moventiq.ui.splash

# CI parity
./gradlew :sharedLogic:testAndroidHostTest :androidApp:assembleDebug
```

---

## Pipeline: implement

Run in this exact order:

| # | Skill | When to skip |
|---|---|---|
| 1 | `moventiq-context` | Never |
| 2 | `moventiq-pencil-design` | Skip if no UI / no `.pen` changes |
| 3 | `moventiq-ui-architecture` | Skip if no UI |
| 4 | `moventiq-room-kmp` | Skip if no data layer |
| 5 | `moventiq-compose-ui` | Skip if not Android UI |
| 6 | `moventiq-swiftui-ui` | Skip if not iOS UI |
| 7 | `moventiq-geofencing` | Skip if no location/geofence/arrival |
| 8 | `moventiq-unit-tests` | Never for new logic |
| 9 | `moventiq-ui-tests` | Skip if no UI |
| 10 | `moventiq-code-review` | Never — self-review before done |
| 11 | `moventiq-ci` | Never — confirm CI green |

Steps 5 and 6 are **parallel platforms** — run both only for KMP UI parity; otherwise run the one that applies.

After step 11, run **verify** pipeline steps 2–6 if not already executed.

---

## Pipeline: review

| # | Skill |
|---|---|
| 1 | `moventiq-context` |
| 2 | `moventiq-code-review` |

Use **chill** mode unless the user asks for assertive/thorough.

---

## Pipeline: data

| # | Skill |
|---|---|
| 1 | `moventiq-context` |
| 2 | `moventiq-room-kmp` |
| 3 | `moventiq-unit-tests` |
| 4 | `moventiq-code-review` |
| 5 | `moventiq-ci` |

---

## Final report template

```markdown
## Moventiq pipeline — <pipeline name>

| # | Skill | Status | Notes |
|---|---|---|---|
| 1 | moventiq-context | ✅ | … |
| 2 | … | ⏭️ | not applicable — no UI |
| … | | | |

**Verdict:** ✅ Ready / 🔄 Fix required

**Blockers:** (list or "none")
**Commands run:** (list or "none")
```

## Rules

- **Order matters** — audit ui-architecture before UI tests; do not run code-review before tests; do not implement UI before reading ui-architecture.
- **One skill file per step** — path: `.cursor/skills/<name>/SKILL.md`
- **Do not duplicate** skill content in chat — read, apply, report status.
- **Platform deps:** iOS steps require Mac; skip with ⏭️ and note if unavailable.
- **Token discipline:** follow `~/.cursor/skills/token-saver/SKILL.md` — short status lines, full detail only on failures.

## Related

- Entry context: `moventiq-context`
- PR babysitting (after pipeline): `babysit` skill (user-level)
