---
name: moventiq-pencil-design
description: >-
  Reads and implements Moventiq screens from Moventiq.pen via Pencil MCP.
  Use when building UI, components, or matching the design file.
---

# Moventiq Pencil design

## Access

- **`Moventiq.pen`** at repo root — encrypted; **Pencil MCP only** (see `moventiq-pencil-only.mdc`)
- Numeric tokens: [DESIGN.md](../../../DESIGN.md)
- Screen list: [MVP.md](../../../MVP.md)

## MCP workflow

1. `get_editor_state(include_schema: true)`
2. `batch_get` — screen/component nodes
3. `get_screenshot` — visual reference before coding
4. `batch_design` — edits only when requested

## Component mapping

| Pencil | Android | iOS |
|---|---|---|
| `Component/TabBar` | `MoventiqBottomBar` | `MoventiqBottomBar` |
| `Component/PrimaryButton` | `MoventiqPrimaryButton` | `MoventiqPrimaryButton` |
| `Component/TaskRow` | `TaskRow` | `TaskRowView` |
| `Component/LocationCard` | `LocationCard` | `LocationCardView` |
| `Component/Dialog` | `MoventiqDialog` | `MoventiqDialog` |

Paths: `androidApp/.../ui/components/`, `iosApp/iosApp/UI/Components/`.

## TabBar

`Home · Tasks · (+) · Places · Settings` — center FAB context-aware by tab. No second FAB on list screens.

## Checklist

- [ ] Screenshot from Pencil before coding
- [ ] Light + dark; tokens from `MoventiqTheme`
- [ ] Reusable components, not copy-pasted markup
- [ ] Previews per `moventiq-ui-architecture`

## Related

- Platform implementation: `moventiq-ui-architecture/platforms.md`
- Behavior UML: `moventiq-feature-workflow`
