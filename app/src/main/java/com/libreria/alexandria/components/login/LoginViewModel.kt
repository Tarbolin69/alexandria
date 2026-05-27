package com.libreria.alexandria.components.login

// Viewmodel de Login y Signup con Google usando Firebase Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface LoginEstadoUI {
    data object Inicial : LoginEstadoUI
    data object Cargando : LoginEstadoUI
    data class Error(val mensaje: String) : LoginEstadoUI
    data object Autenticado : LoginEstadoUI
}

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<LoginEstadoUI>(LoginEstadoUI.Inicial)
    val uiState: StateFlow<LoginEstadoUI> = _uiState.asStateFlow()

    fun establecerError(mensaje: String) {
        _uiState.value = LoginEstadoUI.Error(mensaje)
    }

    fun autenticarConGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginEstadoUI.Cargando
            try {
                val credencial = GoogleAuthProvider.getCredential(idToken, null)
                withContext(Dispatchers.IO) {
                    Tasks.await(auth.signInWithCredential(credencial))
                }
                _uiState.value = LoginEstadoUI.Autenticado
            } catch (e: Exception) {
                _uiState.value = LoginEstadoUI.Error(
                    e.message ?: "Error al iniciar sesión con Google"
                )
            }
        }
    }
}
