# TeleQuest MVP

An Android/Kotlin MVP for TeleQuest, an unofficial Telegram client optimized for Meta Quest 3 / Meta Horizon OS. The first version runs as a flat 2D Android panel and focuses on a Quest-friendly Compose UI, fake repository mode, and a clean TDLib integration boundary.

This project is not affiliated with Telegram. It does not use Telegram branding or the official Telegram logo.

## Current Status

- Android app using Kotlin, Jetpack Compose, Material 3, Coroutines, StateFlow, and MVVM-style state ownership.
- `fake` flavor is the default development path and does not require TDLib binaries.
- Fake flow supports entering a phone number, entering a fake code, viewing chats, opening a chat, reading messages, sending text messages, and back navigation.
- The current UI has been run successfully in an Android emulator and on Meta Quest 3 through Android Studio.
- `tdlib` flavor contains scaffolding only. It is intentionally isolated until TDLib native binaries and Java bindings are added.

## Setup

1. Install Android Studio with Android SDK 34.
2. Open this folder in Android Studio:

   ```bash
   quest-telegram-client
   ```

3. Copy the sample local config when you are ready to test TDLib developer builds:

   ```bash
   cp local.properties.example local.properties
   ```

4. Set your local SDK path and developer Telegram API values in `local.properties`.

   ```properties
   sdk.dir=/Users/you/Library/Android/sdk
   TELEGRAM_API_ID=123456
   TELEGRAM_API_HASH=replace_me
   ```

Regular users should never enter their own `api_id` or `api_hash`. Developer credentials are read at build time from non-committed local config or environment variables.

## Run Fake Mode

Build and install the fake flavor from Terminal:

```bash
./gradlew :app:assembleFakeDebug
```

In Android Studio:

1. Open the `quest-telegram-client` folder itself, not the parent workspace.
2. Wait for Gradle sync to finish.
3. Open View > Tool Windows > Build Variants.
4. Set the `app` module variant to `fakeDebug`.
5. Press Run for an emulator or connected Quest 3.

Fake mode is safe for UI iteration and does not connect to Telegram.

## Fake Mode Vs TDLib Mode

- `fakeDebug` and `fakeRelease` use `FakeTelegramRepository` and must compile at all times.
- `tdlibDebug` and `tdlibRelease` are reserved for real Telegram connectivity work through TDLib.
- TDLib JARs belong in `app/libs/tdlib-jars/`.
- TDLib native libraries belong under `app/src/tdlib/jniLibs/<abi>/`.
- Do not add TDLib dependencies to the fake flavor.

## Build A Debug APK

```bash
./gradlew :app:assembleFakeDebug
```

The APK will be generated under:

```text
app/build/outputs/apk/fake/debug/
```

## Sideload To Meta Quest 3

1. Enable developer mode for the headset.
2. Connect the headset with USB.
3. Confirm the ADB prompt in-headset.
4. Install the fake debug APK:

   ```bash
   adb install -r app/build/outputs/apk/fake/debug/app-fake-debug.apk
   ```

The app is designed as a landscape 2D Android panel with large touch targets, readable type, generous spacing, and high-contrast surfaces for controller or hand input.

## Install To Quest From Android Studio

1. Enable Developer Mode for the Quest in the Meta Horizon mobile app.
2. Connect the headset over USB-C.
3. Accept the USB debugging prompt inside the headset.
4. In Android Studio, select the Quest device and run the `fakeDebug` variant.
5. Launch TeleQuest from App Library > Unknown Sources if it does not open automatically.

Use `docs/quest-validation.md` for controller, hand tracking, keyboard, and comfort checks.

## TDLib Setup Later

The `tdlib` flavor currently compiles against a placeholder wrapper and repository. To connect real Telegram login later:

- Add TDLib Java bindings to `app/libs/tdlib-jars/`.
- Add TDLib native binaries to `app/src/tdlib/jniLibs/<abi>/`.
- Implement `TdLibClient.initialize()` to create the TDLib client.
- Pass TDLib parameters, including app-private database and file paths.
- Read `api_id` and `api_hash` only from `local.properties` or environment variables at build time.
- Map TDLib authorization states:
  - `authorizationStateWaitTdlibParameters`
  - `authorizationStateWaitPhoneNumber`
  - `authorizationStateWaitCode`
  - `authorizationStateWaitPassword`
  - `authorizationStateReady`
  - `authorizationStateLoggingOut`
  - `authorizationStateClosed`
- Listen for chat and message updates and map them into `ChatSummary` and `MessageItem`.
- Handle TDLib errors without logging secrets or user content.

## Pre-Commit Check

Run this before committing app changes:

```bash
./scripts/pre-commit-check.sh
```

The script runs:

```bash
./gradlew :app:testFakeDebugUnitTest :app:assembleFakeDebug
```

Instrumentation tests are intentionally separate because they require an emulator or device:

```bash
./gradlew :app:connectedFakeDebugAndroidTest
```

## Privacy And Security Notes

- Never commit `api_id`, `api_hash`, phone numbers, auth codes, session files, TDLib databases, downloaded files, or user data.
- Never log auth codes, phone numbers, messages, `api_hash`, TDLib session details, or raw TDLib payloads.
- The MVP avoids analytics.
- Future account metadata should use encrypted storage.
- Logout should close TDLib and clear local session/database files where possible.
- The app should communicate directly with Telegram through TDLib and should not proxy Telegram messages through any third-party backend.

## Known Limitations

- Real Telegram login is not implemented yet.
- The TDLib flavor is a scaffold and does not include TDLib binaries.
- Fake timestamps are static.
- Controller, hand tracking, and headset keyboard validation should be recorded for each UI iteration.

## Troubleshooting

- **`fakeDebug` is missing:** open `quest-telegram-client` directly, wait for Gradle sync, then check View > Tool Windows > Build Variants.
- **AndroidX warning:** confirm `gradle.properties` contains `android.useAndroidX=true`.
- **SDK location not found:** create `local.properties` from `local.properties.example` and set `sdk.dir=/Users/<you>/Library/Android/sdk`.
- **Quest appears unauthorized:** put on the headset and accept the USB debugging prompt.
- **App not visible on Quest:** open App Library and switch the filter to Unknown Sources.
- **TDLib build cannot find binaries:** keep using `fakeDebug` until JARs and native libraries are placed in the documented tdlib-only locations.

## Meta Horizon Store Readiness Checklist

- Confirm unofficial-client wording and avoid all Telegram branding.
- Verify data handling, privacy policy, and account deletion/logout behavior.
- Test controller, hand, and keyboard input on Quest 3.
- Validate landscape/wide layouts in headset.
- Complete accessibility review for readable text and focus targets.
- Replace placeholder launcher artwork with original brand assets.
- Add crash/error reporting only after privacy review.
- Run release builds, signing, and store compliance checks.
