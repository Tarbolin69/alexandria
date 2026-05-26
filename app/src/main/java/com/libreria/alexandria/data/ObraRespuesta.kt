package com.libreria.alexandria.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ObraRespuesta(
    @Json(name = "description") val descripcion: String?,
    @Json(name = "title") val titulo: String?,
    @Json(name = "covers") val portadas: List<Int>?,
    @Json(name = "first_publish_date") val pubFecha: String?,
)
