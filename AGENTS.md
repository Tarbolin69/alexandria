# Alexandria

Android app for book reviews (Spanish). Single `:app` module.

## Stack

- **Kotlin 2.4.0** + **Jetpack Compose** + **Material 3**
- **Gradle 9.3.1** / **AGP 9.1.1** / **JDK 21** (Java 11 bytecode target)
- **minSdk 24**, **targetSdk 36**, **compileSdk 37**
- **Hilt 2.59.2** for DI, **Firebase Auth** for Google Sign-In
- **Room** for local persistence, **Firestore** for cloud (reviews, profiles)

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
| Nav routes | `components/Screens.kt` — sealed class `Screen` (7 routes) |
| Screens | `components/splash/`, `components/login/`, `components/listado/`, `components/detalle/`, `components/libreria/`, `components/perfil/`, `components/critica/` — each has a `Pantalla` + `ViewModel` pair |
| Data layer | `data/` — OpenLibrary API via Retrofit + Moshi, Deepseek AI via OkHttp, repository pattern, Firebase Auth, Firestore |
| DI | `di/DataModule.kt` — Hilt `@Module` providing Retrofit, Moshi, repositories, Room DB |
| Theme | `ui/theme/Theme.kt` — Material 3 with hardcoded dark color scheme (`YellowDarkColorScheme`), custom Aporetic font family |
| Markdown | `ui/MarkdownRenderer.kt` — custom markdown-to-Compose renderer for AI recommendations |
| Version catalog | `gradle/libs.versions.toml` (all dependencies and plugins) |

Single activity, single module (`:app`), package `com.libreria.alexandria`.

## Navigation routes

All **7 of 7** declared routes are wired in the NavHost:

| Route | Description |
|---|---|
| `Splash` → `"splash_screen"` | Start destination — auth check → BookList or Login |
| `Login` → `"login_screen"` | Google Sign-In via Credential Manager |
| `BookList` → `"book_list_screen"` | Search + genre chips + infinite scroll |
| `BookDetail` → `"book_detail_screen/{bookId}/{autor}?pubFecha={pubFecha}"` | Book detail, reviews, bookmark |
| `BookReview` → `"book_review_screen/{bookId}/{autor}?pubFecha={pubFecha}"` | Write/publish a review |
| `BookLibrary` → `"book_library_screen"` | Saved books + AI recommendations |
| `UsuarioPerfil` → `"usuario_perfil_screen"` | Profile view/edit + sign-out |

Flow: `Splash` → (auth check) → `BookList` or `Login` → `BookDetail` ⇄ `BookReview`.
Bottom nav: Buscar (`BookList`), Libreria (`BookLibrary`), Perfil (`UsuarioPerfil`).

## Data layer details

- **Models**: `Libro` (domain), `LibroRespuesta`/`GeneroRespuesta`/`ObraRespuesta` (API DTOs), `LibroDetalleInfo` (detail screen model in its own file `data/LibroDetalleInfo.kt`), `Review`, `PerfilUsuarioInfo`.
- **API**: `OpenLibraryAPI.kt` — Retrofit interface with 3 suspend endpoints (`/search.json`, `/subjects/{subject}.json`, `/works/{id}.json`). Base URL `https://openlibrary.org/`.
- **Serialization**: Moshi with code-gen (`@JsonClass(generateAdapter = true)`). `DescripcionAdapter.kt` handles inconsistent description format.
- **Images**: Cover URLs built via `LibroRemoteDataSource.buildCoverUrl(coverId)` → `https://covers.openlibrary.org/b/id/{id}-M.jpg`, loaded via Glide Compose.
- **Auth**: `FirebaseAuthRepositorio` implements `AuthRepositorio` interface — Google Sign-In via `CredentialManager` + `GetGoogleIdOption`.
- **Repository**: `LibroRepositorio` wraps `LibroRemoteDataSource` with `Result<T>` error handling.
- **Saved books**: `LibroGuardadoRepositorio` — Room-backed CRUD for bookmarking books.
- **Reviews**: `ReviewRepositorio` — Firestore-backed publish/query with real-time listeners.
- **Profiles**: `PerfilRepositorio` (Room local cache) + `PerfilFirebaseRepositorio` (Firestore cloud) — dual persistence with Firebase-first, Room-fallback strategy.

## AI Recommendations

- **API key**: Stored in `EncryptedSharedPreferences` (`DeepseekApiKeyStorage.kt`) — AES-256 encrypted.
- **Service**: `DeepseekService.kt` calls `https://api.deepseek.com/v1/chat/completions` via OkHttp with the user's library as prompt context.
- **UI**: Robot icon on Libreria screen → `ModalBottomSheet` with API key input or AI-generated markdown-rendered recommendations.
- **Markdown**: Custom renderer in `ui/MarkdownRenderer.kt` handles `**bold**`, `*italic*`, `#` headers, `-` lists, `1.` numbered lists.

## Local database

- **Room DB**: `AppDatabase.kt` — version 2, entities: `PerfilEntity`, `LibroGuardadoEntity`.
- **No destructive migration** (`fallbackToDestructiveMigration(false)`) — add proper migrations before schema changes.

## Build notes

- `kotlin-metadata-jvm:2.4.0` is forced via `buildscript` in root `build.gradle.kts` + `resolutionStrategy` in `app/build.gradle.kts` — required because Hilt 2.59.2 bundles an older version that can't read Kotlin 2.4.0 metadata.
- Kotlin Android plugin is auto-applied by AGP — do NOT add `org.jetbrains.kotlin.android` explicitly.

## Gotchas

- **Spanish naming** — all UI components use Spanish names (`Pantalla`, `Listado`, `Detalle`, `Libreria`, `Perfil`, `Critica`). Stick to the convention.
- `domain/` directory exists but is empty — placeholder for future clean architecture use.
- `material-icons-extended` is included for the `SmartToy` (robot) icon on the Libreria screen.
- Sign-out shows an `AlertDialog` confirmation before calling `FirebaseAuth.signOut()` and navigating to Login.
- No CI/CD configured.
- Tests cover ViewModels only (37 tests across 7 test classes) — no Compose UI tests, no DAO/repository tests.
