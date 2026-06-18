package com.libreria.alexandria.data

data class Review(
    val id: String,
    val usuario: String,
    val puntuacion: Int,
    val texto: String,
)
