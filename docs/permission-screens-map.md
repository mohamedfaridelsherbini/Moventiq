# Permission screens map

Canonical reference for Moventiq onboarding permission flow (Android + iOS). Design source: `Moventiq.pen`. Tokens: [DESIGN.md](../DESIGN.md).

## App flow

```text
Splash → Onboarding? → Location Permission → (Permission Denied?) → Notification Permission? → Main
```

First Location Setup is **next** after this flow ([MVP.md](../MVP.md) §4 #4).

## Screen inventory

| # | Pencil screen | Light node | Dark node | Purpose |
|---|---------------|------------|-----------|---------|
| 1 | `Screen/Location Permission` | `oeITd` | `jgboQ` | Trust-first rationale before OS location prompts |
| 2 | `Screen/Notification Permission` | `JYMO7` | `KPDim` | Push rationale before OS notification prompt |
| 3 | `Screen/Permission Denied` | `jf5LT` | `H3wk2` | Recovery when location access is insufficient |

**Related (not flow routes):** Settings rows “Permission granted” / “Permission denied” in `Moventiq.pen` — future Settings → Privacy/Location status UI.

## Copy → string keys

| Key | English (Pencil) |
|-----|------------------|
| `permission_location_headline` | Allow location access |
| `permission_location_body` | Moventiq uses your location only to bring up the right tasks the moment you arrive at a place. That's it. |
| `permission_location_trust_1` | Used only to surface your place-based tasks |
| `permission_location_trust_2` | Works in the background when you arrive at a place |
| `permission_location_trust_3` | Your location never leaves your device |
| `permission_location_allow` | Allow location |
| `permission_location_later` | Maybe later |
| `permission_notification_headline` | Stay informed on arrival |
| `permission_notification_body` | Moventiq needs to send you notifications so your tasks appear the moment you arrive at a saved location. |
| `permission_notification_bullet_1` | Arrival reminders when you enter a place |
| `permission_notification_bullet_2` | See pending tasks instantly |
| `permission_notification_bullet_3` | Silent mode and quiet hours supported |
| `permission_notification_allow` | Allow Notifications |
| `permission_notification_skip` | Not now |
| `permission_denied_headline` | Permission Required |
| `permission_denied_body` | Moventiq needs location access to detect when you arrive at saved places. Please enable it in Settings. |
| `permission_denied_step_1` | Open the Settings app |
| `permission_denied_step_2` | Tap Privacy &amp; Location (Android) / Tap Moventiq → Location (iOS) |
| `permission_denied_step_3` | Set to Always Allow |
| `permission_denied_open_settings` | Open Settings |
| `permission_denied_limited` | Use limited features |

Trust row 2 was updated from Pencil (“Active only while you're using the app”) to match MVP geofencing (background / Always).

## UI composition

| Element | Implementation |
|---------|----------------|
| Primary CTA | `MoventiqPrimaryButton` (`Component/PrimaryButton` / `XIAsY`) |
| Secondary CTA | Text button (onboarding skip style) |
| Hero | 96dp circle, `primaryContainer`, Lucide icon (`map-pin`, `bell-ring`, `shield-off`) |
| Trust / bullets | Icon tile + label rows |
| Denied steps | Numbered list in bordered card (`rounded.xl` / 32dp corners) |
| Denied CTA icon | `external-link` Lucide icon on **Open Settings** primary button |

## Permission Denied screen (detail)

**When shown:** User taps **Allow location**, OS denies fine or background/Always access. Not shown for **Maybe later**.

**Layout (Pencil `jf5LT` / `H3wk2`):**

| Region | Spec |
|--------|------|
| Hero | 96dp circle, `errorContainer` fill, `shield-off` icon in `error` |
| Headline / body | Centered; keys `permission_denied_headline`, `permission_denied_body` |
| Steps card | 3 numbered rows; card uses `spacing.xl` (32dp) corner radius |
| Primary CTA | **Open Settings** + external-link icon → app Settings |
| Secondary CTA | **Use limited features** → Main, skips notification prompt |

**Return from Settings:** `ON_RESUME` (Android) / `willEnterForeground` (iOS) triggers `Refresh`; if location is now adequate, flow advances to notification or Main.

**Code:**

| Platform | File |
|----------|------|
| Android | `PermissionPreview.kt` (`LocationPermissionContent`, `NotificationPermissionContent`, `PermissionDeniedContent`) |
| iOS | `PermissionFlowView+Preview.swift` |

## Actions → platform APIs

| Screen | Control | Android | iOS |
|--------|---------|---------|-----|
| Location | Allow location | `RequestMultiplePermissions` (fine/coarse), then `ACCESS_BACKGROUND_LOCATION` (API 29+) | `requestWhenInUseAuthorization` → `requestAlwaysAuthorization` |
| Location | Maybe later | Mark prompt complete; continue | Same |
| Notification | Allow Notifications | `POST_NOTIFICATIONS` (API 33+) | `UNUserNotificationCenter.requestAuthorization` |
| Notification | Not now | Skip; mark complete | Same |
| Denied | Open Settings | `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` | `UIApplication.openSettingsURLString` |
| Denied | Use limited features | Main; geofencing off | Same |

### Manifest / Info.plist

- Android: `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `ACCESS_BACKGROUND_LOCATION`, `POST_NOTIFICATIONS`
- iOS: `NSLocationWhenInUseUsageDescription`, `NSLocationAlwaysAndWhenInUseUsageDescription`

## Recommended flow (Moventiq MVP)

| User action | Same session (stay in app) | App reopen from background |
|-------------|---------------------------|----------------------------|
| **Allow location** → OS grants | Notification (or Main) | Same |
| **Allow location** → OS denies | **Permission Denied** | **Permission Denied** |
| **Maybe later** (location) | Notification | **Location** |
| **Not now** (notification) | Main | **Notification** if location already granted; **Location** if not |
| **Use limited features** | Main | Main — no prompts |

Each screen has its own **session skip** flag. Skips reset when the app returns from background so pages re-prompt independently.

**Permission Denied** is never shown for Maybe later or Not now.

## Persistence flags

| Key | Meaning |
|-----|---------|
| `location_permission_prompt_completed` | Legacy — cleared on launch; use session skip instead |
| `notification_permission_prompt_completed` | Legacy — cleared on launch; use session skip instead |
| `limited_features_acknowledged` | User chose **Use limited features** — permanent opt-out of permission prompts |
| `show_location_denied_screen` | User tapped **Allow** and OS denied / partial grant — show recovery screen |
| `location_allow_attempted` | User tapped **Allow location** at least once |

## Show rules

1. **Location Permission** — no adequate location, not denied recovery, location not skipped this session.
2. **Permission Denied** — only after **Allow location** was rejected (not Maybe later / Not now).
3. **Notification Permission** — location granted or location skipped this session; notification needed, not granted, not skipped this session.

**App reopen:** session skips reset via `PermissionAppSession` — Android `MainActivity` onStart/onStop; iOS `RootView` `scenePhase` — so each page can re-prompt even from Main.

## Code locations

| Platform | Path |
|----------|------|
| Android | `androidApp/.../ui/permissions/` |
| iOS | `iosApp/iosApp/Features/Permissions/` |
