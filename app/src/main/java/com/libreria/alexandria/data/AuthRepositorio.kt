package com.libreria.alexandria.data

data class PerfilUsuarioInfo(
    val nombre: String,
    val email: String,
    val fotoUrl: String?
)

interface AuthRepositorio {
    fun isUsuarioAutenticado(): Boolean
    suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit>
    fun cerrarSesion()
    fun obtenerUsuarioInfo(): PerfilUsuarioInfo?
    fun obtenerUsuarioId(): String
}
