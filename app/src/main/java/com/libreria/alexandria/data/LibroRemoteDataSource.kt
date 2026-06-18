package com.libreria.alexandria.data

// Clase por la cual se procesan todas las llamadas API
// y por la cual sus respuestas son transformadas en data
// que la app puede usar para sus pantallas. Los endpoints
// son manejados por Retrofit, como dicho en el respectivo
// archivo de las API.
class LibroRemoteDataSource(private val api: OpenLibraryAPI) {
    suspend fun buscarLibros(query: String, page: Int, limit: Int): List<Libro> {
        val respuesta = api.buscarLibros(query, page, limit)
        return respuesta.libros.map { dto ->
            Libro(
                id = dto.llave.removePrefix("/works/"),
                titulo = dto.titulo,
                autor = dto.autor?.firstOrNull() ?: "Sin autor",
                cubiertaId = if (dto.portada != null) {
                    buildCoverUrl(dto.portada)
                } else "",
                pubFecha = dto.pubFecha?.toString() ?: "N/A"
            )
        }
    }

    suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): List<Libro> {
        val respuesta = api.buscarPorGenero(subject, limit, offset)
        return respuesta.obras.map { obra ->
            Libro(
                id = obra.llave.removePrefix("/works/"),
                titulo = obra.titulo,
                autor = obra.autores?.firstOrNull()?.nombre ?: "Sin autor",
                cubiertaId = if (obra.portadaId != null) {
                    buildCoverUrl(obra.portadaId)
                } else "",
                pubFecha = obra.pubFecha?.toString() ?: "N/A"
            )
        }
    }

    suspend fun obtenerInfoDetalle(libroId: String, autor: String): LibroDetalleInfo {
        val respuesta = api.obtenerObra(libroId)
        val portadaId = respuesta.portadas?.firstOrNull()
        return LibroDetalleInfo(
            titulo = respuesta.titulo ?: "Sin título",
            autor = autor,
            cubiertaId = if (portadaId != null) {
                buildCoverUrl(portadaId)
            } else "",
            pubFecha = respuesta.pubFecha?.extraerFecha() ?: "N/A",
            descripcion = respuesta.descripcion ?: "Sin descripción",
        )
    }

    companion object {
        private fun buildCoverUrl(coverId: Number): String =
            "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
    }
}

// Mas que nada porque Open Library no incluye fechas correctas
// dependiendo del API asi que hacemos un poquis de magia feocha
private val fechaRegex = Regex("\\b(\\d{4})\\b")

private fun String.extraerFecha(): String = fechaRegex.find(this)?.groupValues?.get(1) ?: this
