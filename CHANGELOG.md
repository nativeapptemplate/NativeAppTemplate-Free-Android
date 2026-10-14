# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added
- Every API request sends `Accept-Language` built from the device's preferred languages (e.g. `ja-JP, en-US;q=0.9`), so the API's English/Japanese responses before sign-in follow the device; a header already on the request is kept
- Sign-up sends the app language (`en`/`ja`, `AppLanguage`) as `locale`, and the signed-in shopkeeper's `locale` from the sign-in/sign-up/profile-update responses is stored with the session. Sessions saved by earlier versions load with `en`
- The app language is the source of truth for `shopkeepers.locale`: on launch, on every return to the foreground and right after sign-in, a stored `locale` that differs from it is sent alone in `PATCH /shopkeeper_auth` (`AppLanguageSyncer`); failures are logged and retried next time
- Per-app language on Android 13+ (`android:localeConfig`, English and 日本語)
- "App Language" section on the Edit Profile screen: shows the current app language and opens the system screen to change it (the app's language list on Android 13+, the device languages before); emails follow it
- Japanese UI (`values-ja/strings.xml`), shown when the app language is Japanese, ported from the paid edition so shared strings read the same. Terms follow the API's `ja.yml` (店舗, アイテムタグ, 未完了 / 完了). Server error messages, error codes and user-entered text are not translated by the app
- `StringResourcesTranslationTest` fails when a translatable string or plural is missing from either language, its format arguments differ, or a Japanese string says ショップ / 店, shortens アイテムタグ, or mentions 組織 (the Free edition has no organizations); `JapaneseResourcesPackagedTest` checks the Japanese resources are packaged

### Changed
- Text that was hardcoded in Kotlin now comes from strings.xml: the minimum password length hint (a plural) and the alert dialog's default "OK"
- The agreement and "Please accept updated ..." sentences are single format strings with the links as arguments (`linkedFormat()`), so a translation can put the links where its grammar needs them
- `app_name` is `translatable="false"`
- `HardcodedUiStringsTest` scans each file as a whole, so it also catches a literal on the line after `Text(`, and `updateMessage("...")` / `?: "..."` fallbacks
- Move agent instructions from `CLAUDE.md` to `AGENTS.md` so Claude Code, Codex, and other agents share one file. `CLAUDE.md` now only imports it (`@AGENTS.md`)

### Fixed
- Error messages are in the app language and say what the user can do, with the same wording as NativeAppTemplate-Free-iOS; the error codes are unchanged. A request that got no answer (no network, a timeout) or an error status without a message (e.g. a 502 page) shows `[NATIVEAPPTEMPLATE-2002]` "Something went wrong while communicating with the server. Please try again later." (サーバーとの通信中に問題が発生しました。しばらくしてからもう一度お試しください。) instead of the English system text (e.g. "Processing error: Unable to resolve host ..." or "Processing error: HTTP 502"). A response the app could not process (`2002`) and `1001` show "An unexpected error occurred." (予期しないエラーが発生しました。). The server's own messages are shown as is, without the developer suffix " [Status: 422]" (`2001`) or the "Processing error: " prefix (sign-in's "Invalid login credentials"). The details are logged with the code instead (`adb logcat -s ApiError`)

## [3.2.6] - 2026-05-24

### Fixed
- Clearing an item tag's description on edit was silently ignored — the Retrofit JSON config keeps `encodeDefaults = false`, so `ItemTagBodyDetail.description = ""` (equal to its declared default) was dropped from the request body and the server's partial update left the old description in place. Removed the default so the empty string is always sent

## [3.2.5] - 2026-05-08

### Changed
- Dropped articles before renameable domain nouns in UI strings ("Tap shop below", "Swipe to change item tag status.", "Add a new item tag and start changing item tag status.") and seed-data fixtures so the cross-platform rename pipeline produces grammatical output for any target word — mirrors [iOS PR #77](https://github.com/nativeapptemplate/NativeAppTemplate-iOS/pull/77)

## [3.2.4] - 2026-05-04

### Fixed
- Welcome screen sparkle was invisible (white-on-white) in light mode — pinned the screen to brand navy `#1A2332` (matching the launcher and splash) regardless of theme so the white sparkle and title always have contrast
- Sign-up / sign-in sparkle was hard-coded to white and washed out on light backgrounds — switched to `MaterialTheme.colorScheme.primary` so it stays visible in both light and dark modes

## [3.2.3] - 2026-05-04

### Fixed
- Welcome screen sparkle and title were drifting apart on tall phones — restructured to a `Column` with weighted spacers and a fixed 220dp sparkle so the icon and title sit together in the upper-middle

### Changed
- Enlarged launcher sparkle from 0.61 to 0.75 scale so the foreground reads at launcher size
- Dropped the embedded `#1A2332` background rect from the launcher foreground/monochrome vectors — the adaptive-icon `<background>` layer supplies the navy, and the monochrome layer must not include a fill rect for Android 13+ themed icons to render correctly

## [3.2.2] - 2026-05-04

### Changed
- Renamed `app_name` to "Native App Template" so the launcher label and in-app title share a single source of truth
- Sign-up / sign-in title dropped to `headlineMedium` bold/centered so it fits on one line
- Refreshed launcher icon with sparkle artwork (per-density webp + vector foreground/monochrome) and added a `<monochrome>` layer for Android 13+ themed icons
- Replaced splash "N" letterform with the same four-pointed sparkle (white on navy via `windowSplashScreenBackground`)

### Removed
- Onboarding sample pages 1–4, the `Onboarding` data class, `ImageOrientation` enum, `OnboardingViewModel`, their tests, and the `ic_overview1-4` / `ic_hero` drawables — onboarding now shows just the welcome screen with an `AutoAwesome` icon

## [3.2.1] - 2026-05-02

### Changed
- Shop list card stat labels now read "completed item tags" / "all item tags" to align with the Number Tag → Item Tag terminology

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
