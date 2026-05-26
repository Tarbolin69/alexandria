package com.libreria.alexandria.data

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryAPI {
    @GET("search.json")
    suspend fun buscarLibros(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): LibroRespuesta
}