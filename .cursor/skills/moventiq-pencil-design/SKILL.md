---
name: moventiq-pencil-design
description: >-
  Reads and implements Moventiq screens from Moventiq.pen via Pencil MCP.
  Use when building UI, components, matching the design, or referencing
  Moventiq.pen.
---

# Moventiq Pencil design workflow

## Access rules

- Design file at repo root: **`Moventiq.pen`** — encrypted, **Pencil MCP only**
- Never Read/Grep/edit `Moventiq.pen` as text
- Numeric tokens from [DESIGN.md](../../../DESIGN.md); layout/composition from `Moventiq.pen`

## MCP workflow

1. `get_editor_state(include_schema: true)` — current file + schema
2. `batch_get` — fetch screen/component nodes by ID or pattern
3. `get_screenshot` — visual reference before implementing
4. `batch_design` — design edits only when explicitly requested

## Screen inventory (MVP — all light + dark)

| Screen | Purpose |
|---|---|
| Splash | Brand splash, init |
| Onboarding 1–3 | Core concept (3 steps) |
| Location Permission | Foreground/background location |
| Notification Permission | Push permission rationale |
| Permission Denied | Enable location in Settings |
| First Location Setup | Suggested first place |
| Home Main | Active location, triggered tasks, today |
| All Tasks | Tasks tab, grouped by place |
| Locations | Saved places list |
| Location Tasks | Tasks for one place |
| Create / Edit Location | Map picker, radius |
| Create / Edit Task | Task form + location link |
| Task Detail | View/complete single task |
| Arrival | Geofence-triggered takeover |
| Notification Preview | Lock-screen preview |
| Empty State | No tasks |
| Search Locations / Tasks | Scoped search |
| Settings + sub-screens | Notifications, Geofencing, Appearance, Privacy, About |

Reference boards (not shipped): Navigation System, Settings States, Loading/Skeleton, Empty/Error States, Arrival States.

## Component mapping (Moventiq.pen → code)

| Design | Android Compose | SwiftUI |
|---|---|---|
| `Component/TabBar` | `MoventiqBottomBar` | `MoventiqBottomBar` |
| `Component/PrimaryButton` | `PrimaryButton` | `PrimaryButton` |
| `Component/SecondaryButton` | `SecondaryButton` | `SecondaryButton` |
| `Component/TaskRow` | `TaskRow` | `TaskRowView` |
| `Component/TaskChip` | `TaskChip` | `TaskChipView` |
| `Component/LocationCard` | `LocationCard` | `LocationCardView` |
| `Component/LocationBanner` | `ActiveLocationCard` | `ActiveLocationCardView` |
| `Component/OfflineBanner` | `OfflineBanner` | `OfflineBannerView` |
| `Component/Dialog` | `MoventiqDialog` | `MoventiqDialog` |
| `Component/SectionHeader` | `SectionHeader` | `SectionHeaderView` |
| `Component/MoventiqSymbol` | brand mark from `brand-assets/` | same |

Build components in `:androidApp/ui/components/` and `iosApp/iosApp/UI/Components/`.

## Navigation (TabBar)

```
Home · Tasks · (+) · Places · Settings
```

- Center FAB is **context-aware**: Places tab → add location; Tasks tab → add task; Home → default capture
- Do not duplicate a second FAB on individual screens

## Maps in design

Map frames in `Moventiq.pen` are mockups. In code:

- Android: Google Maps Compose + radius circle overlay
- iOS: MapKit `Map` + circle overlay

Bind radius slider to geofence radius (default 200m).

## Implementation checklist

- [ ] Screenshot fetched from Pencil before coding
- [ ] Light + dark variants implemented
- [ ] Tokens from `MoventiqTheme`, not hardcoded hex
- [ ] Reusable components extracted (not copy-pasted markup)
- [ ] Previews added (see `moventiq-ui-architecture`)
