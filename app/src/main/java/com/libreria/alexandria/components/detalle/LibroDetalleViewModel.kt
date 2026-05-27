package com.libreria.alexandria.components.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.LibroRepositorio
import com.libreria.alexandria.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LibroDetalleUiState {
    data class Cargando(val autor: String) : LibroDetalleUiState
    data class Error(val mensaje: String) : LibroDetalleUiState
    data class Completado(
        val titulo: String,
        val autor: String,
        val cubiertaUrl: String,
        val pubFecha: String,
        val descripcion: String,
    ) : LibroDetalleUiState
}

class LibroDetalleViewModel(
    private val repositorio: LibroRepositorio = ServiceLocator.libroRepositorio,
    private val libroId: String,
    private val autor: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibroDetalleUiState>(
        LibroDetalleUiState.Cargando(autor = autor)
    )
    val uiState: StateFlow<LibroDetalleUiState> = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    private fun cargarDetalle() {
        viewModelScope.launch {
            _uiState.value = LibroDetalleUiState.Cargando(autor = autor)
            repositorio.obtenerInfoDetalle(libroId, autor)
                .onSuccess { info ->
                    _uiState.value = LibroDetalleUiState.Completado(
                        titulo = info.titulo,
                        autor = info.autor,
                        cubiertaUrl = info.cubiertaId,
                        pubFecha = info.pubFecha,
                        descripcion = info.descripcion,
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
