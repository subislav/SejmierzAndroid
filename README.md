# Sejmierz — Native Android (Kotlin)

## 1. Purpose of the application

Sejmierz is a client for the public Sejm API (`api.sejm.gov.pl`, term `term10`). It presents MPs, votings, and parliamentary clubs, and includes a dedicated telemetry module used for comparative performance measurement across native and multiplatform clients.

This repository is the **native Android** realisation. It mirrors the functional scope and navigation contract of the iOS reference application while employing Android’s established Clean Architecture and Jetpack Compose tooling.

## 2. Architectural style

The project is organised as a multi-module Gradle workspace following **Clean Architecture**:

| Module | Role |
|--------|------|
| `:domain` | Pure JVM models, repository interfaces, use cases, paging, benchmark contracts |
| `:data` | Ktor networking, SQLDelight persistence, repository implementations, settings |
| `:ui` | Jetpack Compose screens, Decompose components (MVI), theme, navigation shell |
| `:app` | Application entry, Koin platform wiring, Android-specific samplers |

`:domain` and `:data` contain no Android UI dependencies. The presentation layer uses **MVI** implemented with Decompose components (not AndroidX ViewModels): each feature exposes a `StateFlow` of immutable state and accepts sealed intents.

## 3. Module and package layout

```
SejmierzAndroid/
├── app/          # MainActivity, Application, DI, Android benchmark samplers
├── domain/       # model, usecase, repository, paging, benchmark, error
├── data/         # remote, local, mapper, repository, settings, di
└── ui/
    ├── navigation/   # RootComponent, RootContent, AppTab
    ├── feature/      # mps, mpdetaill, votings, votingdetail, clubs,
    │                 # clubdetail, benchmark, settings
    └── core/         # theme, StateViews, display/jank tracking
```

Base package: `pl.jsubczynski.sejmierz`.

## 4. Navigation and interaction model

Navigation is built with **Decompose** nested stacks under a persistent bottom bar:

1. Posłowie — nested stack: list → detail  
2. Głosowania — nested stack: list → detail  
3. Kluby — nested stack: list → detail  
4. Telemetria — root benchmark screen  

This structure is the Android substitute for iOS `TabView` + per-tab `NavigationStack`. Settings (language, online/offline) open from a **right-side `ModalNavigationDrawer`** (RTL layout direction trick), available from every tab root via a menu action. Detail screens use a consistent back affordance (`ArrowBack`); tab roots do not show a back control.

## 5. Data and concurrency

- **Networking:** Ktor with OkHttp engine and a network benchmark plugin.
- **Persistence:** SQLDelight (Android SQLite driver).
- **Settings:** in-memory repository exposing `StateFlow` for data-source mode, language, and benchmark configuration.
- **Paging:** Domain `Paginator` (offset/limit), not Jetpack Paging.
- **DI:** Koin modules in domain, data, and app layers.
- **Async:** Kotlin Coroutines and `StateFlow`.

## 6. Telemetry and benchmarking

Metrics are collected through Domain sinks and enriched with run-context tags (including dynamic network type). Android hosts attach JankStats / frame sampling, CPU sampling, display trackers, and cold-start timing from process start. Scroll sessions emit `ui.jankFrames` plus main-thread stall count/total ms; parse and scroll sessions emit `cpu.avg` / `cpu.peak`. The Telemetria tab supports iteration configuration, CSV export to `filesDir/benchmark-exports/`, clearing, and statistical summary cards.

## 7. Principal technologies and dependencies

| Concern | Technology (representative versions) |
|---------|--------------------------------------|
| Language | Kotlin 2.1, Java 17 toolchain |
| UI | Jetpack Compose (BOM 2024.12), Material 3 |
| Navigation | Decompose 3.2, Essenty |
| Networking | Ktor 3.0 (OkHttp) |
| Persistence | SQLDelight 2.0 |
| DI | Koin 4.0 |
| SDK | minSdk 31, compile/targetSdk 35 |

Version catalogue: `gradle/libs.versions.toml`.

## 8. Design patterns employed

- Clean Architecture (Gradle modules as layers)
- MVI with Decompose components
- Repository and use-case patterns
- Nested navigation stacks per tab
- Dependency injection (Koin)
- Explicit online/offline data-source strategy
- Composition-local injection of benchmark context