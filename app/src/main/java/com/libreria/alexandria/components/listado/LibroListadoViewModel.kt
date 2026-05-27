package com.libreria.alexandria.components.listado

// Gestiona la búsqueda y listado paginado de libros
// por la Search/Subject API de Open Library.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroRepositorio
import com.libreria.alexandria.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LibroEstadoUI {
    data object Cargando : LibroEstadoUI
    data class Completado(val books: List<Libro>, val isLoadingMore: Boolean = false) : LibroEstadoUI
    data class Error(val message: String) : LibroEstadoUI
}

class LibrosViewModel(
    // Los datos remotos del Search API
    private val repository: LibroRepositorio = ServiceLocator.libroRepositorio
) : ViewModel() {
    // Los dos modos de búsqueda disponibles
    // en la pantalla: por nombre o género.
    private sealed interface ModoBusqueda {
        data class Texto(val query: String) : ModoBusqueda
        data class Genero(val subject: String) : ModoBusqueda
    }

    private val _uiState = MutableStateFlow<LibroEstadoUI>(LibroEstadoUI.Error(""))
    val uiState: StateFlow<LibroEstadoUI> = _uiState.asStateFlow()

    private val _generoSeleccionado = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _generoSeleccionado.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun actualizarQuery(query: String) {
        _query.value = query
    }

    private var paginaActual = 1
    private var esUltimaPagina = false
    private val librosCompilados = mutableListOf<Libro>()
    private var modoActual: ModoBusqueda? = null


    // Estos dos hacen básicamente lo mismo:
    // resetean la paginación, los libros
    // guardados y borran el elemento de
    // búsqueda anterior, que puede ser la
    // etiqueta de género o texto de búsqueda.
    //
    // Seguido, ejecutan la búsqueda de libros.

    fun buscarLibros(query: String) {
        if (query.isBlank()) return
        modoActual = ModoBusqueda.Texto(query)
        _generoSeleccionado.value = null
        paginaActual = 1
        esUltimaPagina = false
        librosCompilados.clear()
        ejecutarBusqueda()
    }

    fun buscarPorGenero(subject: String) {
        modoActual = ModoBusqueda.Genero(subject)
        _generoSeleccionado.value = subject
        _query.value = ""
        paginaActual = 1
        esUltimaPagina = false
        librosCompilados.clear()
        ejecutarBusqueda()
    }

    fun cargarSiguientePagina() {
        if (esUltimaPagina) return
        val estadoActual = _uiState.value
        if (estadoActual is LibroEstadoUI.Completado && estadoActual.isLoadingMore) return
        paginaActual++
        ejecutarBusqueda()
    }

    private fun ejecutarBusqueda() {
        viewModelScope.launch {
            val esCargaInicial = paginaActual == 1
            if (esCargaInicial) {
                _uiState.value = LibroEstadoUI.Cargando
            } else {
                val estadoActual = _uiState.value
                if (estadoActual is LibroEstadoUI.Completado) {
                    _uiState.value = estadoActual.copy(isLoadingMore = true)
                }
            }
            val resultado = when (val modo = modoActual) {
                is ModoBusqueda.Texto -> repository.buscarLibros(modo.query, paginaActual)
                // A diferencia de Search API, Subject API usa offset en vez de page
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
                    if (esCargaInicial) {
                        _uiState.value = LibroEstadoUI.Error(e.message ?: "Ocurrió un error")
                    } else {
                        _uiState.value = LibroEstadoUI.Completado(librosCompilados.toList())
                    }
                }
        }
    }
}
