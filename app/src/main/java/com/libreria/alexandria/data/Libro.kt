package com.libreria.alexandria.data

// Clase basica de un libro, con todos los datos
// obtenidos mediante la Search API de Open Library.
data class Libro(
    val id: String,
    val titulo: String,
    val autor: String,
    val pubFecha: String,
    val cubiertaId: String,
)