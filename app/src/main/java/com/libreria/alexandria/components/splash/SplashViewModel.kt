package com.libreria.alexandria.components.splash

import androidx.lifecycle.ViewModel
import com.libreria.alexandria.components.Screen
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface SplashUiState {
    data object Cargando : SplashUiState
    data class Navegar(val ruta: String) : SplashUiState
}

// Si el usuario ya está registrado y logueado, no hay razón
// para llevarlo a la página de login. Lo lleva directamente
// al listado de libros.
class SplashViewModel(
    authRepositorio: AuthRepositorio = ServiceLocator.authRepositorio
) : ViewModel() {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Cargando)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        val ruta = if (authRepositorio.isUsuarioAutenticado()) {
            Screen.BookList.route
        } else {
            Screen.Login.route
        }
        _uiState.value = SplashUiState.Navegar(ruta)
    }
}
