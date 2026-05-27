package com.libreria.alexandria.components.listado

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.libreria.alexandria.data.Libro

private val generos = mapOf(
    "Fantasia" to "Fantasy",
    "Ciencia ficción" to "Sci-fi",
    "Misterio" to "Mystery",
    "Aventura" to "Adventure",
    "Historia" to "History",
    "Poesía" to "Poetry",
    "Biografía" to "Biography",
    "Terror" to "Horror",
    "Dramas" to "Drama"
)

@Composable
fun LibroListadoPantalla(
    uiState: LibroEstadoUI,
    generoElegido: String?,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBuscarPorGenero: (String) -> Unit,
    onCargarSiguientePagina: () -> Unit,
    onNavigateToDetail: (String, String) -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listaEstado = rememberLazyListState()

    val cargarMas by remember {
        derivedStateOf {
            val ultimoLibroGenerado = listaEstado.layoutInfo.visibleItemsInfo.lastOrNull()
            ultimoLibroGenerado != null &&
                ultimoLibroGenerado.index >= listaEstado.layoutInfo.totalItemsCount - 2
        }
    }
    LaunchedEffect(cargarMas) {
        if (cargarMas) {
            onCargarSiguientePagina()
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Buscar libros...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onSearch(query) }
            ),
            leadingIcon = {
                IconButton(onClick = onCerrarSesion) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar sesión")
                }
            },
            trailingIcon = {
                IconButton(onClick = { onSearch(query) }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(generos.keys.toList(), key = { it }) { genero ->
                FilterChip(
                    selected = generos[genero] == generoElegido,
                    onClick = {
                        onQueryChange("")
                        onBuscarPorGenero(generos[genero] ?: genero)
                    },
                    label = { Text(genero) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (uiState) {
            is LibroEstadoUI.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LibroEstadoUI.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is LibroEstadoUI.Completado -> {
                if (uiState.books.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron libros",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        state = listaEstado,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.books, key = { it.id }) { libro ->
                            LibroListadoItem(
                                libro = libro,
                                onClick = {
                                    onNavigateToDetail(libro.id, libro.autor)
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
private fun LibroListadoItem(
    libro: Libro,
    onClick: () -> Unit
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
            AsyncImage(
                model = libro.cubiertaId,
                contentDescription = libro.titulo,
                modifier = Modifier.size(64.dp, 96.dp),
                contentScale = ContentScale.Crop
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
