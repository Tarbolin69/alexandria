package com.libreria.alexandria.data

import com.libreria.alexandria.data.local.PerfilDao
import com.libreria.alexandria.data.local.PerfilEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class PerfilRepositorio @Inject constructor(
    private val perfilDao: PerfilDao
) {
    open fun obtenerPerfil(userId: String): Flow<PerfilEntity?> = perfilDao.obtenerPerfil(userId)

    open suspend fun guardarPerfil(perfil: PerfilEntity) = perfilDao.guardarPerfil(perfil)
}
