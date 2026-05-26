package com.libreria.alexandria.data

class LibroRepositorio(private val remoteDataSource: LibroRemoteDataSource) {
    suspend fun buscarLibros(query: String, page: Int = 1, limit: Int = 20): Result<List<Libro>> {
        return try {
            val libros = remoteDataSource.buscarLibros(query, page, limit)
            Result.success(libros)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}