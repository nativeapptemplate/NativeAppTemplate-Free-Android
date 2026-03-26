# Changelog

All notable changes to this project will be documented in this file.

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
