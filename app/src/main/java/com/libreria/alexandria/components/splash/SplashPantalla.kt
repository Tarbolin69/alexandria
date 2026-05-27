package com.libreria.alexandria.components.splash

// Yo intenté buscar por todos lados, pero no pude
// encontrar manera alguna de remover el SplashScreen
// nativo de Android Studio (el que tiene el logo de
// android en fondo blanco). La única solución que vi
// es "esconderlo", pero eso no remueve el tiempo que
// este usa para ser renderizado.

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.libreria.alexandria.R
import kotlinx.coroutines.delay

@Composable
fun SplashPantalla(
    uiState: SplashUiState,
    onSplashFinished: (String) -> Unit,
    modifier: Modifier = Modifier,
    imagenVector: Painter = painterResource(R.drawable.nega_libro)
) {
    LaunchedEffect(uiState) {
        if (uiState is SplashUiState.Navegar) {
            delay(2000L)
            onSplashFinished(uiState.ruta)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = imagenVector,
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Alexandria",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}