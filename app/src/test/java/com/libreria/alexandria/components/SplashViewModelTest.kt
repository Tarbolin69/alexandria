package com.libreria.alexandria.components

import com.libreria.alexandria.components.splash.SplashUiState
import com.libreria.alexandria.components.splash.SplashViewModel
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.PerfilUsuarioInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    @Test
    fun `authenticated user navigates to BookList`() = runTest {
        val auth = object : AuthRepositorio {
            override fun isUsuarioAutenticado() = true
            override suspend fun iniciarSesionConGoogle(idToken: String) = Result.success(Unit)
            override fun cerrarSesion() {}
            override fun obtenerUsuarioInfo() = PerfilUsuarioInfo("User", "u@mail.com", null)
            override fun obtenerUsuarioId() = "123"
        }
        val vm = SplashViewModel(auth)
        val state = vm.uiState.value
        assertTrue(state is SplashUiState.Navegar)
        assertEquals(Screen.BookList.route, (state as SplashUiState.Navegar).ruta)
    }

    @Test
    fun `unauthenticated user navigates to Login`() = runTest {
        val auth = object : AuthRepositorio {
            override fun isUsuarioAutenticado() = false
            override suspend fun iniciarSesionConGoogle(idToken: String) = Result.success(Unit)
            override fun cerrarSesion() {}
            override fun obtenerUsuarioInfo() = null
            override fun obtenerUsuarioId() = ""
        }
        val vm = SplashViewModel(auth)
        val state = vm.uiState.value
        assertTrue(state is SplashUiState.Navegar)
        assertEquals(Screen.Login.route, (state as SplashUiState.Navegar).ruta)
    }
}
