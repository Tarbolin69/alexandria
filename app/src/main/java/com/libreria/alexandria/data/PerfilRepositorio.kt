package com.libreria.alexandria.data

import com.libreria.alexandria.data.local.PerfilDao
import com.libreria.alexandria.data.local.PerfilEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerfilRepositorio @Inject constructor(
    private val perfilDao: PerfilDao
) {
    fun obtenerPerfil(userId: String): Flow<PerfilEntity?> = perfilDao.obtenerPerfil(userId)

    suspend fun guardarPerfil(perfil: PerfilEntity) = perfilDao.guardarPerfil(perfil)
}
