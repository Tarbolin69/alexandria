package com.libreria.alexandria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libros_guardados")
data class LibroGuardadoEntity(
    @PrimaryKey
    val bookId: String,
    val titulo: String,
    val autor: String,
    val cubiertaId: String,
    val pubFecha: String,
    val descripcion: String,
)
