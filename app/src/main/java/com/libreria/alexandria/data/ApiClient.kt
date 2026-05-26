package com.libreria.alexandria.data

// Conexion entre Retrofit y Moshi que
// usa OpenLibraryAPI.kt y maneja los
// datos que esta devuelve.

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiClient {
    private val moshi = Moshi.Builder()
        .add(DescripcionAdapter()) // Para las descripciones formateadas raras.
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    val api: OpenLibraryAPI = retrofit.create(OpenLibraryAPI::class.java)
}
