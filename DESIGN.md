---
version: "alpha"
name: "Moventiq"
description: "Location-aware productivity app. Tasks appear when users arrive at the right place."
colors:
  primary: "#4F46E5"
  primary-container: "#EEF2FF"
  secondary: "#2563EB"
  accent: "#06B6D4"
  success: "#10B981"
  warning: "#F59E0B"
  error: "#EF4444"
  dark: "#0F172A"
  surface-dark: "#111827"
  surface-dark-elevated: "#1E293B"
  light: "#FFFFFF"
  surface-light: "#F8FAFC"
  surface-light-elevated: "#FFFFFF"
  text-primary: "#0F172A"
  text-secondary: "#475569"
  text-muted: "#64748B"
  text-on-dark: "#FFFFFF"
  border-light: "#E2E8F0"
  border-dark: "#334155"
typography:
  display-lg:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "3rem"
    fontWeight: 700
    lineHeight: "3.5rem"
    letterSpacing: "-0.04em"
  h1:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "2rem"
    fontWeight: 700
    lineHeight: "2.5rem"
    letterSpacing: "-0.03em"
  h2:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "1.5rem"
    fontWeight: 700
    lineHeight: "2rem"
    letterSpacing: "-0.02em"
  title:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "1.125rem"
    fontWeight: 600
    lineHeight: "1.5rem"
    letterSpacing: "-0.01em"
  body-md:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: "1.5rem"
    letterSpacing: "0em"
  body-sm:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "0.875rem"
    fontWeight: 400
    lineHeight: "1.25rem"
    letterSpacing: "0em"
  label-md:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "0.875rem"
    fontWeight: 600
    lineHeight: "1.25rem"
    letterSpacing: "0em"
  label-caps:
    fontFamily: "Plus Jakarta Sans"
    fontSize: "0.75rem"
    fontWeight: 700
    lineHeight: "1rem"
    letterSpacing: "0.08em"
rounded:
  xs: "6px"
  sm: "10px"
  md: "16px"
  lg: "24px"
  xl: "32px"
  full: "999px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "16px"
  lg: "24px"
  xl: "32px"
  xxl: "48px"
components:
  button-primary:
    backgroundColor: "{colors.primary}"
    textColor: "{colors.text-on-dark}"
    typography: "{typography.label-md}"
    rounded: "{rounded.md}"
    padding: "14px 20px"
  button-secondary:
    backgroundColor: "{colors.primary-container}"
    textColor: "{colors.primary}"
    typography: "{typography.label-md}"
    rounded: "{rounded.md}"
    padding: "14px 20px"
  card-light:
    backgroundColor: "{colors.surface-light-elevated}"
    textColor: "{colors.text-primary}"
    rounded: "{rounded.lg}"
    padding: "20px"
  card-dark:
    backgroundColor: "{colors.surface-dark-elevated}"
    textColor: "{colors.text-on-dark}"
    rounded: "{rounded.lg}"
    padding: "20px"
  zone-card:
    backgroundColor: "{colors.surface-light-elevated}"
    textColor: "{colors.text-primary}"
    rounded: "{rounded.lg}"
    padding: "18px"
  task-chip:
    backgroundColor: "{colors.primary-container}"
    textColor: "{colors.primary}"
    typography: "{typography.label-md}"
    rounded: "{rounded.full}"
    padding: "8px 12px"
  location-active-banner:
    backgroundColor: "{colors.dark}"
    textColor: "{colors.text-on-dark}"
    rounded: "{rounded.xl}"
    padding: "20px"
---

## Overview

Moventiq is a location-aware productivity product. The interface should feel calm, intelligent, and immediate: users link tasks to places, then see the right tasks when they arrive.

The design language is **premium SaaS + mobile productivity**. It should look modern enough for a startup product, but simple enough to scale across Android, iOS, and web.

The approved logo direction is:

- **M** for Moventiq.
- **Location pin** for place.
- **Subtle checkmark** for completed tasks.

The logo should stay simple. Do not overload it with routes, arrows, maps, roads, GPS symbols, or delivery-style visuals.

## Colors

The palette uses Indigo, Blue, and Cyan to communicate productivity, trust, motion, and context awareness.

