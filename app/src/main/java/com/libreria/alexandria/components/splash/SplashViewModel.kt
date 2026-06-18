package com.libreria.alexandria.components.splash

import androidx.lifecycle.ViewModel
import com.libreria.alexandria.components.Screen
import com.libreria.alexandria.data.AuthRepositorio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface SplashUiState {
    data object Cargando : SplashUiState
    data class Navegar(val ruta: String) : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepositorio: AuthRepositorio
) : ViewModel() {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Cargando)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    // Lleva directo al listado de libros si es usuario ya esta
    // registrado, de lo contrario, a la pantalla de login.
    init {
        val ruta = if (authRepositorio.isUsuarioAutenticado()) {
            Screen.BookList.route
        } else {
            Screen.Login.route
        }
        _uiState.value = SplashUiState.Navegar(ruta)
    }
}
