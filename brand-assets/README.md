# Moventiq — Logo Assets (SVG / Vector)

Pixel-accurate vector source for the Moventiq mark. All files trace the same geometry
(viewBox `0 0 100 100`): indigo **M**, with a cyan **location pin** in the valley and a
cyan **check** in the cradle.

## Colors
| Token | Hex |
|---|---|
| Primary (Indigo) | `#4F46E5` |
| Accent (Cyan) | `#06B6D4` |
| Ink (mono) | `#0F172A` |
| Gradient | `#4F46E5 → #2563EB → #06B6D4` |

## `svg/`
| File | Use |
|---|---|
| `moventiq-mark.svg` | Primary two-tone mark, transparent bg (web, docs, favicon source) |
| `moventiq-mark-mono.svg` | Single-color ink mark (print, 1-color contexts) |
| `moventiq-mark-white.svg` | White mark for dark / colored backgrounds |
| `moventiq-icon-rounded.svg` | App tile — gradient rounded square + white mark (web/PWA, favicon) |
| `moventiq-appicon-ios.svg` | **iOS** app icon — full-bleed gradient square, no rounding, no alpha (1024) |
| `moventiq-icon-maskable.svg` | Maskable / adaptive — mark inside the safe zone (PWA `purpose="maskable"`) |

## `android/` (Material 3 adaptive icon — drop into `res/`)
| File | Use |
|---|---|
| `mipmap-anydpi-v26/ic_launcher.xml` | Adaptive icon (background + foreground + monochrome) |
| `mipmap-anydpi-v26/ic_launcher_round.xml` | Round variant |
| `drawable/ic_launcher_background.xml` | Gradient background (VectorDrawable) |
| `drawable/ic_launcher_foreground.xml` | White mark in the 72dp safe zone |
| `drawable/ic_launcher_monochrome.xml` | Themed-icon silhouette (Android 13+) |

Set `minSdk` ≥ 26 (or keep legacy PNG fallbacks). Android Studio can import any
`svg/*.svg` via **File → New → Vector Asset** if you prefer regenerating the VectorDrawables.

## iOS
Use `moventiq-appicon-ios.svg` as the App Icon source. In Xcode 15+ asset catalogs choose
**Single Size** and enable **Preserve Vector Data**, or render a 1024×1024 PNG (no alpha)
from this SVG for the App Store.

## Generated PNGs (26)
Rendered from the SVGs with `sharp` (vector-accurate). Regenerate any time:
```bash
cd .build && npm install && node render.mjs
```

**`android/mipmap-*/`** — legacy launcher fallback (pre-API 26)
`ic_launcher.png` (rounded) + `ic_launcher_round.png` (circle) at 48/72/96/144/192.

**`ios/AppIcon.appiconset/`** — drop straight into Xcode. Includes `Contents.json` and
PNGs 20·29·40·58·60·76·80·87·120·152·167·180·1024 (no alpha, App-Store safe).

**`store/`**
- `appstore/AppStore-1024.png` — 1024×1024, no alpha (Apple App Store)
- `playstore/ic_launcher-playstore.png` — 512×512 (Play listing icon)
- `playstore/feature-graphic.png` — 1024×500, no alpha (Play feature graphic)

The Android **adaptive** icon (`mipmap-anydpi-v26` + `drawable/*.xml`) is the real
modern icon; the mipmap PNGs are only the legacy fallback.
