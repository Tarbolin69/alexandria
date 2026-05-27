package com.libreria.alexandria.data

// Básicamente, lo mismo que LibroRespuesta.kt,
// pero con unos cambios menores debido a que
// Subjects API devuelve una lista de libros
// con diferentes valores que Search API.

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeneroRespuesta(
    @Json(name = "works") val obras: List<ObraDto>
)

@JsonClass(generateAdapter = true)
data class ObraDto(
    @Json(name = "key") val llave: String,
    @Json(name = "title") val titulo: String,
    @Json(name = "authors") val autores: List<AutorDto>?,
    @Json(name = "cover_id") val portadaId: Long?,
    @Json(name = "first_publish_year") val pubFecha: Int?,
)

data class AutorDto(
    @Json(name = "name") val nombre: String,
)
