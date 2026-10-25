# NativeAppTemplate-Free-Android

NativeAppTemplate-Free-Android is a modern, comprehensive, and production-ready native Android app with user authentication and a full CRUD example.  
It is a complete, working app on its own, not a demo or a trimmed-down preview: build on it and ship it as is, under the MIT License.  
The paid editions, [NativeAppTemplate-Android (Solo)](https://nativeapptemplate.com/products/android-solo) and [NativeAppTemplate-Android (Team)](https://nativeapptemplate.com/products/android-team), are for apps in which several people work in a shared organization; they add the features listed in [Paid Edition Extras](#paid-edition-extras).  

The iOS version is available here: [NativeAppTemplate-Free-iOS](https://github.com/nativeapptemplate/NativeAppTemplate-Free-iOS).  

Want this template adapted to your own domain? [nativeapptemplate-agent](https://github.com/nativeapptemplate/nativeapptemplate-agent) is a Claude Code agent that turns a one-sentence spec (e.g. *"a walk-in queue for a barbershop"*) into a coherent three-platform app — this Jetpack Compose Android app, a matching [Rails 8.1 API](https://github.com/nativeapptemplate/nativeapptemplateapi), and a [SwiftUI iOS app](https://github.com/nativeapptemplate/NativeAppTemplate-Free-iOS) — renamed and adapted for you, with validation built in.

## Overview

NativeAppTemplate-Free-Android connects to a Rails 8.1 API backend that **you run yourself**. The backend is open source (MIT) — run it locally on your Wi-Fi or deploy your own, then point the app at it (see [Running with the NativeAppTemplate-API on localhost](#running-with-the-nativeapptemplate-api-on-localhost)).

> **Note:** There is no shared hosted API: run your own API server and point the app at it. The built-in default, `https://api.nativeapptemplate.com`, stands for your own production API's host.

- [nativeapptemplateapi](https://github.com/nativeapptemplate/nativeapptemplateapi) &middot; [API Docs](https://nativeapptemplate.com/api-docs/index.html)

### Screenshots

![Screenshot showing Sign in screen, Shops screen and Settings screen](https://github.com/nativeapptemplate/NativeAppTemplate-Free-Android/blob/main/docs/images/screenshots.png "Screenshot showing Sign in screen, Shops screen and Settings screen")

### Features

NativeAppTemplate-Free-Android uses modern Android development tools and practices, including:

- **100% Kotlin**
- **100% Jetpack Compose**
- **Hilt** (Dependency Injection)
- **Retrofit2** (Networking)
- **[Proto DataStore](https://developer.android.com/topic/libraries/architecture/datastore)**  
- **[Android Modern App Architecture](https://developer.android.com/topic/architecture#modern-app-architecture)**
- **[Simple MVVM Layered Architecture](https://medium.com/@dadachix/key-differences-in-mvvm-architecture-ios-vs-android-e239d30b2ea7)**
- **Test**  
- Inspired by [nowinandroid](https://github.com/android/nowinandroid) and [emitron-Android](https://github.com/razeware/emitron-Android)

#### Included Features

- Onboarding
- Sign Up / Sign In / Sign Out
- Email Confirmation
- Forgot Password
- CRUD Operations for Shops (Create/Read/Update/Delete)
- CRUD Operations for Shops' Nested Resource, Item Tags (Create/Read/Update/Delete)
- Force App Version Update
- Force Privacy Policy Version Update
- Force Terms of Use Version Update
- English and Japanese Localization (string resources)
- And more!

## NFC Tag Operations

NFC tag writing and background tag reading were part of v1 and have been removed from the current version. The full NFC implementation remains available in the [`v1-with-nfc`](https://github.com/nativeapptemplate/NativeAppTemplate-Free-Android/tree/v1-with-nfc) branch.

## Paid Edition Extras

The free version is complete for apps used by individuals. The paid editions ([NativeAppTemplate-Android (Solo)](https://nativeapptemplate.com/products/android-solo) and [NativeAppTemplate-Android (Team)](https://nativeapptemplate.com/products/android-team)) add what apps for teams need:

- URL Path-Based Multitenancy (prepends `/:account_id/` to URLs)
- User Invitation to Organizations
- Role-Based Permissions and Access Control
- Organization Switching UI
- Push Notifications via FCM
- In-App Subscriptions with RevenueCat, off until you add your RevenueCat key
- Error Monitoring with Sentry, off until you add your Sentry DSN

## Getting Started

To get started, clone this repository:

```bash
git clone https://github.com/nativeapptemplate/NativeAppTemplate-Free-Android.git
```

## Requirements

To run this app successfully, ensure you have:

- An Android device or emulator with API level 26 or higher.

## Running with the NativeAppTemplate-API on localhost

The debug build's built-in default is `https://api.nativeapptemplate.com`, your production API's host, so point it at a Rails server running on your Wi-Fi. Add the following to `~/.gradle/gradle.properties` (here `~` is your user home directory — `/Users/<you>` on macOS, `/home/<you>` on Linux, `C:\Users\<you>` on Windows; create the file if it doesn't exist):

```
# Use your current Wi-Fi IP (macOS: `ipconfig getifaddr en0`), or 10.0.2.2 for emulator → host.
# Never use 127.0.0.1, localhost, or 0.0.0.0 — Rails and this app must agree on one reachable address.
NATIVEAPPTEMPLATE_API_DOMAIN=192.168.1.21
NATIVEAPPTEMPLATE_API_PORT=3000
NATIVEAPPTEMPLATE_API_SCHEME=http
```

Then `./gradlew assembleDebug` — or Build → Rebuild Project from Android Studio. The debug `buildConfigField` entries in `app/build.gradle.kts` read these via `project.findProperty(...)`, so the same config works from both the terminal and the IDE. (The three properties override the built-in default, which is no longer served.) For a one-off override: `./gradlew -PNATIVEAPPTEMPLATE_API_DOMAIN=192.168.1.21 -PNATIVEAPPTEMPLATE_API_PORT=3000 -PNATIVEAPPTEMPLATE_API_SCHEME=http assembleDebug`.

Cleartext HTTP to private IPs is already permitted in debug via `app/src/debug/res/xml/network_security_config.xml`; the release config (in `app/src/main/`) keeps `api.nativeapptemplate.com` HTTPS-only — change this host to your own production API domain.

## Blog

- [Key Differences in MVVM Architecture: iOS vs. Android](https://medium.com/@dadachix/key-differences-in-mvvm-architecture-ios-vs-android-e239d30b2ea7)

## Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on reporting issues, proposing changes, and submitting pull requests.

This project adheres to the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## Security

If you discover a security vulnerability, please follow the disclosure process in [SECURITY.md](SECURITY.md). Do not open public issues for security concerns.

## License

This project is licensed under the MIT License — see [LICENSE](LICENSE) for details.
