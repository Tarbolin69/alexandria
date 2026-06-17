package com.libreria.alexandria.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PerfilEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun perfilDao(): PerfilDao
}
