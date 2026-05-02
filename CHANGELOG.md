# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [3.2.0] - 2026-05-02

### Added
- Generic CRUD UI for Item Tags (replaces NFC/QR scan flow)
- Client-side length caps and truncation for Shop name and description
- Date + time on `completedAt` timestamps via `cardDateTimeString`
- Tests for Onboarding model, MainActivityViewModel, and AuthInterceptor
- `canCreateItemTags` / `canDeleteItemTags` permission tests on `ItemTagListViewModel`
- Configurable API endpoint via Gradle properties in Debug builds

### Changed
- Renamed "Number Tag" to "Item Tag" throughout the app
- Updated ItemTag schema to Rails API v2
- Collapsed 7-tier role hierarchy to `admin` / `member`
- Slimmed onboarding from 6 to 4 slides and replaced hero imagery
- ItemTag list card now uses a two-column row layout with vertically centered content
- Swipe actions reveal from the trailing (right) edge; renamed `reset` → `idle`
- Card date format changed to `yyyy/MM/dd`
- Renamed `validateEmail` → `isValidEmail`; converted `isNumeric` to a `String?` extension
- Normalized substrate naming to a single `NativeAppTemplate` stem (Kotlin symbols, theme styles, Gradle properties, error-code prefix)

### Removed
- NFC, QR, and Scan features (background tag reading, scan view, camera permission)
- "How To Use" entry from Settings
- Server-driven `maximum_name_length` (moved to client-side `NativeAppTemplateConstants`)
- Success toast for swipe complete/idle actions (reload is the success signal)
- Unused Teal10/Teal30/Teal90 color tokens
- Unused FileProvider declaration and `filepaths` resource
- NATIVEAPPTEMPLATE-3xxx NFC/scan error code range
- Dead code across `utils` and `designsystem`

### Fixed
- ItemTag whitespace-only name now fails validation
- Long shop names no longer overflow the right column in `ShopDetailCardView`
- Shop description field height regressed after switching to two-line `supportingText`

## [3.0.0] - 2026-04-05

### Added
- Implemented pagination for item tags list screen.
- Implemented CodedError system with NATIVEAPPTEMPLATE-XXXX error codes.
- Added unit tests for utils, network, and pre-push hook.
- Added Spotless + ktlint for Kotlin code formatting.
- Added app version and reorganized settings sections.

### Changed
- Updated compileSdk and targetSdk to 36.
- Updated libraries and dependencies.
- Extracted SnackbarMessageEffect composable.
- Replaced generic Exception with sealed ApiException hierarchy.
- Extracted duplicated API error handling into shared extensions.

### Fixed
- Fixed null safety bugs and resource leak.
- Fixed security vulnerabilities from audit.

## [2.0.1] - 2025-06-24

### Changed
- Updated libraries and dependencies.

## [2.0.0] - 2025-03-10

### Added
- NFC features for Number Tags (ItemTags): Write Application Info to a Tag, Read a Tag, Background Tag Reading.
- Generate QR Code Image for Number Tags (ItemTags) with a Centered Number.
- Onboarding screen with background tag reading support when not signed in.

### Changed
- Updated navigation to use the Now in Android approach with type-safe navigation.
- Updated onboarding flow.

### Fixed
- Fixed login flow.
- Fixed bottom navigation tab color not changing.
- Fixed test failures.

## [1.0.0] - 2024-12-07

### Added
- Initial release.
- 100% Kotlin and 100% Jetpack Compose.
- User authentication: Sign Up, Sign In, Sign Out, Email Confirmation, Forgot Password.
- Input validation.
- CRUD operations for Shops.
- CRUD operations for Number Tags (ItemTags).
- Hilt dependency injection.
- Retrofit2 networking with Sandwich API response handling.
- Proto DataStore for user preferences.
- Dark mode support.
- Repository tests with Robolectric.
