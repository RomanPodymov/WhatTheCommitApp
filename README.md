# WhatTheCommitApp

A single-screen Android app written in Kotlin with Jetpack Compose and Material 3.
It fetches a plain-text commit message from https://whatthecommit.com/index.txt on
launch. Tap **New commit** to fetch another message. Failed requests show a Retry
button. Messages can be selected and copied.

`CommitViewModel` retains the message across configuration changes and performs
HTTP requests on the IO dispatcher with connection/read timeouts. The UI supports
system light/dark mode, edge-to-edge insets, and Compose previews.

## Build and check

Use JDK 17 or newer and Android SDK 35 (configure `sdk.dir` in `local.properties`).

```sh
./gradlew :app:assembleDebug :app:lintDebug
```

With an emulator or Android device connected, run the UI tests (no network needed):

```sh
./gradlew :app:connectedDebugAndroidTest
```
