package com.libreria.alexandria.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LibroRespuesta(
     @Json(name = "docs") val libros: List<LibroDto>
)

@JsonClass(generateAdapter = true)
data class LibroDto(
     @Json(name = "title") val titulo: String,
     @Json(name = "author_name") val autor: List<String>?,
     @Json(name = "first_publish_year") val pubFecha: Int?,
     @Json(name = "cover_i") val portada: Long?,
     @Json(name = "key") val llave: String,
)