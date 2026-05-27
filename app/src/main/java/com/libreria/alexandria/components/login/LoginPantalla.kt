package com.libreria.alexandria.components.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.libreria.alexandria.R
import com.libreria.alexandria.components.Screen

@Composable
fun LoginPantalla(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: LoginViewModel = viewModel()
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = remember { context as? Activity }
    val imagenVector: Painter = painterResource(R.drawable.nega_libro)

    val launcherGoogle = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .addOnSuccessListener { cuenta ->
                    viewModel.autenticarConGoogle(cuenta.idToken ?: "")
                }
                .addOnFailureListener { e ->
                    val codigo = (e as? ApiException)?.statusCode
                    viewModel.establecerError(
                        "Error ($codigo): ${e.localizedMessage ?: "No se pudo iniciar sesión"}"
                    )
                }
        }
    }

    LaunchedEffect(estado) {
        if (estado is LoginEstadoUI.Autenticado) {
            navController.navigate(Screen.BookList.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
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

            Spacer(modifier = Modifier.height(48.dp))

            when (estado) {
                is LoginEstadoUI.Error -> {
                    Text(
                        text = (estado as LoginEstadoUI.Error).mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                is LoginEstadoUI.Cargando -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                }
                else -> {}
            }

            BotonGoogle(
                text = "Iniciar sesión con Google",
                onClick = {
                    activity?.let { act ->
                        val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(act.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val cliente = GoogleSignIn.getClient(act, opciones)
                        launcherGoogle.launch(cliente.signInIntent)
                    }
                },
                enabled = estado !is LoginEstadoUI.Cargando
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonGoogle(
                text = "Registrarse con Google",
                outlined = true,
                onClick = {
                    activity?.let { act ->
                        val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(act.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val cliente = GoogleSignIn.getClient(act, opciones)
                        launcherGoogle.launch(cliente.signInIntent)
                    }
                },
                enabled = estado !is LoginEstadoUI.Cargando
            )
        }
    }
}

@Composable
private fun BotonGoogle(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    outlined: Boolean = false
) {
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(48.dp)
        ) {
            Text(
                text = "G",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "  $text",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(48.dp)
        ) {
            Text(
                text = "G",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "  $text",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
