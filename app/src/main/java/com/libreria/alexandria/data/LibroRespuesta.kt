package com.libreria.alexandria.data

// El "Data Transfer Object" para la Search API
// de Open Library. Contiene tanto la lista de
// "docs" (libros) que se obtiene por la API,
// y el contenido en sí de los "docs" (libros).

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Maneja todos los "docs" que devuelve la API
// y los convierte en una lista de libros.
@JsonClass(generateAdapter = true)
data class LibroRespuesta(
     @Json(name = "docs") val libros: List<LibroDto>
)

// Maneja uno de los "docs" que devuelve la API, y
// lo convierte en un libro con todos los datos
// proveídos por la misma.
@JsonClass(generateAdapter = true)
data class LibroDto(
     @Json(name = "title") val titulo: String,
     @Json(name = "author_name") val autor: List<String>?,
     @Json(name = "first_publish_year") val pubFecha: Int?,
     @Json(name = "cover_i") val portada: Long?,
     @Json(name = "key") val llave: String,
)