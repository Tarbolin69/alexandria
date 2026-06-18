package com.libreria.alexandria.components.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.PerfilFirebaseRepositorio
import com.libreria.alexandria.data.PerfilRepositorio
import com.libreria.alexandria.data.PerfilUsuarioInfo
import com.libreria.alexandria.data.local.PerfilEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerfilUiState(
    val usuarioInfo: PerfilUsuarioInfo? = null,
    val acercaDeMi: String = "",
    val telefono: String = "",
    val sitioWeb: String = "",
    val email: String = "",
    val estaEditando: Boolean = false,
    val userId: String = "",
)

@HiltViewModel
class PerfilPantallaViewModel @Inject constructor(
    authRepositorio: AuthRepositorio,
    private val perfilRepositorio: PerfilRepositorio,
    private val perfilFirebaseRepositorio: PerfilFirebaseRepositorio
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        val info = authRepositorio.obtenerUsuarioInfo()
        val userId = authRepositorio.obtenerUsuarioId()
        _uiState.value = PerfilUiState(
            usuarioInfo = info,
            userId = userId
        )

        viewModelScope.launch {
            try {
                perfilFirebaseRepositorio.inicializarSiNoExiste(
                    userId = userId,
                    nombre = info?.nombre ?: "",
                    email = info?.email ?: ""
                )

                perfilFirebaseRepositorio.obtenerPerfil(userId).collect { perfil ->
                    if (perfil != null) {
                        _uiState.update {
                            it.copy(
                                acercaDeMi = perfil.acercaDeMi,
                                telefono = perfil.telefono,
                                sitioWeb = perfil.sitioWeb,
                                email = perfil.email
                            )
                        }
                        perfilRepositorio.guardarPerfil(perfil)
                    }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (_: Exception) {
                perfilRepositorio.obtenerPerfil(userId).collect { perfil ->
                    if (perfil != null) {
                        _uiState.update {
                            it.copy(
                                acercaDeMi = perfil.acercaDeMi,
                                telefono = perfil.telefono,
                                sitioWeb = perfil.sitioWeb,
                                email = perfil.email
                            )
                        }
                    }
                }
            }
        }
    }

    fun iniciarEdicion() {
        _uiState.update { it.copy(estaEditando = true) }
    }

    fun guardar() {
        val state = _uiState.value
        viewModelScope.launch {
            val perfil = PerfilEntity(
                userId = state.userId,
                nombre = state.usuarioInfo?.nombre ?: "",
                email = state.email,
                acercaDeMi = state.acercaDeMi,
                telefono = state.telefono,
                sitioWeb = state.sitioWeb
            )
            perfilFirebaseRepositorio.guardarPerfil(perfil)
            perfilRepositorio.guardarPerfil(perfil)
            _uiState.update { it.copy(estaEditando = false) }
        }
    }

    fun cancelarEdicion() {
        _uiState.update { it.copy(estaEditando = false) }
    }

    fun actualizarAcercaDeMi(texto: String) {
        _uiState.update { it.copy(acercaDeMi = texto) }
    }

    fun actualizarEmail(texto: String) {
        _uiState.update { it.copy(email = texto) }
    }

    fun actualizarTelefono(texto: String) {
        _uiState.update { it.copy(telefono = texto) }
    }

    fun actualizarSitioWeb(texto: String) {
        _uiState.update { it.copy(sitioWeb = texto) }
    }
}
