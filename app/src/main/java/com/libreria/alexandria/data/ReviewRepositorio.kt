package com.libreria.alexandria.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ReviewRepositorio @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun coleccionCriticas() = firestore.collection("criticas")

    private fun documentoId(bookId: String, userId: String) = "${bookId}_$userId"

    open suspend fun publicarReview(
        bookId: String,
        userId: String,
        usuario: String,
        puntuacion: Int,
        texto: String,
    ) {
        withContext(Dispatchers.IO) {
            Tasks.await(
                coleccionCriticas().document(documentoId(bookId, userId)).set(
                    mapOf(
                        "bookId" to bookId,
                        "userId" to userId,
                        "usuario" to usuario,
                        "puntuacion" to puntuacion,
                        "texto" to texto,
                        "fecha" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                    )
                )
            )
        }
    }

    open fun yaCalificado(bookId: String, userId: String): Flow<Boolean> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = coleccionCriticas()
                .document(documentoId(bookId, userId))
                .addSnapshotListener { snapshot, _ ->
                    trySend(snapshot != null && snapshot.exists())
                }
        } catch (_: Exception) {
            trySend(false)
        }
        awaitClose { listener?.remove() }
    }

    open fun obtenerReviews(bookId: String): Flow<List<Review>> = callbackFlow {
        var listener: ListenerRegistration? = null
        try {
            listener = coleccionCriticas()
                .whereEqualTo("bookId", bookId)
                .orderBy("fecha", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    val reviews = snapshot?.documents?.mapNotNull { doc ->
                        val usuario = doc.getString("usuario") ?: return@mapNotNull null
                        val puntuacion = doc.getLong("puntuacion")?.toInt() ?: return@mapNotNull null
                        val texto = doc.getString("texto") ?: ""
                        Review(
                            id = doc.id,
                            usuario = usuario,
                            puntuacion = puntuacion,
                            texto = texto,
                        )
                    } ?: emptyList()
                    trySend(reviews)
                }
        } catch (_: Exception) {
            trySend(emptyList())
        }
        awaitClose { listener?.remove() }
    }
}
