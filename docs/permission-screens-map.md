# Permission screens map

Canonical reference for Moventiq onboarding permission flow (Android + iOS). Design source: `Moventiq.pen`. Tokens: [DESIGN.md](../DESIGN.md).

## App flow

```
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
| Denied steps | Numbered list in bordered card |

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

## Persistence flags

| Key | Meaning |
|-----|---------|
| `location_permission_prompt_completed` | User completed location step (granted, skipped, or limited) |
| `notification_permission_prompt_completed` | User completed notification step |
| `limited_features_acknowledged` | User chose “Use limited features” (skips notification prompt) |
| `show_location_denied_screen` | Show denied recovery after Allow was rejected |

## Show rules

1. **Location Permission** — onboarding done, location not adequate, `!location_permission_prompt_completed`, `!limited_features_acknowledged`.
2. **Permission Denied** — `show_location_denied_screen` after Allow rejection (not “Maybe later”).
3. **Notification Permission** — location step done, `!limited_features_acknowledged`, notifications required and not granted, `!notification_permission_prompt_completed`.

Non-blocking: “Maybe later”, “Not now”, and “Use limited features” all reach Main.

## Code locations

| Platform | Path |
|----------|------|
| Android | `androidApp/.../ui/permissions/` |
| iOS | `iosApp/iosApp/Features/Permissions/` |
