package com.libreria.alexandria.components.libreria

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.libreria.alexandria.data.DeepseekApiKeyStorage
import com.libreria.alexandria.data.DeepseekService
import com.libreria.alexandria.data.LibroGuardadoRepositorio
import com.libreria.alexandria.data.local.LibroGuardadoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LibreriaUiState {
    data object Cargando : LibreriaUiState
    data class Completado(val libros: List<LibroGuardadoEntity>) : LibreriaUiState
}

sealed interface AiDialogState {
    data object Oculto : AiDialogState
    data object SolicitarApiKey : AiDialogState
    data object CargandoRecomendaciones : AiDialogState
    data class Recomendaciones(val texto: String) : AiDialogState
    data class Error(val mensaje: String) : AiDialogState
}

@HiltViewModel
class LibreriaViewModel @Inject constructor(
    private val libroGuardadoRepositorio: LibroGuardadoRepositorio,
    private val deepseekApiKeyStorage: DeepseekApiKeyStorage,
    private val deepseekService: DeepseekService
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibreriaUiState>(LibreriaUiState.Cargando)
    val uiState: StateFlow<LibreriaUiState> = _uiState.asStateFlow()

    private val _aiDialogState = MutableStateFlow<AiDialogState>(AiDialogState.Oculto)
    val aiDialogState: StateFlow<AiDialogState> = _aiDialogState.asStateFlow()

    private val _apiKeyInput = MutableStateFlow("")
    val apiKeyInput: StateFlow<String> = _apiKeyInput.asStateFlow()

    init {
        viewModelScope.launch {
            libroGuardadoRepositorio.obtenerTodos().collect { libros ->
                _uiState.value = LibreriaUiState.Completado(libros)
            }
        }
    }

    fun abrirDialogoAI() {
        if (deepseekApiKeyStorage.hasApiKey()) {
            solicitarRecomendaciones()
        } else {
            _aiDialogState.value = AiDialogState.SolicitarApiKey
        }
    }

    fun cerrarDialogoAI() {
        _aiDialogState.value = AiDialogState.Oculto
    }

    fun actualizarApiKeyInput(texto: String) {
        _apiKeyInput.value = texto
    }

    fun guardarApiKey() {
        val key = _apiKeyInput.value.trim()
        if (key.isNotBlank()) {
            deepseekApiKeyStorage.saveApiKey(key)
            _apiKeyInput.value = ""
            solicitarRecomendaciones()
        }
    }

    private fun solicitarRecomendaciones() {
        viewModelScope.launch {
            _aiDialogState.value = AiDialogState.CargandoRecomendaciones

            val state = _uiState.value
            if (state !is LibreriaUiState.Completado) {
                _aiDialogState.value = AiDialogState.Error("No se pudo cargar la biblioteca")
                return@launch
            }

            val result = deepseekService.getRecommendations(state.libros)

            result.fold(
                onSuccess = { texto ->
                    _aiDialogState.value = AiDialogState.Recomendaciones(texto)
                },
                onFailure = { error ->
                    _aiDialogState.value = AiDialogState.Error(
                        error.message ?: "Error desconocido"
                    )
                }
            )
        }
    }
}
