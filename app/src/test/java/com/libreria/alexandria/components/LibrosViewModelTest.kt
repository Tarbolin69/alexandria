package com.libreria.alexandria.components

import com.libreria.alexandria.components.listado.LibroEstadoUI
import com.libreria.alexandria.components.listado.LibroListadoViewModel
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroDetalleInfo
import com.libreria.alexandria.data.LibroRepositorio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibrosViewModelTest {

    @Before
    fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    private val sampleBooks = listOf(
        Libro("OL27448W", "The Lord of the Rings", "J.R.R. Tolkien", "1954", "covers"),
    )

    @Test
    fun `buscarLibros returns Completado with books`() = runTest {
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.success(sampleBooks)
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.success(emptyList())
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.failure(Exception("n/a"))
        }
        val vm = LibroListadoViewModel(repo)
        vm.buscarLibros("Tolkien")
        assertTrue(vm.uiState.value is LibroEstadoUI.Completado)
    }

    @Test
    fun `buscarPorGenero clears query`() = runTest {
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.success(emptyList())
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.success(sampleBooks)
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.failure(Exception("n/a"))
        }
        val vm = LibroListadoViewModel(repo)
        vm.actualizarQuery("text")
        vm.buscarPorGenero("Fantasy")
        assertEquals("Fantasy", vm.selectedSubject.value)
        assertEquals("", vm.query.value)
    }

    @Test
    fun `search failure sets Error`() = runTest {
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("err"))
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.success(emptyList())
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.failure(Exception("n/a"))
        }
        val vm = LibroListadoViewModel(repo)
        vm.buscarLibros("x")
        assertTrue(vm.uiState.value is LibroEstadoUI.Error)
    }

    @Test
    fun `blank query is ignored`() = runTest {
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.success(sampleBooks)
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.success(emptyList())
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.failure(Exception("n/a"))
        }
        val vm = LibroListadoViewModel(repo)
        val initial = vm.uiState.value
        vm.buscarLibros("   ")
        assertEquals(initial, vm.uiState.value)
    }

    @Test
    fun `empty result sets empty Completado`() = runTest {
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.success(emptyList())
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.success(emptyList())
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.failure(Exception("n/a"))
        }
        val vm = LibroListadoViewModel(repo)
        vm.buscarLibros("nope")
        assertTrue((vm.uiState.value as LibroEstadoUI.Completado).books.isEmpty())
    }
}
