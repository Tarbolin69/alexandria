package com.libreria.alexandria.components.detalle

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

private data class ResenaPlaceholder(
    val usuario: String,
    val texto: String,
    val puntuacion: Int,
)

private val resenasPlaceholder = listOf(
    ResenaPlaceholder("usuario01", "Una obra fascinante que atrapa desde la primera página.", 5),
    ResenaPlaceholder("lector_ávido", "Buen libro, aunque el ritmo decae en la mitad.", 4),
    ResenaPlaceholder("librero99", "Lo recomiendo ampliamente, muy bien escrito.", 5),
    ResenaPlaceholder("lector_critico", "Esperaba más del desenlace, pero en general es entretenido.", 3),
)

@Composable
fun LibroDetallePantalla(
    estado: LibroDetalleUiState,
    onRegresar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (estado) {
            is LibroDetalleUiState.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LibroDetalleUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = estado.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is LibroDetalleUiState.Completado -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                        ) {
                            AsyncImage(
                                model = estado.cubiertaUrl,
                                contentDescription = estado.titulo,
                                modifier = Modifier.size(120.dp, 180.dp),
                                contentScale = ContentScale.Crop,
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = estado.titulo,
                                    style = MaterialTheme.typography.headlineSmall,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = estado.autor,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = estado.pubFecha,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Descripción",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = estado.descripcion,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }

                    item {
                        HorizontalDivider()
                    }

                    item {
                        Text(
                            text = "Reseñas de usuarios",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    items(resenasPlaceholder, key = { it.usuario }) { resena ->
                        ResenaItem(resena)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .clickable(onClick = onRegresar)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Regresar",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun ResenaItem(resena: ResenaPlaceholder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = resena.usuario,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "${"★".repeat(resena.puntuacion)}${"☆".repeat(5 - resena.puntuacion)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = resena.texto,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
