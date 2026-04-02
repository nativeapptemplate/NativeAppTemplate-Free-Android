# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

NativeAppTemplate-Free-Android is a native Android app (100% Kotlin, 100% Jetpack Compose) for managing walk-in customer waitlists using NFC tags and QR codes. It connects to a Rails backend API at `api.nativeapptemplate.com`. Inspired by [nowinandroid](https://github.com/android/nowinandroid).

## Build & Run Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run all unit tests
./gradlew test

# Run a single test class
./gradlew testDebugUnitTest --tests "com.nativeapptemplate.nativeapptemplatefree.ui.shops.ShopListViewModelTest"

# Run a single test method
./gradlew testDebugUnitTest --tests "com.nativeapptemplate.nativeapptemplatefree.ui.shops.ShopListViewModelTest.stateIsInitiallyLoading"

# Dependency analysis
./gradlew buildHealth
```

## Modules

- **`:app`** — Main application module. Contains all UI, data, DI, networking, and navigation code.
- **`:model`** — Shared data model classes (serializable with kotlinx.serialization). No Android framework dependencies beyond the library plugin.

## Architecture

MVVM layered architecture following [Android Modern App Architecture](https://developer.android.com/topic/architecture#modern-app-architecture):

- **UI Layer** (`ui/`): Jetpack Compose screens + Hilt-injected ViewModels. Each feature has its own `navigation/` subpackage defining routes and `NavGraphBuilder` extensions.
- **Data Layer** (`data/`): Repository interfaces + `*Impl` classes backed by Retrofit API interfaces. Each domain (shop, item_tag, login) has its own `*Api.kt`, `*Repository.kt`, and `*RepositoryImpl.kt`.
- **Network Layer** (`network/`): `AuthInterceptor` for token injection, `RequestHelper` for request construction.
- **DI** (`di/modules/`): Hilt modules — `NetModule` (Retrofit/OkHttp), `DataModule` (repository bindings), `DataStoreModule`, `DispatchersModule`, `CoroutineScopesModule`.
- **DataStore** (`datastore/`): Proto DataStore for user preferences. Proto definitions live in `app/src/main/proto/`.
- **Navigation**: `NatNavHost.kt` is the top-level nav graph. Three bottom-nav sections: Shops, Scan, Settings. Each section uses nested navigation graphs via `*BaseRoute`.

## Key Patterns

- **API response handling**: Uses [Sandwich](https://github.com/skydoves/sandwich) library (`ApiResponse` type) for Retrofit call adaptation.
- **Dependency injection**: Hilt with `@InstallIn(SingletonComponent::class)`. Repositories are bound via `@Binds` in `DataModule`.
- **Navigation routes**: Defined as extension functions on `NavGraphBuilder` (e.g., `shopListView()`, `navigateToShopDetail()`). Routes use type-safe navigation.
- **Proto DataStore**: User preferences and NFC scan state are persisted via Protocol Buffers (lite).

## Error Handling (CodedError System)
All errors should use the `CodedError` interface. Error codes use the `NATA-XXXX` prefix (NativeAppTemplate Android).

| Range | Type | Description |
|-------|------|-------------|
| NATA-1xxx | App/general errors | Unexpected errors, catch-all |
| NATA-2xxx | API/network errors | HTTP request failures, parsing errors |
| NATA-3xxx | NFC/scan errors | NFC tag read/write/scan failures |

- New error types must implement `CodedError`
- Use `codedDescription` (not `message` or `localizedMessage`) in all user-facing error messages — this prepends `[NATA-XXXX]` for `CodedError` types

## Testing

- Tests use JUnit 4 with `kotlinx.coroutines.test` and Robolectric.
- `MainDispatcherRule` (in `testing/util/`) replaces the main dispatcher for coroutine tests.
- Test doubles: `Test*Repository` classes (in `testing/repository/`) implement repository interfaces for ViewModel testing.
- Demo data: `demo/` package contains `DemoAssetManager` and `Demo*Repository` classes that load JSON fixtures from `app/src/test/assets/`.
- JVM toolchain: Java 17.

## Pre-push Hook

A `scripts/pre-push` script runs Spotless, lint, and tests before each push. Install it with:
```bash
cp scripts/pre-push .git/hooks/pre-push
```

## Connecting to Local API

In `app/build.gradle.kts`, swap the debug `buildConfigField` values:
```kotlin
buildConfigField("String", "DOMAIN","\"192.168.1.21\"")
buildConfigField("String", "PORT","\"3000\"")
buildConfigField("String", "SCHEME","\"http\"")
```
