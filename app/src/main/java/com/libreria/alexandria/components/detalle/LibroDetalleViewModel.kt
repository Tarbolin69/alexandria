package com.libreria.alexandria.components.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.LibroRepositorio
import com.libreria.alexandria.data.Review
import com.libreria.alexandria.data.ReviewRepositorio
import com.libreria.alexandria.data.local.LibroGuardadoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibroDetalleUiState {
    data class Cargando(val autor: String) : LibroDetalleUiState
    data class Error(val mensaje: String) : LibroDetalleUiState
    data class Completado(
        val titulo: String,
        val autor: String,
        val cubiertaUrl: String,
        val pubFecha: String,
        val descripcion: String,
        val esMarcado: Boolean,
    ) : LibroDetalleUiState
}

@HiltViewModel
class LibroDetalleViewModel @Inject constructor(
    private val repositorio: LibroRepositorio,
    private val libroGuardadoRepositorio: LibroGuardadoRepositorio,
    private val reviewRepositorio: ReviewRepositorio,
    private val authRepositorio: AuthRepositorio,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val libroId: String = savedStateHandle["bookId"] ?: ""
    val autor: String = savedStateHandle["autor"] ?: ""
    private val pubFechaPasada: String = savedStateHandle.get<String>("pubFecha") ?: ""

    private val _esMarcado = MutableStateFlow(false)
    val esMarcado: StateFlow<Boolean> = _esMarcado.asStateFlow()

    private val _uiState = MutableStateFlow<LibroDetalleUiState>(
        LibroDetalleUiState.Cargando(autor = autor)
    )
    val uiState: StateFlow<LibroDetalleUiState> = _uiState.asStateFlow()

    private val _criticas = MutableStateFlow<List<Review>>(emptyList())
    val criticas: StateFlow<List<Review>> = _criticas.asStateFlow()

    private val _yaCalificado = MutableStateFlow(false)
    val yaCalificado: StateFlow<Boolean> = _yaCalificado.asStateFlow()

    init {
        observarMarcador()
        observarCriticas()
        observarYaCalificado()
        cargarDetalle()
    }

    private fun observarYaCalificado() {
        val userId = authRepositorio.obtenerUsuarioId()
        if (userId.isEmpty()) {
            _yaCalificado.value = false
            return
        }
        viewModelScope.launch {
            reviewRepositorio.yaCalificado(libroId, userId).collect { calificado ->
                _yaCalificado.value = calificado
            }
        }
    }

    private fun observarMarcador() {
        viewModelScope.launch {
            libroGuardadoRepositorio.esMarcado(libroId).collect { entidad ->
                _esMarcado.value = entidad != null
            }
        }
    }

    private fun observarCriticas() {
        viewModelScope.launch {
            reviewRepositorio.obtenerReviews(libroId).collect { reviews ->
                _criticas.value = reviews
            }
        }
    }

    fun alternarMarcador() {
        viewModelScope.launch {
            val estadoActual = _uiState.value
            if (estadoActual is LibroDetalleUiState.Completado) {
                if (_esMarcado.value) {
                    libroGuardadoRepositorio.desmarcar(libroId)
                } else {
                    libroGuardadoRepositorio.marcar(
                        LibroGuardadoEntity(
                            bookId = libroId,
                            titulo = estadoActual.titulo,
                            autor = estadoActual.autor,
                            cubiertaId = estadoActual.cubiertaUrl,
                            pubFecha = estadoActual.pubFecha,
                            descripcion = estadoActual.descripcion,
                        )
                    )
                }
            }
        }
    }

    private fun cargarDetalle() {
        viewModelScope.launch {
            val entidad = libroGuardadoRepositorio.esMarcado(libroId).first()
            if (entidad != null) {
                _uiState.value = LibroDetalleUiState.Completado(
                    titulo = entidad.titulo,
                    autor = entidad.autor,
                    cubiertaUrl = entidad.cubiertaId,
                    pubFecha = entidad.pubFecha,
                    descripcion = entidad.descripcion,
                    esMarcado = true,
                )
                return@launch
            }
            _uiState.value = LibroDetalleUiState.Cargando(autor = autor)
            repositorio.obtenerInfoDetalle(libroId, autor)
                .onSuccess { info ->
                    val fecha = if (info.pubFecha == "N/A" && pubFechaPasada.isNotEmpty()) {
                        pubFechaPasada
                    } else {
                        info.pubFecha
                    }
                    _uiState.value = LibroDetalleUiState.Completado(
                        titulo = info.titulo,
                        autor = info.autor,
                        cubiertaUrl = info.cubiertaId,
                        pubFecha = fecha,
                        descripcion = info.descripcion,
                        esMarcado = false,
                    )
                }
                .onFailure { e ->
                    _uiState.value = LibroDetalleUiState.Error(
                        mensaje = e.message ?: "Error al cargar información."
                    )
                }
        }
    }
}
