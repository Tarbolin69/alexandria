package com.libreria.alexandria.data

data class LibroDetalleInfo(
    val titulo: String,
    val autor: String,
    val cubiertaId: String,
    val pubFecha: String,
    val descripcion: String,
)

class LibroRemoteDataSource(private val api: OpenLibraryAPI) {
    suspend fun buscarLibros(query: String, page: Int, limit: Int): List<Libro> {
        val respuesta = api.buscarLibros(query, page, limit)
        return respuesta.libros.map { dto ->
            Libro(
                id = dto.llave.removePrefix("/works/"),
                titulo = dto.titulo,
                autor = dto.autor?.firstOrNull() ?: "Sin autor",
                cubiertaId = if (dto.portada != null) {
                    "https://covers.openlibrary.org/b/id/${dto.portada}-M.jpg"
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
                    "https://covers.openlibrary.org/b/id/${obra.portadaId}-M.jpg"
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
                "https://covers.openlibrary.org/b/id/$portadaId-M.jpg"
            } else "",
            pubFecha = respuesta.pubFecha ?: "N/A",
            descripcion = respuesta.descripcion ?: "Sin descripción",
        )
    }
}
