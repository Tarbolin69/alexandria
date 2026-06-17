package com.libreria.alexandria.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PerfilEntity::class, LibroGuardadoEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun perfilDao(): PerfilDao
    abstract fun libroGuardadoDao(): LibroGuardadoDao
}
