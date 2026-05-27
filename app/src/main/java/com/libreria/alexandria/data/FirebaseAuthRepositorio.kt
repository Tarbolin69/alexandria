package com.libreria.alexandria.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirebaseAuthRepositorio : AuthRepositorio {
    private val auth = FirebaseAuth.getInstance()

    override fun isUsuarioAutenticado(): Boolean = auth.currentUser != null

    override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> {
        return try {
            val credencial = GoogleAuthProvider.getCredential(idToken, null)
            withContext(Dispatchers.IO) {
                Tasks.await(auth.signInWithCredential(credencial))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun cerrarSesion() = auth.signOut()
}
