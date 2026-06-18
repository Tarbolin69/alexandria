package com.libreria.alexandria.components

import com.libreria.alexandria.components.libreria.LibreriaUiState
import com.libreria.alexandria.components.libreria.LibreriaViewModel
import com.libreria.alexandria.data.DeepseekApiKeyStorage
import com.libreria.alexandria.data.DeepseekService
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.local.LibroGuardadoDao
import com.libreria.alexandria.data.local.LibroGuardadoEntity
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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
class LibreriaViewModelTest {

    private val apiKeyStorage = mockk<DeepseekApiKeyStorage>(relaxed = true)
    private val deepseekService = mockk<DeepseekService>(relaxed = true)

    @Before
    fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `emits Completado with saved books`() = runTest {
        val saved = listOf(LibroGuardadoEntity("OL27448W", "TLOTR", "JRRT", "url", "1954", "desc"))
        val dao = object : LibroGuardadoDao {
            override fun obtenerTodos(): Flow<List<LibroGuardadoEntity>> = flowOf(saved)
            override fun obtenerPorId(bookId: String): Flow<LibroGuardadoEntity?> = flowOf(null)
            override suspend fun insertar(libro: LibroGuardadoEntity) {}
            override suspend fun eliminar(bookId: String) {}
        }
        val repo = object : LibroGuardadoRepositorio(dao) {
            override fun obtenerTodos(): Flow<List<LibroGuardadoEntity>> = flowOf(saved)
        }
        val vm = LibreriaViewModel(repo, apiKeyStorage, deepseekService)
        assertEquals("TLOTR", (vm.uiState.value as LibreriaUiState.Completado).libros[0].titulo)
    }

    @Test
    fun `emits Completado with empty list`() = runTest {
        val dao = object : LibroGuardadoDao {
            override fun obtenerTodos(): Flow<List<LibroGuardadoEntity>> = flowOf(emptyList())
            override fun obtenerPorId(bookId: String): Flow<LibroGuardadoEntity?> = flowOf(null)
            override suspend fun insertar(libro: LibroGuardadoEntity) {}
            override suspend fun eliminar(bookId: String) {}
        }
        val repo = object : LibroGuardadoRepositorio(dao) {
            override fun obtenerTodos(): Flow<List<LibroGuardadoEntity>> = flowOf(emptyList())
        }
        val vm = LibreriaViewModel(repo, apiKeyStorage, deepseekService)
        assertTrue((vm.uiState.value as LibreriaUiState.Completado).libros.isEmpty())
    }
}
