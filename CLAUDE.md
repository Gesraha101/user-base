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
    │   │   ├── state/
    │   │   ├── event/
    │   │   └── stateholder/
    │   │       └── event/
    │   └── ui/
    │       ├── screen/
    │       ├── component/
    │       └── stateholder/
    ├── domain/
    │   ├── model/
    │   ├── usecase/
    │   └── repo/
    └── data/
        ├── model/
        ├── repo/
        │   └── source/
        └── source/
            ├── local/
            │   └── room/
            └── remote/
```

- `presentation/viewmodel` — ViewModels only
- `presentation/viewmodel/state` — UI state classes (sealed) and `InputFieldState<E>` wrapper
- `presentation/viewmodel/event` — UI events (inputs) and UI effects (one-shot outputs)
- `presentation/viewmodel/stateholder` — `StateHolder` interface the ViewModel depends on
- `presentation/viewmodel/stateholder/event` — `StateHolderEvent` sealed interface (field change + submit variants)
- `presentation/ui/screen` — Top-level Composable screens, one per route
- `presentation/ui/component` — Reusable Composables scoped to this feature only
- `presentation/ui/stateholder` — `StateHolder` implementation and its `@ViewModelScoped` Hilt binding module
- `domain/model` — Business/domain models, pure Kotlin data classes
- `domain/usecase` — One class per use case, single `invoke` operator function
- `domain/repo` — Repository interfaces (contracts), defined in domain, implemented in data
- `data/repo` — Repository implementations (implement interfaces from `domain/repo`)
- `data/repo/source` — Data source interfaces (contracts used by repositories; parallel to `domain/repo` for source abstraction)
- `data/model` — DTOs, database entities, and mappers to/from domain models
- `data/source/local` — Local data source implementations (implement interfaces from `data/repo/source`)
- `data/source/local/room` — Room DAO interfaces and `@Database` class
- `data/source/remote` — Remote (API) data source implementations

When creating a new feature, always scaffold the full package structure above, even if some packages start empty.

## StateHolder Pattern
For screens with form input, always create a `StateHolder`. Events flow UI → StateHolder → ViewModel → StateHolder (state update). The screen is fully dumb — it only reads state and calls lambdas stored in it.

1. **`InputFieldState`** in `viewmodel/state/` — holds `value: String`, `isTouched: Boolean`, `error: String?`, computed `displayError`, and interaction lambdas `onChanged: (String) -> Unit` and `onFocusLost: (String) -> Unit`. UI calls these lambdas directly.
2. **`Form` state** in `viewmodel/state/` — holds one `InputFieldState` per field plus `genderOptions`, `isFormValid`, `isSubmitting`, and `onSubmit: () -> Unit`. Screen reads it all; no logic in the screen.
3. **`<Feature>StateHolderEvent`** sealed interface in `viewmodel/stateholder/event/` — one variant per field interaction and submit.
4. **`<Feature>StateHolder`** interface in `viewmodel/stateholder/` — exposes `events: Flow<StateHolderEvent>`, `state: StateFlow<Form>`, per-field update methods `onXxxChanged(value, error)`, `setSubmitting`, `currentUser()/currentEntity()`, and `reset()`.
5. **`<Feature>StateHolderImpl`** in `ui/stateholder/` — wires `InputFieldState` lambdas at construction to emit events via `Channel<StateHolderEvent>`; per-field update methods mutate `_state`; uses `StringUnwrapper` for string resource resolution.
6. **ViewModel** — observes `stateHolder.events` in `init { viewModelScope.launch { observeStateHolderEvents() } }`; for each field event: validates, resolves error string via `StringUnwrapper`, calls `stateHolder.onXxxChanged(value, error)`; for Submit: calls `stateHolder.currentUser()`, invokes use case, emits effects.
7. **Screen** — passes `form.field.onChanged` / `form.field.onFocusLost` directly to components; passes `form.onSubmit` to the submit button. No event dispatch or logic of any kind.

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
