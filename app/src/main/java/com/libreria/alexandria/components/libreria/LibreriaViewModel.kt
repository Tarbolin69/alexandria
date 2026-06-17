package com.libreria.alexandria.components.libreria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.local.LibroGuardadoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibreriaUiState {
    data object Cargando : LibreriaUiState
    data class Completado(val libros: List<LibroGuardadoEntity>) : LibreriaUiState
}

@HiltViewModel
class LibreriaViewModel @Inject constructor(
    private val libroGuardadoRepositorio: LibroGuardadoRepositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibreriaUiState>(LibreriaUiState.Cargando)
    val uiState: StateFlow<LibreriaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            libroGuardadoRepositorio.obtenerTodos().collect { libros ->
                _uiState.value = LibreriaUiState.Completado(libros)
            }
        }
    }
}
