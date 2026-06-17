package com.libreria.alexandria.data

// Clase para las "cartas" con las que se
// muestran los libros en la app. Todas
// las variables son Strings sin NULL.
data class LibroDetalleInfo(
    val titulo: String,
    val autor: String,
    val cubiertaId: String,
    val pubFecha: String,
    val descripcion: String,
)

// Clase por la cual se procesan todas las llamadas API
// y por la cual sus respuestas son transformadas en data
// que la app puede usar para sus pantallas. Los endpoints
// son manejados por Retrofit, como dicho en el respectivo
// archivo de las API.
class LibroRemoteDataSource(private val api: OpenLibraryAPI) {
    // Busca libros por título. Es lo que usa LibroListadoPantalla.kt
    // cuando se utiliza la barra de búsqueda.
    suspend fun buscarLibros(query: String, page: Int, limit: Int): List<Libro> {
        val respuesta = api.buscarLibros(query, page, limit)
        return respuesta.libros.map { dto ->
            Libro( // Transforma la lista de LibroDto a Libro con "map"
                id = dto.llave.removePrefix("/works/"),
                titulo = dto.titulo,
                autor = dto.autor?.firstOrNull() ?: "Sin autor",
                cubiertaId = if (dto.portada != null) {
                    // Esta API me pareció medio molesta de usar, ya que no
                    // sigue el mismo formato que el resto de las API de
                    // Open Library (usa un subdominio en vez de subdirectorio)
                    "https://covers.openlibrary.org/b/id/${dto.portada}-M.jpg"
                } else "", // Casi imposible que suceda, pero nunca se sabe
                pubFecha = dto.pubFecha?.toString() ?: "N/A"
            )
        }
    }

    // Busca libros por género. Es lo que usa LibroListadoPantalla.kt
    // cuando se busca por etiquetas de género.
    suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): List<Libro> {
        val respuesta = api.buscarPorGenero(subject, limit, offset)
        return respuesta.obras.map { obra ->
            Libro(
                id = obra.llave.removePrefix("/works/"),
                titulo = obra.titulo,
                // Pequeña diferencia porque esta API tiene el nombre
                // del autor dentro de una lista junto con su código.
                autor = obra.autores?.firstOrNull()?.nombre ?: "Sin autor",
                cubiertaId = if (obra.portadaId != null) {
                    "https://covers.openlibrary.org/b/id/${obra.portadaId}-M.jpg"
                } else "",
                pubFecha = obra.pubFecha?.toString() ?: "N/A"
            )
        }
    }

    // Obtiene los detalles de un libro usando su código de obra. Es lo
    // que usa LibroDetallePantalla.kt cuando se selecciona un libro.
    suspend fun obtenerInfoDetalle(libroId: String, autor: String): LibroDetalleInfo {
        val respuesta = api.obtenerObra(libroId)
        val portadaId = respuesta.portadas?.firstOrNull()
        return LibroDetalleInfo(
            titulo = respuesta.titulo ?: "Sin título",
            // Por razones que escapan mi comprensión, la Works API
            // no incluye el nombre del autor por ningún lado. Asi
            // que simplemente se agarra la variable "autor" del
            // libro que se seleccionó.
            autor = autor,
            cubiertaId = if (portadaId != null) {
                "https://covers.openlibrary.org/b/id/$portadaId-M.jpg"
            } else "",
            pubFecha = respuesta.pubFecha?.extraerAño() ?: "N/A",
            descripcion = respuesta.descripcion ?: "Sin descripción",
        )
    }
}

private val añoRegex = Regex("\\b(\\d{4})\\b")

private fun String.extraerAño(): String = añoRegex.find(this)?.groupValues?.get(1) ?: this
