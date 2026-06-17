package com.libreria.alexandria.components.resena

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.LibroRepositorio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ResenaUiState {
    data object Cargando : ResenaUiState
    data class Completado(
        val cubiertaUrl: String,
        val autor: String,
        val pubFecha: String,
    ) : ResenaUiState
    data class Error(val mensaje: String) : ResenaUiState
}

@HiltViewModel
class ResenaViewModel @Inject constructor(
    private val repositorio: LibroRepositorio,
    private val libroGuardadoRepositorio: LibroGuardadoRepositorio,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val libroId: String = savedStateHandle["bookId"] ?: ""
    private val autor: String = savedStateHandle["autor"] ?: ""
    private val pubFechaPasada: String = savedStateHandle.get<String>("pubFecha") ?: ""

    private val _uiState = MutableStateFlow<ResenaUiState>(ResenaUiState.Cargando)
    val uiState: StateFlow<ResenaUiState> = _uiState.asStateFlow()

    init {
        cargarInfo()
    }

    private fun cargarInfo() {
        viewModelScope.launch {
            val entidad = libroGuardadoRepositorio.esMarcado(libroId).first()
            if (entidad != null) {
                _uiState.value = ResenaUiState.Completado(
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
                    _uiState.value = ResenaUiState.Completado(
                        cubiertaUrl = info.cubiertaId,
                        autor = info.autor,
                        pubFecha = fecha,
                    )
                }
                .onFailure { e ->
                    _uiState.value = ResenaUiState.Error(
                        mensaje = e.message ?: "Error al cargar información."
                    )
                }
        }
    }
}
