package com.libreria.alexandria.data

interface AuthRepositorio {
    fun isUsuarioAutenticado(): Boolean
    suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit>
    fun cerrarSesion()
}
