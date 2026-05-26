package com.libreria.alexandria.components.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BooksViewModel(private val repository: LibroRepositorio) : ViewModel() {
    sealed interface LibroEstadoUI {
        data object Cargando : LibroEstadoUI
        data class Completado(val books: List<Libro>) : LibroEstadoUI
        data class Error(val message: String) : LibroEstadoUI
    }

    private val _uiState = MutableStateFlow<LibroEstadoUI>(LibroEstadoUI.Cargando)
    val uiState: StateFlow<LibroEstadoUI> = _uiState.asStateFlow()

    private var paginaActual = 1
    private var esUltimaPagina = false
    private val librosCompilados = mutableListOf<Libro>()

    fun buscarLibros(query: String) {
        viewModelScope.launch {
            _uiState.value = LibroEstadoUI.Cargando
            paginaActual = 1
            esUltimaPagina = false
            librosCompilados.clear()

            repository.buscarLibros(query, paginaActual)
                .onSuccess { books ->
                    if (books.isEmpty()) esUltimaPagina = true
                    librosCompilados.addAll(books)
                    _uiState.value = LibroEstadoUI.Completado(librosCompilados.toList())
                }
                .onFailure { e ->
                    _uiState.value = LibroEstadoUI.Error(e.message ?: "Ocurrio un error")
                }
        }
    }

    fun cargarSiguientePagina(query: String) {
        if (esUltimaPagina || _uiState.value is LibroEstadoUI.Cargando) return
        viewModelScope.launch {
            paginaActual++
            repository.buscarLibros(query, paginaActual)
                .onSuccess { books ->
                    if (books.isEmpty()) esUltimaPagina = true
                    librosCompilados.addAll(books)
                    _uiState.value = LibroEstadoUI.Completado(librosCompilados.toList())
                }
                .onFailure {
                    _uiState.value = LibroEstadoUI.Error(it.message ?: "Ocurrio un error")
                }
        }
    }
}