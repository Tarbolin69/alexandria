package com.libreria.alexandria.components

import androidx.lifecycle.SavedStateHandle
import com.libreria.alexandria.components.critica.CriticaUiState
import com.libreria.alexandria.components.critica.CriticaViewModel
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroDetalleInfo
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.LibroRepositorio
import com.libreria.alexandria.data.PerfilUsuarioInfo
import com.libreria.alexandria.data.ReviewRepositorio
import com.libreria.alexandria.data.local.LibroGuardadoDao
import com.libreria.alexandria.data.local.LibroGuardadoEntity
import com.google.firebase.firestore.FirebaseFirestore
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CriticaViewModelTest {

    @Before
    fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    private val sampleInfo = LibroDetalleInfo("TLOTR", "JRRT", "url", "1954", "desc")
    private val firestoreMock = mockk<FirebaseFirestore>(relaxed = true)
    private val handle = SavedStateHandle(mapOf("bookId" to "OL27448W", "autor" to "JRRT"))

    @Test
    fun `publicar transitions to Publicado`() = runTest {
        val repo = fakeRepo()
        val vm = CriticaViewModel(repo, fakeGuardado(), fakeAuth(), fakeReview(), handle)
        assertTrue(vm.uiState.value is CriticaUiState.Completado)
        vm.publicar(5, "Great!")
        assertTrue(vm.uiState.value is CriticaUiState.Publicado)
    }

    @Test
    fun `zero stars is ignored`() = runTest {
        val vm = CriticaViewModel(fakeRepo(), fakeGuardado(), fakeAuth(), fakeReview(), handle)
        assertTrue(vm.uiState.value is CriticaUiState.Completado)
        vm.publicar(0, "text")
        assertTrue(vm.uiState.value is CriticaUiState.Completado)
    }

    @Test
    fun `blank text is ignored`() = runTest {
        val vm = CriticaViewModel(fakeRepo(), fakeGuardado(), fakeAuth(), fakeReview(), handle)
        assertTrue(vm.uiState.value is CriticaUiState.Completado)
        vm.publicar(3, "  ")
        assertTrue(vm.uiState.value is CriticaUiState.Completado)
    }

    @Test
    fun `publish failure transitions to Error`() = runTest {
        val review = object : ReviewRepositorio(firestoreMock) {
            override suspend fun publicarReview(bookId: String, userId: String, usuario: String, puntuacion: Int, texto: String) {
                throw Exception("offline")
            }
        }
        val vm = CriticaViewModel(fakeRepo(), fakeGuardado(), fakeAuth(), review, handle)
        assertTrue(vm.uiState.value is CriticaUiState.Completado)
        vm.publicar(4, "ok")
        assertTrue(vm.uiState.value is CriticaUiState.Error)
    }

    private fun fakeRepo() = object : LibroRepositorio {
        override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
        override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
        override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.success(sampleInfo)
    }

    private fun fakeAuth() = object : AuthRepositorio {
        override fun isUsuarioAutenticado() = true
        override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> = Result.success(Unit)
        override fun cerrarSesion() {}
        override fun obtenerUsuarioInfo(): PerfilUsuarioInfo? = PerfilUsuarioInfo("U", "u@m", null)
        override fun obtenerUsuarioId() = "user1"
    }

    private fun fakeGuardado() = object : LibroGuardadoRepositorio(
        object : LibroGuardadoDao {
            override fun obtenerTodos(): Flow<List<LibroGuardadoEntity>> = flowOf(emptyList())
            override fun obtenerPorId(bookId: String): Flow<LibroGuardadoEntity?> = flowOf(null)
            override suspend fun insertar(libro: LibroGuardadoEntity) {}
            override suspend fun eliminar(bookId: String) {}
        }
    ) {}

    private fun fakeReview() = object : ReviewRepositorio(firestoreMock) {
        override suspend fun publicarReview(bookId: String, userId: String, usuario: String, puntuacion: Int, texto: String) {}
    }
}
