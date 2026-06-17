package com.libreria.alexandria.components.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PerfilPantalla(
    uiState: PerfilUiState,
    onAcercaDeMiChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onSitioWebChange: (String) -> Unit,
    onEditar: () -> Unit,
    onGuardar: () -> Unit,
    onCancelarEdicion: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuarioInfo = uiState.usuarioInfo
    val estaEditando = uiState.estaEditando

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = usuarioInfo?.nombre ?: "Usuario",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val fotoUrl = usuarioInfo?.fotoUrl
                if (fotoUrl != null) {
                    GlideImage(
                        model = fotoUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(72.dp),
                        contentScale = ContentScale.Crop,
                        failure = placeholder(com.libreria.alexandria.R.drawable.baseline_book_24)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Acerca de mi",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (estaEditando) {
            OutlinedTextField(
                value = uiState.acercaDeMi,
                onValueChange = onAcercaDeMiChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Escribe algo sobre ti...") }
            )
        } else {
            Text(
                text = uiState.acercaDeMi.ifEmpty { "Sin información" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (uiState.acercaDeMi.isEmpty())
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Información de la cuenta",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoInfoEditable(
            label = "Email",
            valor = uiState.email,
            estaEditando = estaEditando,
            placeholder = "ejemplo@mail.com",
            onValueChange = onEmailChange
        )
        CampoInfoEditable(
            label = "Teléfono",
            valor = uiState.telefono,
            estaEditando = estaEditando,
            placeholder = "+52 123 456 7890",
            onValueChange = onTelefonoChange
        )
        CampoInfoEditable(
            label = "Sitio",
            valor = uiState.sitioWeb,
            estaEditando = estaEditando,
            placeholder = "www.ejemplo.com",
            onValueChange = onSitioWebChange
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (estaEditando) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onCancelarEdicion,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.size(12.dp))
                Button(
                    onClick = onGuardar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar")
                }
            }
        } else {
            Button(
                onClick = onEditar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar")
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesion")
            }
        }
    }
}

@Composable
private fun CampoInfoEditable(
    label: String,
    valor: String,
    estaEditando: Boolean,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (estaEditando) {
            OutlinedTextField(
                value = valor,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                singleLine = true
            )
        } else {
            Text(
                text = valor.ifEmpty { "Sin información" },
                style = MaterialTheme.typography.bodyLarge,
                color = if (valor.isEmpty())
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
