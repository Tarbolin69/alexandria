package com.libreria.alexandria.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.libreria.alexandria.data.local.PerfilEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// El perfil del usuario en la nube (Firestore)
// para tener sincronizado entre dispositivos.

@Singleton
open class PerfilFirebaseRepositorio @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun perfilDoc(userId: String) = firestore.collection(COLECCION_PERFILES).document(userId)

    companion object {
        private const val COLECCION_PERFILES = "perfiles"
    }

    open fun obtenerPerfil(userId: String): Flow<PerfilEntity?> = callbackFlow {
        val listener = perfilDoc(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            val perfil = if (snapshot != null && snapshot.exists()) {
                PerfilEntity(
                    userId = snapshot.id,
                    nombre = snapshot.getString("nombre") ?: "",
                    email = snapshot.getString("email") ?: "",
                    acercaDeMi = snapshot.getString("acercaDeMi") ?: "",
                    telefono = snapshot.getString("telefono") ?: "",
                    sitioWeb = snapshot.getString("sitioWeb") ?: ""
                )
            } else {
                null
            }
            trySend(perfil)
        }
        awaitClose { listener.remove() }
    }

    open suspend fun guardarPerfil(perfil: PerfilEntity): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                Tasks.await(
                    perfilDoc(perfil.userId).update(
                        mapOf(
                            "nombre" to perfil.nombre,
                            "email" to perfil.email,
                            "acercaDeMi" to perfil.acercaDeMi,
                            "telefono" to perfil.telefono,
                            "sitioWeb" to perfil.sitioWeb
                        )
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        }
    }

    open suspend fun inicializarSiNoExiste(userId: String, nombre: String, email: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                val doc = Tasks.await(perfilDoc(userId).get())
                if (!doc.exists()) {
                    Tasks.await(
                        perfilDoc(userId).set(
                            mapOf(
                                "nombre" to nombre,
                                "email" to email,
                                "acercaDeMi" to "",
                                "telefono" to "",
                                "sitioWeb" to ""
                            )
                        )
                    )
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Result.failure(e)
        }
    }
}
