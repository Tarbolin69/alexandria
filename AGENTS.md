# Alexandria

Android app for book reviews (Spanish). Single `:app` module, early stage.

## Stack

- **Kotlin 2.2.10** + **Jetpack Compose** + **Material 3**
- **Gradle 9.3.1** / **AGP 9.1.1** / **JDK 21** (Java 11 bytecode target)
- **minSdk 24**, **target/compileSdk 36**

## Essential commands

```sh
./gradlew assembleDebug              # build
./gradlew testDebugUnitTest           # unit tests (JUnit 4)
./gradlew connectedDebugAndroidTest   # instrumented tests (needs emulator/device)
./gradlew testDebugUnitTest --tests "com.libreria.alexandria.*"  # single test class
./gradlew lint                        # Android lint
```

No formatter or linter besides `./gradlew lint` is configured.

## Architecture

| Thing | Location |
|---|---|
| Entrypoint | `app/src/main/java/.../MainActivity.kt` |
| Nav routes | `components/Screens.kt` — sealed class `Screen` (Splash, Login, BookList, BookDetail, BookReview, BookLibrary) |
| Theme | `ui/theme/Theme.kt` — Material 3 with dynamic colors |
| Version catalog | `gradle/libs.versions.toml` (all dependencies and plugins live here) |

Single activity, single module (`:app`), package `com.libreria.alexandria`.

## Gotchas

- **ViewBinding enabled** alongside Compose (`buildFeatures.viewBinding = true`).
- **`SplashScreen.kt`** currently breaks the `@Composable` contract: missing annotation, parameter name typo (`modifider`).
- Version catalog has stale version keys (*e.g.* `composeBom` pinned at `2024.09.00` in `[versions]` but the library alias resolves `2026.05.01`).
- No CI/CD configured. Active branch: `SplashScreen` (not yet ahead of `main`).
