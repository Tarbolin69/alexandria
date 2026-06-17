package com.libreria.alexandria.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroGuardadoDao {

    @Query("SELECT * FROM libros_guardados ORDER BY titulo ASC")
    fun obtenerTodos(): Flow<List<LibroGuardadoEntity>>

    @Query("SELECT * FROM libros_guardados WHERE bookId = :bookId")
    fun obtenerPorId(bookId: String): Flow<LibroGuardadoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(libro: LibroGuardadoEntity)

    @Query("DELETE FROM libros_guardados WHERE bookId = :bookId")
    suspend fun eliminar(bookId: String)
}
