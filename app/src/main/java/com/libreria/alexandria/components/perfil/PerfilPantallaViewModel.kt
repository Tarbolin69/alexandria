package com.libreria.alexandria.components.perfil

import androidx.lifecycle.ViewModel
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.PerfilUsuarioInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PerfilUiState(
    val usuarioInfo: PerfilUsuarioInfo? = null,
    val acercaDeMi: String = "",
)

@HiltViewModel
class PerfilPantallaViewModel @Inject constructor(
    authRepositorio: AuthRepositorio
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        val info = authRepositorio.obtenerUsuarioInfo()
        _uiState.value = PerfilUiState(usuarioInfo = info)
    }

    fun actualizarAcercaDeMi(texto: String) {
        _uiState.update { it.copy(acercaDeMi = texto) }
    }
}
