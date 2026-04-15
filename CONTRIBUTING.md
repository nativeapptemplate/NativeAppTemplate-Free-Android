# Contributing to NativeAppTemplate-Free-Android

Thanks for your interest in contributing! This document explains how to report issues, propose changes, and submit pull requests.

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold it.

## Reporting Bugs

Before opening an issue, please:

1. Search existing [issues](https://github.com/nativeapptemplate/NativeAppTemplate-Free-Android/issues) to avoid duplicates.
2. Confirm the bug reproduces on the `main` branch with the latest Android Studio.
3. Include:
   - Android version and device model (or emulator)
   - Android Studio and Gradle versions
   - Steps to reproduce
   - Expected vs actual behavior
   - Relevant Logcat output or stack traces

## Reporting Security Vulnerabilities

**Do not open public issues for security vulnerabilities.** See [SECURITY.md](SECURITY.md) for the disclosure process.

## Proposing Changes

For non-trivial changes, please open an issue first to discuss the approach before investing implementation time.

## Pull Requests

1. Fork the repository and create a feature branch from `main`.
2. Make your changes with clear, focused commits.
3. Add or update tests for any behavioral changes.
4. Run the test suite locally:
   ```bash
   ./gradlew test
   ```
5. Build the app to confirm no compilation errors:
   ```bash
   ./gradlew build
   ```
6. Push your branch and open a pull request against `main`.
7. In the PR description, explain *what* changed and *why*.

### Style

Follow standard Kotlin style conventions. Keep 100% Kotlin and 100% Jetpack Compose.

Architecture: Simple MVVM Layered Architecture with Hilt for dependency injection and Retrofit2 for networking. Follow existing patterns for new screens and view models.

### Tests

- Tests use the standard Android test frameworks (JUnit, etc.).
- Place new tests alongside existing ones mirroring the source structure.

## Development Setup

See [README.md](README.md) for full setup instructions.

## License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).
