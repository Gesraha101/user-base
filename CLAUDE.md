# Project Overview
This is a Kotlin Android app targeting API 26+, using Jetpack Compose for UI.

## Modules
- **app** — main Android application module
- **common-ui** — Android module for shared Compose theme configuration (colors, typography, spacing) and reusable UI components

## Architecture
- MVI with Clean Architecture (data / domain / presentation layers)
- Hilt for dependency injection
- Ktor + OkHttp for networking
- Room for local persistence
- Kotlin Coroutines + StateFlow for async work
- Gradle dependencies are added only through `libs.versions.toml`

## Package Structure
Each feature follows this structure strictly:
```
feature/
└── feature_name/
    ├── presentation/
    │   ├── viewmodel/
    │   └── ui/
    │       ├── screen/
    │       └── component/
    ├── domain/
    │   ├── model/
    │   ├── usecase/
    │   └── repo/
    └── data/
        ├── model/
        └── source/
            ├── local/
            └── remote/
```

- `presentation/viewmodel` — ViewModels, UI state classes (sealed), and UI events
- `presentation/ui/screen` — Top-level Composable screens, one per route
- `presentation/ui/component` — Reusable Composables scoped to this feature only
- `domain/model` — Business/domain models, pure Kotlin data classes
- `domain/usecase` — One class per use case, single `invoke` operator function
- `domain/repo` — Repository interfaces (contracts), defined in domain, implemented in data
- `data/repo` — Repository implementations from the domain
- `data/model` — DTOs, database entities, and mappers to/from domain models
- `data/source/remote` — Remote (API services)
- `data/source/local` — Local (DAOs) data sources

When creating a new feature, always scaffold the full package structure above, even if some packages start empty.

## Code Conventions
- All new UI must use Jetpack Compose, no XML layouts
- Use `sealed class` for UI state representation
- Errors must be handled via a `Result` wrapper, not exceptions
- No hardcoded strings — always use `strings.xml`

## Testing
- ViewModels: unit tested with MockK and Truth
- Aim for 80%+ coverage on domain and presentation layers
- For measuring test coverage, use jacoco
- **Do NOT generate UI tests of any kind** — no Compose UI tests, no Espresso tests, no instrumented tests

## What to Avoid
- No AsyncTask, no RxJava
- No deprecated Android APIs
- Do not modify `build.gradle` without explaining why
- Do not hardcode any dependency in `build.gradle`. Always add through `libs.versions.toml`
- Do not place shared utilities inside feature modules — they belong in `common-ui`
- Do not define theme or design tokens inside feature modules — always import from `common-ui`
- Do not add any comments unless instructed to do so
