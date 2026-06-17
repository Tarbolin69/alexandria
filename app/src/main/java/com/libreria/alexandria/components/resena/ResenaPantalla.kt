package com.libreria.alexandria.components.resena

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.libreria.alexandria.R

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
fun ResenaPantalla(
    uiState: ResenaUiState,
    onRegresar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is ResenaUiState.Cargando -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is ResenaUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        is ResenaUiState.Completado -> {
            ResenaContenido(
                cubiertaUrl = uiState.cubiertaUrl,
                autor = uiState.autor,
                pubFecha = uiState.pubFecha,
                onRegresar = onRegresar,
                modifier = modifier,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun ResenaContenido(
    cubiertaUrl: String,
    autor: String,
    pubFecha: String,
    onRegresar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var puntuacion by remember { mutableIntStateOf(0) }
    var textoResena by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val surfaceColor = MaterialTheme.colorScheme.background

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
            ) {
                GlideImage(
                    model = cubiertaUrl,
                    contentDescription = "Portada del libro",
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        surfaceColor
                                    ),
                                    startY = size.height * 0.45f,
                                    endY = size.height,
                                )
                            )
                        },
                    contentScale = ContentScale.Crop,
                    loading = placeholder(R.drawable.baseline_book_24),
                    failure = placeholder(R.drawable.baseline_book_24),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$autor - $pubFecha",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                for (i in 1..5) {
                    Text(
                        text = if (i <= puntuacion) "★" else "☆",
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (i <= puntuacion) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { puntuacion = i },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = textoResena,
                onValueChange = { textoResena = it },
                label = { Text("Escribe tu reseña...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(200.dp),
                maxLines = 10,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onRegresar,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Cancelar",
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Publicar",
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        Row(
            modifier = Modifier
                .padding(12.dp)
                .clickable(onClick = onRegresar),
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
