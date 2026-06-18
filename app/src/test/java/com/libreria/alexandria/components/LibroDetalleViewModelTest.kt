package com.libreria.alexandria.components

import androidx.lifecycle.SavedStateHandle
import com.libreria.alexandria.components.detalle.LibroDetalleUiState
import com.libreria.alexandria.components.detalle.LibroDetalleViewModel
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroDetalleInfo
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.LibroRepositorio
import com.libreria.alexandria.data.PerfilUsuarioInfo
import com.libreria.alexandria.data.Review
import com.libreria.alexandria.data.ReviewRepositorio
import com.libreria.alexandria.data.local.LibroGuardadoDao
import com.libreria.alexandria.data.local.LibroGuardadoEntity
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibroDetalleViewModelTest {

    @Before
    fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    private val sampleInfo = LibroDetalleInfo(
        titulo = "The Lord of the Rings", autor = "J.R.R. Tolkien",
        cubiertaId = "url", pubFecha = "1954", descripcion = "Epic.",
    )
    private val firestoreMock = mockk<FirebaseFirestore>(relaxed = true)

    @Test
    fun `loads from API when not saved`() = runTest {
        val guardado = guardadoRepo(MutableStateFlow(null))
        val review = reviewRepo()
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.success(sampleInfo)
        }
        val h = SavedStateHandle(mapOf("bookId" to "OL27448W", "autor" to "J.R.R. Tolkien"))
        val vm = LibroDetalleViewModel(repo, guardado, review, auth(), h)
        assertEquals("The Lord of the Rings", (vm.uiState.value as LibroDetalleUiState.Completado).titulo)
    }

    @Test
    fun `loads from Room when bookmarked`() = runTest {
        val e = LibroGuardadoEntity("OL27448W", "The Lord of the Rings", "J.R.R. Tolkien", "url", "1954", "desc")
        val guardado = guardadoRepo(MutableStateFlow(e))
        val review = reviewRepo()
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.failure(Exception("nope"))
        }
        val h = SavedStateHandle(mapOf("bookId" to "OL27448W", "autor" to "J.R.R. Tolkien"))
        val vm = LibroDetalleViewModel(repo, guardado, review, auth(), h)
        val s = vm.uiState.value as LibroDetalleUiState.Completado
        assertEquals("The Lord of the Rings", s.titulo)
        assertTrue(s.esMarcado)
    }

    @Test
    fun `esMarcado false when not bookmarked`() = runTest {
        val guardado = guardadoRepo(MutableStateFlow(null))
        val review = reviewRepo()
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.success(sampleInfo)
        }
        val h = SavedStateHandle(mapOf("bookId" to "OL27448W", "autor" to "J.R.R. Tolkien"))
        val vm = LibroDetalleViewModel(repo, guardado, review, auth(), h)
        assertFalse(vm.esMarcado.value)
    }

    @Test
    fun `yaCalificado true when user reviewed`() = runTest {
        val guardado = guardadoRepo(MutableStateFlow(null))
        val review = object : ReviewRepositorio(firestoreMock) {
            override fun yaCalificado(bookId: String, userId: String): Flow<Boolean> = flowOf(true)
            override fun obtenerReviews(bookId: String): Flow<List<Review>> = flowOf(emptyList())
        }
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.success(sampleInfo)
        }
        val h = SavedStateHandle(mapOf("bookId" to "OL27448W", "autor" to "J.R.R. Tolkien"))
        val vm = LibroDetalleViewModel(repo, guardado, review, auth("user1"), h)
        assertTrue(vm.yaCalificado.value)
    }

    @Test
    fun `API failure sets Error`() = runTest {
        val guardado = guardadoRepo(MutableStateFlow(null))
        val review = reviewRepo()
        val repo = object : LibroRepositorio {
            override suspend fun buscarLibros(query: String, page: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun buscarPorGenero(subject: String, offset: Int, limit: Int): Result<List<Libro>> = Result.failure(Exception("n/a"))
            override suspend fun obtenerInfoDetalle(libroId: String, autor: String): Result<LibroDetalleInfo> = Result.failure(Exception("API down"))
        }
        val h = SavedStateHandle(mapOf("bookId" to "OL27448W", "autor" to "J.R.R. Tolkien"))
        val vm = LibroDetalleViewModel(repo, guardado, review, auth(), h)
        assertTrue(vm.uiState.value is LibroDetalleUiState.Error)
    }

    private fun auth(id: String = ""): AuthRepositorio = object : AuthRepositorio {
        override fun isUsuarioAutenticado() = id.isNotEmpty()
        override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> = Result.success(Unit)
        override fun cerrarSesion() {}
        override fun obtenerUsuarioInfo(): PerfilUsuarioInfo? = PerfilUsuarioInfo("U", "u@m", null)
        override fun obtenerUsuarioId() = id
    }

    private fun reviewRepo() = object : ReviewRepositorio(firestoreMock) {
        override fun yaCalificado(bookId: String, userId: String): Flow<Boolean> = flowOf(false)
        override fun obtenerReviews(bookId: String): Flow<List<Review>> = flowOf(emptyList())
    }

    private fun guardadoRepo(flow: MutableStateFlow<LibroGuardadoEntity?>): LibroGuardadoRepositorio {
        val dao = object : LibroGuardadoDao {
            override fun obtenerTodos(): Flow<List<LibroGuardadoEntity>> = flowOf(emptyList())
            override fun obtenerPorId(bookId: String): Flow<LibroGuardadoEntity?> = flow
            override suspend fun insertar(libro: LibroGuardadoEntity) {}
            override suspend fun eliminar(bookId: String) {}
        }
        return object : LibroGuardadoRepositorio(dao) {
            override fun esMarcado(bookId: String): Flow<LibroGuardadoEntity?> = flow.asStateFlow()
        }
    }
}
