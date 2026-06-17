# Alexandria

Android app for book reviews (Spanish). Single `:app` module, early stage.

## Stack

- **Kotlin 2.3.21** + **Jetpack Compose** + **Material 3**
- **Gradle 9.3.1** / **AGP 9.1.1** / **JDK 21** (Java 11 bytecode target)
- **minSdk 24**, **target/compileSdk 36**
- **Hilt 2.59.2** for DI, **Firebase Auth** for Google Sign-In

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
| Entrypoint | `app/src/main/java/.../MainActivity.kt` (single activity, `@AndroidEntryPoint`) |
| Application | `AlexandriaApplication.kt` (`@HiltAndroidApp`) |
| Nav routes | `components/Screens.kt` — sealed class `Screen` (Splash, Login, BookList, BookDetail, BookReview, BookLibrary, UsuarioPerfil) |
| Screens | `components/splash/`, `components/login/`, `components/listado/`, `components/detalle/` — each has a `Pantalla` + `ViewModel` pair |
| Data layer | `data/` — OpenLibrary API via Retrofit + Moshi, repository pattern, Firebase Auth |
| DI | `di/DataModule.kt` — Hilt `@Module` providing Retrofit, Moshi, repositories |
| Theme | `ui/theme/Theme.kt` — Material 3 with hardcoded dark color scheme (`YellowDarkColorScheme`), custom Aporetic font family |
| Version catalog | `gradle/libs.versions.toml` (all dependencies and plugins live here) |

Single activity, single module (`:app`), package `com.libreria.alexandria`.

## Navigation routes

Only **4 of 7** declared routes are wired in the NavHost:

| Route | In NavHost? |
|---|---|
| `Splash` → `"splash_screen"` | Yes — start destination |
| `Login` → `"login_screen"` | Yes — Google Sign-In via Credential Manager |
| `BookList` → `"book_list_screen"` | Yes — search + genre chips + infinite scroll |
| `BookDetail` → `"book_detail_screen/{bookId}/{autor}"` | Yes — URL-encoded params via `SavedStateHandle` |
| `BookReview`, `BookLibrary`, `UsuarioPerfil` | No — declared but not wired |

Flow: `Splash` → (auth check) → `BookList` or `Login` → `BookDetail` (with back nav).

## Data layer details

- **Models**: `Libro` (domain), `LibroRespuesta`/`GeneroRespuesta`/`ObraRespuesta` (API DTOs), `LibroDetalleInfo` (detail screen model in `LibroRemoteDataSource.kt`).
- **API**: `OpenLibraryAPI.kt` — Retrofit interface with 3 suspend endpoints (`/search.json`, `/subjects/{subject}.json`, `/works/{id}.json`). Base URL `https://openlibrary.org/`.
- **Serialization**: Moshi with code-gen (`@JsonClass(generateAdapter = true)`). `DescripcionAdapter.kt` handles inconsistent description format.
- **Images**: Cover URLs built as `https://covers.openlibrary.org/b/id/{id}-M.jpg`, loaded via Glide Compose.
- **Auth**: `FirebaseAuthRepositorio` implements `AuthRepositorio` interface — Google Sign-In via `CredentialManager` + `GetGoogleIdOption`.
- **Repository**: `LibroRepositorio` wraps `LibroRemoteDataSource` with `Result<T>` error handling.

## Gotchas

- **Spanish naming** — all UI components use Spanish names (`Pantalla`, `Listado`, `Detalle`). Stick to the convention.
- **ViewBinding enabled** alongside Compose (`buildFeatures.viewBinding = true`).
- `domain/` directory exists but is empty — placeholder for future clean architecture use.
- Paging 3 libraries are included but the app uses manual pagination (`paginaActual`, `librosCompilados` in `LibrosViewModel`).
- The `LibrosViewModel` class in `LibroListadoViewModel.kt` has a mismatched filename (plural `Libros` vs singular `Libro`).
- Book detail screen has hardcoded placeholder reviews (`ResenaPlaceholder`) — marked for replacement with real database.
- The sign-out button lives in the search bar's `leadingIcon` slot — temporary, per code comment.
- No CI/CD configured.
- Tests are placeholder only (`ExampleUnitTest.kt`, `ExampleInstrumentedTest.kt`) — no real domain or UI tests.
