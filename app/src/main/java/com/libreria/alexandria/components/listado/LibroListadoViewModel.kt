package com.libreria.alexandria.components.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibrosViewModel(private val repository: LibroRepositorio) : ViewModel() {
    sealed interface LibroEstadoUI {
        data object Cargando : LibroEstadoUI
        data class Completado(val books: List<Libro>) : LibroEstadoUI
        data class Error(val message: String) : LibroEstadoUI
    }

    private sealed interface ModoBusqueda {
        data class Texto(val query: String) : ModoBusqueda
        data class Genero(val subject: String) : ModoBusqueda
    }

    private val _uiState = MutableStateFlow<LibroEstadoUI>(LibroEstadoUI.Error(""))
    val uiState: StateFlow<LibroEstadoUI> = _uiState.asStateFlow()

    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    private var paginaActual = 1
    private var esUltimaPagina = false
    private val librosCompilados = mutableListOf<Libro>()
    private var modoActual: ModoBusqueda? = null

    fun buscarLibros(query: String) {
        if (query.isBlank()) return
        modoActual = ModoBusqueda.Texto(query)
        _selectedSubject.value = null
        paginaActual = 1
        esUltimaPagina = false
        librosCompilados.clear()
        ejecutarBusqueda()
    }

    fun buscarPorGenero(subject: String) {
        modoActual = ModoBusqueda.Genero(subject)
        _selectedSubject.value = subject
        paginaActual = 1
        esUltimaPagina = false
        librosCompilados.clear()
        ejecutarBusqueda()
    }

    fun cargarSiguientePagina() {
        if (esUltimaPagina || _uiState.value is LibroEstadoUI.Cargando) return
        paginaActual++
        ejecutarBusqueda()
    }

    private fun ejecutarBusqueda() {
        viewModelScope.launch {
            _uiState.value = LibroEstadoUI.Cargando
            val resultado = when (val modo = modoActual) {
                is ModoBusqueda.Texto -> repository.buscarLibros(modo.query, paginaActual)
                is ModoBusqueda.Genero -> repository.buscarPorGenero(modo.subject, (paginaActual - 1) * 20)
                null -> return@launch
            }
            resultado
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
}
