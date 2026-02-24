# User Base

An Android sample application demonstrating a clean, scalable architecture using MVI + Clean Architecture, Jetpack Compose, and modern Android libraries.

---

## Tech Stack

| Category | Library |
|---|---|
| UI | Jetpack Compose, Material3 |
| Architecture | MVI + Clean Architecture |
| DI | Hilt 2.52 |
| Navigation | Navigation3 1.0.1 |
| Networking | Ktor 3.0.3, OkHttp 4.12.0 |
| Local DB | Room 2.6.1 |
| Async | Kotlin Coroutines 1.9.0, StateFlow |
| Testing | MockK 1.13.13, Google Truth 1.4.4 |
| Coverage | JaCoCo 0.8.12 |

- **Min SDK:** 26 · **Target/Compile SDK:** 36
- **Kotlin:** 2.0.21 · **AGP:** 8.13.2

---

## Modules

```
user-base/
├── app/          # Main application module
└── common-ui/    # Shared Compose theme and utilities
```

### `:app`
The main application module. Contains all feature packages, DI setup, navigation, Room database, and the application/activity entry points.

### `:common-ui`
Android library module providing:
- **Theme** — `UserBaseTheme` (Material3), color tokens, typography, and spacing scale (`xs` 4dp → `xxl` 48dp)
- **Utilities** — `StringResource` sealed class and `StringUnwrapper` for resolving string resources outside Composables (e.g. in ViewModels)

---

## Architecture

The project follows **MVI + Clean Architecture** with three layers per feature: `data`, `domain`, and `presentation`.

```
feature/feature_name/
├── presentation/
│   ├── viewmodel/
│   │   ├── state/          # UI state classes, InputFieldState
│   │   ├── event/          # UI events (inputs) and one-shot effects
│   │   └── stateholder/    # StateHolder interface + StateHolderEvent
│   │       └── event/
│   └── ui/
│       ├── screen/         # Top-level Composable screens
│       ├── component/      # Feature-scoped Composables
│       └── stateholder/    # StateHolderImpl + Hilt binding module
├── domain/
│   ├── model/              # Pure Kotlin domain models
│   ├── usecase/            # One class per use case (invoke operator)
│   └── repo/               # Repository interfaces (contracts)
└── data/
    ├── model/              # DTOs, Room entities, mappers
    ├── repo/               # Repository implementations
    │   └── source/         # Data source interfaces
    └── source/
        ├── local/          # Local data source implementations
        │   └── room/       # Room DAO and @Database
        └── remote/         # Remote data source implementations
```

### StateHolder Pattern

For screens with form input, a `StateHolder` mediates between the UI and the ViewModel. The screen is purely declarative — it reads state and invokes lambdas, containing zero logic.

**Event flow:**

```
UI (calls lambda) → StateHolderImpl (emits StateHolderEvent)
  → ViewModel (validates, resolves error string)
    → StateHolder.onXxxChanged(value, error)
      → StateFlow update → UI recomposes
```

Key components:

| Component | Location | Responsibility |
|---|---|---|
| `InputFieldState` | `viewmodel/state/` | Holds field value, touched state, error, and interaction lambdas |
| Form state | `viewmodel/state/` | Aggregates all `InputFieldState`s, `isFormValid`, `isSubmitting`, `onSubmit` |
| `<Feature>StateHolderEvent` | `viewmodel/stateholder/event/` | Sealed interface for field-change and submit events |
| `<Feature>StateHolder` | `viewmodel/stateholder/` | Interface exposing `events: Flow`, `state: StateFlow<Form>`, and field-update methods |
| `<Feature>StateHolderImpl` | `ui/stateholder/` | Wires lambdas at construction; emits events via `Channel`; mutates `_state` |
| ViewModel | `viewmodel/` | Observes events, validates, calls `stateHolder.onXxxChanged`, emits effects |
| Screen | `ui/screen/` | Reads state, passes lambdas to child components |

---

## Features

### Add User
An input form for registering a new user. Validates all fields on change and on focus loss before allowing submission.

**Fields:** Name · Age · Job Title · Gender (dropdown)

**Validation rules:**
- Name must be longer than a minimum length and contain no digits or special characters
- Age must be a valid number within a defined range
- Job title is required and has a maximum length
- Gender must be selected

On successful submission, the user is persisted to the local Room database and the app navigates to the user list.

### List Users
Displays all persisted users in a scrollable list. Each item shows the user's name, age, job title, and gender. Handles empty and error states with a retry option.

---

## Navigation

Navigation uses **Navigation3** (`androidx.navigation3`). Routes are defined as `@Serializable data object`s implementing `NavKey`, declared in `MainActivity`.

```
AddUserRoute  ──(add success)──►  UserListRoute
              ◄──(back)──────────
```

---

## Dependency Injection

Hilt manages all dependencies. Scoping:

| Scope | Usage |
|---|---|
| `SingletonComponent` | `UserDatabase`, `UserDao`, `StringUnwrapper` |
| `ViewModelComponent` (`@ViewModelScoped`) | Repository, data source, and `StateHolder` bindings per feature |

Each feature has its own `@Module` (e.g. `AddUserModule`) binding its interfaces to implementations.

---

## Testing

Unit tests cover the `domain` and `presentation` layers with a target of **80%+ coverage**.

**Tools:** MockK · Google Truth · `kotlinx-coroutines-test`

**Coverage report (JaCoCo):**
```bash
./gradlew jacocoTestReport
```
HTML report: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

**Run unit tests:**
```bash
./gradlew testDebugUnitTest
```

> UI tests (Compose/Espresso/instrumented) are intentionally excluded from this project.

---

## Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

All dependencies are declared in `gradle/libs.versions.toml`. No versions are hardcoded in `build.gradle.kts` files.