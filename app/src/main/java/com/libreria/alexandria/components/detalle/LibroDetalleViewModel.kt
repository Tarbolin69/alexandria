package com.libreria.alexandria.components.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.LibroRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Un "state-holder" básico para
// el esqueleto del libro.
data class LibroDetalleUiState(
    val titulo: String = "",
    val autor: String = "",
    val cubiertaUrl: String = "",
    val pubFecha: String = "",
    val descripcion: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
)

// Expone 1 libro y sus detalles.
class LibroDetalleViewModel(
    private val repositorio: LibroRepositorio,
    private val libroId: String,
    private val autor: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LibroDetalleUiState(autor = autor, isLoading = true)
    )
    val uiState: StateFlow<LibroDetalleUiState> = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    // Agarra errors al intentar cargar los detalles del
    // libro. Si obtiene buen los resultados, los llena
    // el state-holder de la UI
    private fun cargarDetalle() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repositorio.obtenerInfoDetalle(libroId, autor)
                .onSuccess { info ->
                    _uiState.value = LibroDetalleUiState(
                        titulo = info.titulo,
                        autor = info.autor,
                        cubiertaUrl = info.cubiertaId,
                        pubFecha = info.pubFecha,
                        descripcion = info.descripcion,
                        isLoading = false,
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar información.",
                    )
                }
        }
    }
}
