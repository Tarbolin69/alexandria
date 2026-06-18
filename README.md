# Alexandria

<p align="center" width="100%">
  <img src="https://i.imgur.com/s6RWNh1.png" />
</p>

Alexandria es una aplicación para descubrir y hacer reseñas de libros junto a gente de todo el mundo.

## Funcionalidades

- **Búsqueda de libros** por título usando la API pública de [Open Library](https://openlibrary.org/).
- **Exploración por género** con chips de filtro y scroll infinito.
- **Ficha detallada** con portada, descripción, autor, fecha de publicación y generos.
- **Reseñas** con puntuación de estrellas y escritos, almacenadas en Firestore.
- **Librería personal** con piezas guardadas localmente con Room.
- **Perfil de usuario** editable, persistido en Firestore.
- **Autenticación** con Google a través de Firebase Auth.

## Arquitectura

```
app/src/main/java/com/libreria/alexandria/
├── components/
│   ├── splash/        # Pantalla de bienvenida y verificación de autenticación
│   ├── login/         # Inicio de sesión con Google
│   ├── listado/       # Listado de libros con búsqueda y filtros
│   ├── detalle/       # Ficha detallada del libro
│   ├── critica/       # Escritura de reseña (estrellas + texto)
│   ├── libreria/      # Libros guardados por el usuario
│   └── perfil/        # Perfil de usuario editable
├── data/
│   ├── local/         # Room (AppDatabase, DAOs, entidades)
│   ├── remote/        # API de Open Library, Firestore
│   └── repositorio/   # Patrón repositorio
├── di/                # Módulo Hilt de inyección de dependencias
├── dominio/           # Capa de dominio (vacía, para uso futuro)
└── ui/theme/          # Tema Material 3 oscuro personalizado
```

Cada pantalla sigue el patrón `Pantalla` (Composable) + `ViewModel` (Hilt).

## Pantallas y navegación

| Pantalla | Ruta | Descripción |
|---|---|---|
| Splash | `splash_screen` | Verifica autenticación y redirige a `BookList` o `Login` |
| Login | `login_screen` | Google Sign-In con Credential Manager |
| BookList | `book_list_screen` | Búsqueda, chips de género y scroll infinito |
| BookDetail | `book_detail_screen/{bookId}/{autor}` | Portada, descripción, guardar libro, ver reseñas |
| BookReview | `book_review_screen/{bookId}/{autor}` | Escribir reseña con puntuación y texto |
| BookLibrary | `book_library_screen` | Libros guardados en Room |
| UsuarioPerfil | `usuario_perfil_screen` | Perfil editable y cierre de sesión |

**Barra de navegación inferior** con tres pestañas: **Buscar**, **Librería** y **Perfil**.

## Capa de datos

### API de Open Library

| Endpoint | Uso |
|---|---|
| `GET /search.json` | Búsqueda de libros por título |
| `GET /subjects/{subject}.json` | Libros por género |
| `GET /works/{id}.json` | Detalle de una obra |

Portadas obtenidas desde `https://covers.openlibrary.org/b/id/{id}-M.jpg`.

### Persistencia

- **Room (SQLite)**: libros guardados por el usuario y perfil local.
- **Firebase Firestore**: reseñas de libros y datos de perfil del usuario.
- **Firebase Auth**: autenticación con Google.

## Comandos esenciales

```sh
./gradlew assembleDebug              # Compilar
./gradlew testDebugUnitTest           # Tests unitarios (JUnit 4)
./gradlew connectedDebugAndroidTest   # Tests instrumentados (requiere emulador/dispositivo)
./gradlew lint                        # Análisis estático
```

