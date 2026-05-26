package com.libreria.alexandria.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Define los endpoints de la API de Open Library
// usando Retrofit. Cada una de las funciones
// usan "suspend" para que puedan ser todas
// llamadas desde una corrutina.

interface OpenLibraryAPI {
    // Usa la Search API para buscar libros por
    // titulo y devuelve una lista de LibroDto.
    @GET("search.json")
    suspend fun buscarLibros(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): LibroRespuesta
    // Usa la Subjects API para buscar libros
    // por genero, y devuelve una lista de ObraDto.
    @GET("subjects/{subject}.json")
    suspend fun buscarPorGenero(
        @Path("subject") subject: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): GeneroRespuesta
    // Usa la Works API para obtener informacion
    // especifica de un libro para la pantalla de
    // detalles (LibroDetallePantalla.kt).
    @GET("works/{id}.json")
    suspend fun obtenerObra(
        @Path("id") id: String,
    ): ObraRespuesta
}