package com.libreria.alexandria.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryAPI {
    @GET("search.json")
    suspend fun buscarLibros(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): LibroRespuesta

    @GET("subjects/{subject}.json")
    suspend fun buscarPorGenero(
        @Path("subject") subject: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
    ): GeneroResult

    @GET("works/{id}.json")
    suspend fun obtenerObra(
        @Path("id") id: String,
    ): ObraRespuesta
}