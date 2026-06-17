package com.libreria.alexandria.data

import com.libreria.alexandria.data.local.LibroGuardadoDao
import com.libreria.alexandria.data.local.LibroGuardadoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibroGuardadoRepositorio @Inject constructor(
    private val dao: LibroGuardadoDao
) {
    fun obtenerTodos(): Flow<List<LibroGuardadoEntity>> = dao.obtenerTodos()

    fun esMarcado(bookId: String): Flow<LibroGuardadoEntity?> = dao.obtenerPorId(bookId)

    suspend fun marcar(libro: LibroGuardadoEntity) = dao.insertar(libro)

    suspend fun desmarcar(bookId: String) = dao.eliminar(bookId)
}
