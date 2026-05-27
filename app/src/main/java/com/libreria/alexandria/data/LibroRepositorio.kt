package com.libreria.alexandria.data

// Capa de abstracción entre las API de Open Library
// y los diferentes ViewModels que usan sus datos.

interface LibroRepositorio {
    suspend fun buscarLibros(query: String, page: Int = 1, limit: Int = 20): Result<List<Libro>>
    suspend fun buscarPorGenero(subject: String, offset: Int = 0, limit: Int = 20): Result<List<Libro>>
    suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo>
}

class LibroRepositorioImpl(private val remoteDataSource: LibroRemoteDataSource) : LibroRepositorio {
    override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> {
        return try {
            val libros = remoteDataSource.buscarLibros(query, page, limit)
            Result.success(libros)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> {
        return try {
            val libros = remoteDataSource.buscarPorGenero(subject, offset, limit)
            Result.success(libros)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> {
        return try {
            val info = remoteDataSource.obtenerInfoDetalle(libroId, autor)
            Result.success(info)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
