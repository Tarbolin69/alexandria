package com.libreria.alexandria.components.listado

// Aca se maneja el StateFlow de la pantalla y su lógica.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroRepositorio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Los diferentes estados de la pantalla.
class LibrosViewModel(private val repository: LibroRepositorio) : ViewModel() {
    sealed interface LibroEstadoUI {
        data object Cargando : LibroEstadoUI
        data class Completado(val books: List<Libro>) : LibroEstadoUI
        data class Error(val message: String) : LibroEstadoUI
    }

    // Diferencia si el usuario está buscando con la barra de
    // búsqueda o mediante las etiquetas de género.
    private sealed interface ModoBusqueda {
        data class Texto(val query: String) : ModoBusqueda
        data class Genero(val subject: String) : ModoBusqueda
    }

    // Se encarga de controlar que muestra la pantalla
    // dependiendo de su estado actual.
    private val _uiState = MutableStateFlow<LibroEstadoUI>(LibroEstadoUI.Error(""))
    val uiState: StateFlow<LibroEstadoUI> = _uiState.asStateFlow()

    // Es NULL en caso de ninguna etiqueta de género
    // esté seleccionada. Si no, la almacena.
    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    // Variables para manejar la paginacion dentro
    // de la pantalla.
    private var paginaActual = 1
    private var esUltimaPagina = false
    // Lista de todos los libros cargados
    private val librosCompilados = mutableListOf<Libro>()
    private var modoActual: ModoBusqueda? = null

    // Usado cuando el usuario entra en modo "Texto".
    // Limpia la búsqueda previa y reinicia la cuenta
    // de páginas.
    fun buscarLibros(query: String) {
        if (query.isBlank()) return
        modoActual = ModoBusqueda.Texto(query)
        _selectedSubject.value = null
        paginaActual = 1
        esUltimaPagina = false
        librosCompilados.clear()
        ejecutarBusqueda()
    }

    // Lo mismo de la anterior función, pero para
    // el modo "Genero".
    fun buscarPorGenero(subject: String) {
        modoActual = ModoBusqueda.Genero(subject)
        _selectedSubject.value = subject
        paginaActual = 1
        esUltimaPagina = false
        librosCompilados.clear()
        ejecutarBusqueda()
    }

    // Cuando se llega al fin de los datos cargados, se
    // llama de nuevo a la función de búsqueda para que
    // siga el scroll infinito.
    fun cargarSiguientePagina() {
        if (esUltimaPagina || _uiState.value is LibroEstadoUI.Cargando) return
        paginaActual++
        ejecutarBusqueda()
    }

    private fun ejecutarBusqueda() {
        viewModelScope.launch {
            // Pone el uiState como "cargando" mientras se obtiene información.
            _uiState.value = LibroEstadoUI.Cargando
            // Usa la función apropiada de repositorio dependiendo del
            // modo de búsqueda en el que se encuentra el usuario.
            val resultado = when (val modo = modoActual) {
                is ModoBusqueda.Texto -> repository.buscarLibros(modo.query, paginaActual)
                is ModoBusqueda.Genero -> repository.buscarPorGenero(modo.subject, (paginaActual - 1) * 20)
                null -> return@launch
            }
            resultado
                // El uiState se actualiza a "completado" y actualiza el
                // view con más resultados.
                .onSuccess { books ->
                    if (books.isEmpty()) esUltimaPagina = true
                    librosCompilados.addAll(books)
                    _uiState.value = LibroEstadoUI.Completado(librosCompilados.toList())
                }
                // En caso de error, solo emite el mensaje de error.
                .onFailure { e ->
                    _uiState.value = LibroEstadoUI.Error(e.message ?: "Ocurrio un error")
                }
        }
    }
}
