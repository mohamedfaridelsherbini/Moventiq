# Static analysis config

| File | Tool |
|---|---|
| `.editorconfig` | Editor + ktlint baseline |
| `config/detekt/detekt.yml` | detekt 2.x rules |
| `iosApp/.swiftlint.yml` | SwiftLint |

```bash
./gradlew staticAnalysis          # detekt + ktlint + Android Lint
./gradlew ktlintFormat            # auto-fix Kotlin formatting
swiftlint lint --strict --config iosApp/.swiftlint.yml iosApp/iosApp
```

Modules: `androidApp`, `sharedLogic` (deprecated `sharedUI` excluded).
