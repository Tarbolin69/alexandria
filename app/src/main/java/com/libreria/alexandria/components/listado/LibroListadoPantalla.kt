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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.libreria.alexandria.components.Screen
import com.libreria.alexandria.data.ApiClient
import com.libreria.alexandria.data.Libro
import com.libreria.alexandria.data.LibroRemoteDataSource
import com.libreria.alexandria.data.LibroRepositorio

private val generos = listOf(
    "Romance", "Fantasy", "Sci-fi", "Mystery", "Adventure",
    "History", "Poetry", "Biography", "Horror", "Drama",
)

@Composable
fun LibroListadoPantalla(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val fabrica = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val dataSource = LibroRemoteDataSource(ApiClient.api)
                val repositorio = LibroRepositorio(dataSource)
                return LibrosViewModel(repositorio) as T
            }
        }
    }
    val viewModel: LibrosViewModel = viewModel(factory = fabrica)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val generoElegido by viewModel.selectedSubject.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
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
            viewModel.cargarSiguientePagina()
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar libros...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { viewModel.buscarLibros(query) }
            ),
            trailingIcon = {
                IconButton(onClick = { viewModel.buscarLibros(query) }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(generos, key = { it }) { genero ->
                FilterChip(
                    selected = genero == generoElegido,
                    onClick = {
                        query = ""
                        viewModel.buscarPorGenero(genero)
                    },
                    label = { Text(genero) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (val estado = uiState) {
            is LibrosViewModel.LibroEstadoUI.Cargando -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LibrosViewModel.LibroEstadoUI.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = estado.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is LibrosViewModel.LibroEstadoUI.Completado -> {
                if (estado.books.isEmpty()) {
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
                        items(estado.books, key = { it.id }) { libro ->
                            LibroListadoItem(
                                libro = libro,
                                onClick = {
                                    navController.navigate(
                                        Screen.BookDetail.createRoute(libro.id, libro.autor)
                                    )
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
