package com.libreria.alexandria.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LibroRespuesta(
     @Json(name = "docs") val libros: List<LibroDto>
)

@JsonClass(generateAdapter = true)
data class LibroDto(
     @Json(name = "titulo") val titulo: String,
     @Json(name = "autor") val autor: String,
     @Json(name = "pubFecha") val pubFecha: String,
     @Json(name = "cubiertaId") val portada: String,
     @Json(name = "generos") val generos: List<String>
)