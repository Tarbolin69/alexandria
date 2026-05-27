package com.libreria.alexandria.components.login

// Maneja la autenticación por medio de Google.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginEstadoUI {
    data object Inicial : LoginEstadoUI
    data object Cargando : LoginEstadoUI
    data class Error(val mensaje: String) : LoginEstadoUI
    data object Autenticado : LoginEstadoUI
}

class LoginViewModel(
    private val authRepositorio: AuthRepositorio = ServiceLocator.authRepositorio
) : ViewModel() {
    private val _uiState = MutableStateFlow<LoginEstadoUI>(LoginEstadoUI.Inicial)
    val uiState: StateFlow<LoginEstadoUI> = _uiState.asStateFlow()

    fun establecerError(mensaje: String) {
        _uiState.value = LoginEstadoUI.Error(mensaje)
    }

    fun autenticarConGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginEstadoUI.Cargando
            authRepositorio.iniciarSesionConGoogle(idToken)
                .onSuccess { _uiState.value = LoginEstadoUI.Autenticado }
                .onFailure { e ->
                    _uiState.value = LoginEstadoUI.Error(
                        e.message ?: "Error al iniciar sesión con Google"
                    )
                }
        }
    }
}
