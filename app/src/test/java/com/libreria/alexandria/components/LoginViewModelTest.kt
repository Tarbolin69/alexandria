package com.libreria.alexandria.components

import com.libreria.alexandria.components.login.LoginEstadoUI
import com.libreria.alexandria.components.login.LoginViewModel
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.PerfilUsuarioInfo
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
class LoginViewModelTest {

    @Before
    fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }

    @After
    fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `successful sign in transitions to Autenticado`() = runTest {
        val auth = object : AuthRepositorio {
            override fun isUsuarioAutenticado() = false
            override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> = Result.success(Unit)
            override fun cerrarSesion() {}
            override fun obtenerUsuarioInfo(): PerfilUsuarioInfo? = null
            override fun obtenerUsuarioId() = ""
        }
        val vm = LoginViewModel(auth)
        vm.autenticarConGoogle("token")
        assertTrue(vm.uiState.value is LoginEstadoUI.Autenticado)
    }

    @Test
    fun `failed sign in transitions to Error`() = runTest {
        val auth = object : AuthRepositorio {
            override fun isUsuarioAutenticado() = false
            override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> = Result.failure(Exception("fail"))
            override fun cerrarSesion() {}
            override fun obtenerUsuarioInfo(): PerfilUsuarioInfo? = null
            override fun obtenerUsuarioId() = ""
        }
        val vm = LoginViewModel(auth)
        vm.autenticarConGoogle("x")
        assertTrue(vm.uiState.value is LoginEstadoUI.Error)
    }

    @Test
    fun `establecerError sets Error state`() = runTest {
        val auth = object : AuthRepositorio {
            override fun isUsuarioAutenticado() = false
            override suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> = Result.success(Unit)
            override fun cerrarSesion() {}
            override fun obtenerUsuarioInfo(): PerfilUsuarioInfo? = null
            override fun obtenerUsuarioId() = ""
        }
        val vm = LoginViewModel(auth)
        vm.establecerError("oops")
        assertEquals("oops", (vm.uiState.value as LoginEstadoUI.Error).mensaje)
    }
}
