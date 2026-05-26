package com.libreria.alexandria.data

// Usa las funciones definidas en OpenLibraryAPI.kt
// y se encarga de revisar si estos devuelven resultados
// o si ocurrio un error. En caso de exito, los mando a
// LibroRemoteDataSource.kt para que se procesen.

class LibroRepositorio(private val remoteDataSource: LibroRemoteDataSource) {
    suspend fun buscarLibros(query: String, page: Int = 1, limit: Int = 20): Result<List<Libro>> {
        return try {
            val libros = remoteDataSource.buscarLibros(query, page, limit)
            Result.success(libros)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun buscarPorGenero(subject: String, offset: Int = 0, limit: Int = 20): Result<List<Libro>> {
        return try {
            val libros = remoteDataSource.buscarPorGenero(subject, offset, limit)
            Result.success(libros)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> {
        return try {
            val info = remoteDataSource.obtenerInfoDetalle(libroId, autor)
            Result.success(info)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
