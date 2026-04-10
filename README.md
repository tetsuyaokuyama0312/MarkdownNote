# MarkdownNote

[日本語版 README はこちら](README_ja.md)

A Markdown-based note-taking app for Android, built with Jetpack Compose and Material Design 3.

## Features

- **Create / Edit / Delete memos** — manage notes with a clean, modern interface
- **Markdown editor** — write in Markdown with full syntax support via CommonMark (GFM extensions: strikethrough, tables)
- **Live preview** — toggle between Edit, Split (side-by-side editor + preview), and View modes
- **Search** — instantly filter memos by content
- **Swipe to delete** — swipe a memo left to trigger a deletion confirmation
- **File export** — export any memo as Plain Text (.txt), Markdown (.md), or HTML (.html) to the device's Documents folder
- **Unsaved-changes guard** — prompts to save or discard before navigating away from an edited memo
- **Offline-first** — all data is stored locally in a Room (SQLite) database

## Requirements

| Item | Value |
|------|-------|
| Minimum SDK | API 24 (Android 7.0) |
| Target SDK | API 35 |
| JDK | 21 |

## Tech Stack

| Category | Library / Tool |
|----------|----------------|
| Language | Kotlin 2.3.0 |
| UI | Jetpack Compose (BOM 2025.12.01), Material Design 3 |
| Navigation | Jetpack Navigation Compose 2.9.6 — type-safe `@Serializable` routes |
| DI | Hilt 2.57.2 |
| Database | Room 2.8.4 |
| Markdown | CommonMark 0.24.0 + GFM extensions (strikethrough, tables) |
| Serialization | Kotlinx Serialization 2.3.0 |
| Logging | Timber 5.0.1 |
| Build system | AGP 8.13.2, KSP 2.2.21-2.0.4 |

## Architecture

The project follows **Clean Architecture** with the **MVVM** pattern, organised into multiple Gradle modules.

```
Presentation  (feature:memo)
      │  Compose screens, ViewModels, UiState
      ▼
Domain        (domain)
      │  Use cases, repository interfaces, domain models
      ▼
Data          (data)
      │  Room DB, DAOs, repository implementations, Hilt modules
      ▼
Core          (core:ui, core:common)
         Shared Compose components, app theme, Markdown parser, file I/O
```

Data flows reactively through Kotlin `Flow` / `StateFlow`. Each ViewModel exposes a single immutable `UiState` object that drives Compose recomposition.

## Module Structure

```
MarkdownNote/
├── app/                    # Application entry point, Hilt setup, Navigation host
├── feature/
│   └── memo/               # MemoListScreen, MemoEditorScreen, ViewModels, UiState
├── domain/                 # Memo model, use cases (Get / Search / Save / Delete), repository interfaces
├── data/                   # Room database, DAOs, MemoRepositoryImpl, Hilt DI modules
├── core/
│   ├── ui/                 # Shared Compose components (dialogs, MarkdownHtmlView), app theme
│   └── common/             # Markdown parser (CommonMark), FileAccessor, utility functions
└── gradle/
    └── libs.versions.toml  # Version catalog — single source of truth for all dependencies
```

### Module dependency graph

```
         ┌─────────────────────────────────────┐
         │                app                  │
         └──┬──────────┬──────────┬────────────┘
            │          │          │
       feature:memo   data      domain
            │    \     │    \      │
          core:ui  domain  domain  core:common
                            │
                        core:common
```

## Build & Run

```bash
# 1. Clone the repository
git clone https://github.com/tetsuyaokuyama0312/MarkdownNote.git
cd MarkdownNote

# 2. Open in Android Studio (Meerkat or later recommended),
#    or build from the command line:
./gradlew assembleDebug

# 3. Install on a connected device / emulator
./gradlew installDebug
```

### Run tests

```bash
# Unit tests
./gradlew test

# Instrumentation tests (requires a connected device or emulator)
./gradlew connectedAndroidTest
```

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| Multi-module | Enforces layer boundaries; modules can be built and tested in isolation |
| Type-safe navigation | Eliminates stringly-typed route bugs at compile time |
| `@Serializable` route objects | Seamless integration between Navigation Compose and Kotlin Serialization |
| Use-case `operator fun invoke()` | Single-responsibility, easily mockable in tests |
| `FileAccessor` in `core:common` | Keeps file I/O logic out of ViewModels; provided by Hilt from the `data` module |
| CommonMark + GFM extensions | Spec-compliant Markdown rendering with table and strikethrough support |

## License

This project is for personal / educational use. No license is currently specified.
