# Feature: `<feature-name>`

Status: **draft** | accepted | implemented  
Slug: `<feature-slug>`

## Summary

One paragraph: what the user can do and why it exists.

## State machine

```mermaid
stateDiagram-v2
    [*] --> ScreenA
    ScreenA --> ScreenB: primary_action
    ScreenB --> Done: confirm
    ScreenA --> Done: skip
```

## Sequence (optional)

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant VM as ViewModel
    participant OS
    User->>UI: tap Allow
    UI->>VM: Event.Allow
    VM->>OS: request permission
    OS-->>VM: granted / denied
    VM-->>UI: updated state
```

## Component sketch (optional)

```mermaid
classDiagram
    class FeatureViewModel {
        +state
        +onEvent()
    }
    class FeatureStepResolver {
        +resolve(input) Step
    }
    FeatureViewModel --> FeatureStepResolver
```

## Behavior cases

| # | Trigger | Same session | After background / cold start | Test layer |
|---|---|---|---|---|
| 1 | | | | unit / UI |
| 2 | | | | unit / UI |

## Persistence (if any)

| Key / field | Meaning |
|---|---|
| | |

## Platform notes

| Behavior | Android | iOS |
|---|---|---|
| | | |

## Related

- Pencil: `Moventiq.pen` — `<screen names>`
- Copy / strings: link if separate doc
