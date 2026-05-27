package com.libreria.alexandria.components.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.LibroRepositorio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    ) : LibroDetalleUiState
}

@HiltViewModel
class LibroDetalleViewModel @Inject constructor(
    private val repositorio: LibroRepositorio,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val libroId: String = savedStateHandle["bookId"] ?: ""
    private val autor: String = savedStateHandle["autor"] ?: ""

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
