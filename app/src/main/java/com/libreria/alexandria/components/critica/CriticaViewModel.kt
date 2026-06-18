package com.libreria.alexandria.components.critica

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.LibroRepositorio
import com.libreria.alexandria.data.ReviewRepositorio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CriticaUiState {
    data object Cargando : CriticaUiState
    data class Completado(
        val titulo: String,
        val cubiertaUrl: String,
        val autor: String,
        val pubFecha: String,
    ) : CriticaUiState
    data object Publicando : CriticaUiState
    data object Publicado : CriticaUiState
    data class Error(val mensaje: String) : CriticaUiState
}

@HiltViewModel
class CriticaViewModel @Inject constructor(
    private val repositorio: LibroRepositorio,
    private val libroGuardadoRepositorio: LibroGuardadoRepositorio,
    private val authRepositorio: AuthRepositorio,
    private val reviewRepositorio: ReviewRepositorio,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val libroId: String = savedStateHandle["bookId"] ?: ""
    private val autor: String = savedStateHandle["autor"] ?: ""
    private val pubFechaPasada: String = savedStateHandle.get<String>("pubFecha") ?: ""

    private val _uiState = MutableStateFlow<CriticaUiState>(CriticaUiState.Cargando)
    val uiState: StateFlow<CriticaUiState> = _uiState.asStateFlow()

    init {
        cargarInfo()
    }

    fun publicar(puntuacion: Int, texto: String) {
        if (puntuacion == 0 || texto.isBlank()) return
        viewModelScope.launch {
            _uiState.value = CriticaUiState.Publicando
            try {
                val userId = authRepositorio.obtenerUsuarioId()
                val usuario = authRepositorio.obtenerUsuarioInfo()?.nombre ?: "Usuario"
                reviewRepositorio.publicarReview(
                    bookId = libroId,
                    userId = userId,
                    usuario = usuario,
                    puntuacion = puntuacion,
                    texto = texto,
                )
                _uiState.value = CriticaUiState.Publicado
            } catch (e: Exception) {
                _uiState.value = CriticaUiState.Error(
                    mensaje = e.message ?: "Error al publicar la crítica."
                )
            }
        }
    }

    private fun cargarInfo() {
        viewModelScope.launch {
            val entidad = libroGuardadoRepositorio.esMarcado(libroId).first()
            if (entidad != null) {
                _uiState.value = CriticaUiState.Completado(
                    titulo = entidad.titulo,
                    cubiertaUrl = entidad.cubiertaId,
                    autor = entidad.autor,
                    pubFecha = entidad.pubFecha,
                )
                return@launch
            }
            repositorio.obtenerInfoDetalle(libroId, autor)
                .onSuccess { info ->
                    val fecha = if (info.pubFecha == "N/A" && pubFechaPasada.isNotEmpty()) {
                        pubFechaPasada
                    } else {
                        info.pubFecha
                    }
                    _uiState.value = CriticaUiState.Completado(
                        titulo = info.titulo,
                        cubiertaUrl = info.cubiertaId,
                        autor = info.autor,
                        pubFecha = fecha,
                    )
                }
                .onFailure { e ->
                    _uiState.value = CriticaUiState.Error(
                        mensaje = e.message ?: "Error al cargar información."
                    )
                }
        }
    }
}