- **Primary Indigo (#4F46E5):** Main brand color. Use for primary actions, selected states, app icon accents, and high-emphasis interactive elements.
- **Secondary Blue (#2563EB):** Use for links, active navigation, secondary highlights, and gradients.
- **Accent Cyan (#06B6D4):** Use sparingly for location-aware moments, arrival states, notifications, and contextual highlights.
- **Dark Navy (#0F172A):** Main dark theme background and premium marketing surface.
- **Success (#10B981):** Completed tasks and positive confirmation.
- **Warning (#F59E0B):** Permission warnings, location disabled, or attention states.
- **Error (#EF4444):** Failed sync, denied permission, destructive actions.

Recommended brand gradient:

```css
linear-gradient(135deg, #4F46E5 0%, #2563EB 55%, #06B6D4 100%)
```

Use gradients mostly for brand surfaces, hero sections, app icon backgrounds, and launch assets. Do not use gradients on every button or card.

## Typography

Use **Plus Jakarta Sans** as the primary font across Android, iOS, and web. It gives Moventiq a modern software-first look while staying readable on small screens.

Fallbacks:

- Android: `FontFamily.Default` or bundled Plus Jakarta Sans.
- iOS: Plus Jakarta Sans if bundled; otherwise San Francisco.
- Web: `Plus Jakarta Sans, Inter, system-ui, sans-serif`.

Typography should be clear, spacious, and functional. Avoid decorative typefaces.

## Layout

Use a clean grid-based system with generous spacing.

Core rules:

- Use `16px` as the base spacing unit.
- Use `24px` horizontal screen padding on large mobile screens.
- Use `16px` horizontal padding on compact mobile screens.
- Cards should breathe. Avoid dense task lists unless the user explicitly switches to compact mode.
- Important location-triggered content should appear in a focused card or bottom sheet.

Primary screen hierarchy:

1. Current location context.
2. Pending tasks for this place.
3. Quick actions.
4. Other zones and upcoming tasks.

## Elevation & Depth

Moventiq should feel flat and modern. Use depth only for hierarchy.

- Light mode cards: white surface with subtle border.
- Dark mode cards: elevated navy/slate surfaces.
- Avoid heavy shadows.
- Prefer borders and background contrast over drop shadows.
- Use motion and state transitions rather than visual noise.

Recommended light card style:

```css
background: #FFFFFF;
border: 1px solid #E2E8F0;
border-radius: 24px;
```

Recommended dark card style:

```css
background: #1E293B;
border: 1px solid #334155;
border-radius: 24px;
```

## Shapes

Shapes should be rounded, friendly, and precise.

- Buttons: `16px` radius.
- Cards: `24px` radius.
- Bottom sheets: `32px` top radius.
- Chips: full pill radius.
- App icon: platform-native adaptive shape.

Do not use sharp enterprise-style rectangles. Do not use playful blob shapes.

## Components

### Primary Button

Use for the main action on each screen:

- Add task.
- Create zone.
- Enable location.
- Save reminder.

Style:

- Background: Primary Indigo.
- Text: White.
- Radius: 16px.
- Height: 52px minimum.
- Typography: label-md.

### Secondary Button

Use for supportive actions:

- Later.
- Skip.
- View all.
- Edit zone.

Style:

- Background: Primary container.
- Text: Primary Indigo.
- Radius: 16px.

### Zone Card

A zone card represents a place such as Home, Work, Gym, Supermarket, or Kita.

Include:

- Zone icon or pin.
- Zone name.
- Radius.
- Pending task count.
- Last triggered status.

States:

- Default.
- Active location.
- Disabled location.
- Permission required.
- No tasks.

### Task Row

A task row must be fast to scan.

Include:

- Completion checkbox.
- Task title.
- Optional zone label.
- Optional due/context hint.
- Optional priority indicator.

States:

- Pending.
- Completed.
- Snoozed.
- Location-triggered.
- Expired.

### Location Active Banner

Shown when the user enters a saved zone.

Content pattern:

```text
You are at Work
4 tasks are waiting here
```

Actions:

- View tasks.
- Snooze.
- Mark all done.

### Permission Screen

Location permissions are sensitive. Explain clearly why permission is needed.

Tone:

- Direct.
- Transparent.
- No dark patterns.

Content pattern:

```text
Moventiq needs location access to show your place-based tasks when you arrive.
```

Actions:

- Enable location.
- Continue without location.

## Do's and Don'ts

### Do

- Keep the UI minimal.
- Make location context visible but not intrusive.
- Use Cyan for important location-aware moments.
- Make tasks easy to complete with one tap.
- Use native permission patterns on Android and iOS.
- Keep app icons simple and recognizable.
- Prefer native UI for geofencing, permissions, and notifications.

### Don't

- Do not make the app look like Google Maps.
- Do not use route lines, roads, arrows, compass icons, or navigation UI.
- Do not overuse gradients.
- Do not hide the main task list behind too many screens.
- Do not make location tracking feel creepy.
- Do not use aggressive notification colors.
- Do not use complex logo variants in small UI surfaces.

## Platform Notes

### Android

Use Material 3 patterns with native Jetpack Compose.

Recommended mapping:

- Primary: `#4F46E5`
- Secondary: `#2563EB`
- Tertiary: `#06B6D4`
- Background: `#F8FAFC`
- Dark background: `#0F172A`

Use Android geofencing and native notification behavior. Notification icon should be a simple one-color version of the logo mark.

### iOS

Use SwiftUI with native iOS navigation and permission flows.

Use:

- Tint: `#4F46E5`
- Accent: `#06B6D4`
- Dark background: `#0F172A`

Respect iOS location permission expectations. Explain Always Allow only when it is genuinely required for background place triggers.

### Web

The web experience should focus on landing page, account management, and future dashboard use.

Use the full logo in the header. Use the icon-only logo for favicons and social previews.

## Brand Voice

Moventiq should sound calm, clear, and practical.

Use:

- “Tasks that surface at the right place.”
- “The right task. At the right place.”
- “You are here. These tasks matter now.”

Avoid:

- “Track everything.”
- “Never forget again.”
- “We follow your location.”
- Any wording that sounds invasive.

## Logo Usage

Use the logo mark as:

- App icon.
- Favicon.
- GitHub avatar.
- Social avatar.
- Splash icon.

Use the full logo as:

- Website header.
- App onboarding.
- App Store / Play Store marketing.
- Brand presentations.

Minimum sizes:

- Icon-only: 16px minimum, 24px preferred.
- Full logo: 120px width minimum.
- App icon: platform-defined sizes.

Clear space:

- Keep at least one pin-width of space around the logo.
- Do not place the logo too close to card edges.

Incorrect usage:

- Do not stretch.
- Do not rotate.
- Do not add shadows.
- Do not add glow.
- Do not outline the mark.
- Do not place on low-contrast backgrounds.
- Do not add arrows, roads, maps, or GPS symbols.
