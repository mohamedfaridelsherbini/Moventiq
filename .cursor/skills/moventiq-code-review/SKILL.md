---
name: moventiq-code-review
description: >-
  Reviews Moventiq PRs and diffs for clean architecture, SOLID UI, previews, tests,
  and MVP constraints. Use for code review, PR feedback, or pre-merge audit.
---

# Moventiq code review

Cross-ref: `moventiq-ui-architecture`, `moventiq-context`, `.cursor/rules/`.

## Workflow

1. Identify touched layers: `:sharedLogic`, `:androidApp`, `:iosApp`
2. Run checklists below
3. Classify: **Blocker** → **Major** → **Minor** → **Nit**
4. Output using template at bottom

## Blockers

| Area | Block if |
|---|---|
| Architecture | UI imports Room, DAO, or Entity types |
| MVP scope | Profile, auth, sign-in, avatar, PRO badge, integrations, "Delete account" |
| Design tokens | Hardcoded hex colors, dp spacing, or font sizes in UI |
| Themes | New screen/component missing **light and dark** |
| Previews | New `*Content` or component without `@Preview` / `#Preview` (both themes) |
| State | Business logic or repository calls inside Composable/View body |
| Tests | New use case or ViewModel with zero unit tests |
| commonMain | Platform APIs (Android/iOS) imported in domain or commonMain data |
| Design file | `Moventiq.pen` accessed via Read/Grep instead of Pencil MCP |

## Major

| Area | Flag if |
|---|---|
| SOLID | ViewModel > ~200 lines mixing mapping + domain logic |
| Components | Duplicated markup instead of design-system component |
| Previews | Preview uses real ViewModel, Koin, or Room |
| Navigation | Broken back stack; duplicate FAB on screen (TabBar FAB only) |
| A11y | Icon-only control missing `contentDescription`/label; touch target < 44dp |
| Deprecation | New UI added to `:sharedUI` instead of `:androidApp` |
| Geofencing | Geofence API used directly from `commonMain` |
| Data | Location change without triggering geofence sync |

## Minor / Nit

- Preview only in one theme
- Missing `PreviewParameterProvider` for multi-state component
- Test names not descriptive
- Missing `testTag` on list items used in UI tests

## Architecture quick check

```
✅ UI → ViewModel → UseCase → Repository → DAO → Room
❌ UI → DAO
❌ ViewModel → RoomDatabase
❌ Domain → android.* / platform.*
```

## Tests & previews audit

For each new file in the diff:

| File type | Required |
|---|---|
| Use case | `commonTest` happy path + edge case |
| Repository | `androidHostTest` or `iosTest` with in-memory Room |
| ViewModel | Unit test: event → state transition |
| `*Content` / component | Light + dark preview with fake `UiState` |
| Critical flow | UI test plan or existing coverage noted |

## Output template

```markdown
## Summary
<1–2 sentences>

## Blockers
- [ ] `path:line` — issue — fix: …

## Major
- …

## Minor / Nit
- …

## Tests & previews
- Previews: ✅/❌ (list missing)
- Unit tests: ✅/❌
- UI tests: ✅/❌/N/A

## Verdict
Approve / Request changes
```

## Related skills

- UI rules: `moventiq-ui-architecture`
- Unit tests: `moventiq-unit-tests`
- UI tests: `moventiq-ui-tests`
- Static analysis: `kotlin-static-analysis`, `swift-static-analysis`
