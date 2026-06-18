package com.libreria.alexandria.components

import com.libreria.alexandria.components.perfil.PerfilPantallaViewModel
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.PerfilFirebaseRepositorio
import com.libreria.alexandria.data.PerfilRepositorio
import com.libreria.alexandria.data.PerfilUsuarioInfo
import com.libreria.alexandria.data.local.PerfilDao
import com.libreria.alexandria.data.local.PerfilEntity
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class PerfilPantallaViewModelTest {

    @Before
    fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    private val firestoreMock = mockk<FirebaseFirestore>(relaxed = true)

    private fun createViewModel() = PerfilPantallaViewModel(
        object : AuthRepositorio {
            override fun isUsuarioAutenticado() = true
            override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> = Result.success(Unit)
            override fun cerrarSesion() {}
            override fun obtenerUsuarioInfo(): PerfilUsuarioInfo? = PerfilUsuarioInfo("John", "j@m.com", null)
            override fun obtenerUsuarioId() = "user1"
        },
        object : PerfilRepositorio(
            object : PerfilDao {
                override fun obtenerPerfil(userId: String) = flowOf(null)
                override suspend fun guardarPerfil(perfil: PerfilEntity) {}
            }
        ) {},
        object : PerfilFirebaseRepositorio(firestoreMock) {
            override fun obtenerPerfil(userId: String) = flowOf(null)
            override suspend fun guardarPerfil(perfil: PerfilEntity) {}
            override suspend fun inicializarSiNoExiste(userId: String, nombre: String, email: String) {}
        },
    )

    @Test
    fun `init loads user info`() = runTest {
        val vm = createViewModel()
        assertEquals("John", vm.uiState.value.usuarioInfo?.nombre)
        assertEquals("user1", vm.uiState.value.userId)
    }

    @Test
    fun `iniciarEdicion sets estaEditando true`() = runTest {
        val vm = createViewModel()
        assertFalse(vm.uiState.value.estaEditando)
        vm.iniciarEdicion()
        assertTrue(vm.uiState.value.estaEditando)
    }

    @Test
    fun `cancelarEdicion sets estaEditando false`() = runTest {
        val vm = createViewModel()
        vm.iniciarEdicion()
        vm.cancelarEdicion()
        assertFalse(vm.uiState.value.estaEditando)
    }

    @Test
    fun `actualizarAcercaDeMi updates state`() = runTest {
        val vm = createViewModel()
        vm.actualizarAcercaDeMi("I love books")
        assertEquals("I love books", vm.uiState.value.acercaDeMi)
    }

    @Test
    fun `guardar sets estaEditando false`() = runTest {
        val vm = createViewModel()
        vm.iniciarEdicion()
        assertTrue(vm.uiState.value.estaEditando)
        vm.guardar()
        assertFalse(vm.uiState.value.estaEditando)
    }
}
