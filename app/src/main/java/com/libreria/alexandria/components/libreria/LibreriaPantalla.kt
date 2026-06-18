package com.libreria.alexandria.components.libreria

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.libreria.alexandria.R
import com.libreria.alexandria.data.local.LibroGuardadoEntity

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LibreriaPantalla(
    uiState: LibreriaUiState,
    aiDialogState: AiDialogState,
    apiKeyInput: String,
    onNavigateToDetail: (String, String, String) -> Unit,
    onRobotClick: () -> Unit,
    onCerrarDialogoAI: () -> Unit,
    onApiKeyInputChange: (String) -> Unit,
    onGuardarApiKey: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (aiDialogState != AiDialogState.Oculto) {
        ModalBottomSheet(
            onDismissRequest = onCerrarDialogoAI,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AiDialogContent(
                aiDialogState = aiDialogState,
                apiKeyInput = apiKeyInput,
                onCerrar = onCerrarDialogoAI,
                onApiKeyInputChange = onApiKeyInputChange,
                onGuardarApiKey = onGuardarApiKey
            )
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Libreria",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "Recomendaciones AI",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRobotClick() },
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        when (uiState) {
            is LibreriaUiState.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LibreriaUiState.Completado -> {
                if (uiState.libros.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay libros guardados",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.libros, key = { it.bookId }) { libro ->
                            LibroGuardadoItem(
                                libro = libro,
                                onClick = {
                                    onNavigateToDetail(libro.bookId, libro.autor, libro.pubFecha)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiDialogContent(
    aiDialogState: AiDialogState,
    apiKeyInput: String,
    onCerrar: () -> Unit,
    onApiKeyInputChange: (String) -> Unit,
    onGuardarApiKey: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCerrar) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Recomendaciones AI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (aiDialogState) {
            is AiDialogState.SolicitarApiKey -> {
                Text(
                    text = "Ingresa tu API key de Deepseek para recibir recomendaciones personalizadas:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = onApiKeyInputChange,
                    label = { Text("Deepseek API Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onGuardarApiKey,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar y obtener recomendaciones")
                }
            }
            is AiDialogState.CargandoRecomendaciones -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Consultando a Deepseek AI...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            is AiDialogState.Recomendaciones -> {
                val scrollState = rememberScrollState()
                LaunchedEffect(aiDialogState.texto) {
                    scrollState.scrollTo(0)
                }
                Text(
                    text = aiDialogState.texto,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .height(400.dp)
                )
            }
            is AiDialogState.Error -> {
                Text(
                    text = "Error: ${aiDialogState.mensaje}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is AiDialogState.Oculto -> { /* no renderiza */ }
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun LibroGuardadoItem(
    libro: LibroGuardadoEntity,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = libro.cubiertaId,
                contentDescription = libro.titulo,
                modifier = Modifier.size(64.dp, 96.dp),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.baseline_book_24),
                failure = placeholder(R.drawable.baseline_book_24)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = libro.titulo,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = libro.autor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = libro.pubFecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
