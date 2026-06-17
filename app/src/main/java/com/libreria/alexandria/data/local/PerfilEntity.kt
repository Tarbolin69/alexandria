package com.libreria.alexandria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil")
data class PerfilEntity(
    @PrimaryKey
    val userId: String,
    val nombre: String,
    val email: String,
    val acercaDeMi: String,
    val telefono: String,
    val sitioWeb: String
)
