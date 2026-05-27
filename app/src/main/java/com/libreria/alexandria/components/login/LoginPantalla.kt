package com.libreria.alexandria.components.login

import android.app.Activity
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.libreria.alexandria.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginPantalla(
    estado: LoginEstadoUI,
    onAutenticarConGoogle: (String) -> Unit,
    onEstablecerError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(contexto) }
    val imagenVector: Painter = painterResource(R.drawable.nega_libro)

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
                        text = estado.mensaje,
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
                    launchGoogleSignIn(
                        scope, credentialManager, contexto,
                        onToken = { onAutenticarConGoogle(it) },
                        onError = { onEstablecerError(it) },
                        soloCuentasExistentes = true
                    )
                },
                enabled = estado !is LoginEstadoUI.Cargando
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonGoogle(
                text = "Registrarse con Google",
                outlined = true,
                onClick = {
                    launchGoogleSignIn(
                        scope, credentialManager, contexto,
                        onToken = { onAutenticarConGoogle(it) },
                        onError = { onEstablecerError(it) },
                        soloCuentasExistentes = false
                    )
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

private fun launchGoogleSignIn(
    scope: CoroutineScope,
    credentialManager: CredentialManager,
    context: android.content.Context,
    onToken: (String) -> Unit,
    onError: (String) -> Unit,
    soloCuentasExistentes: Boolean
) {
    val activity = context as? Activity ?: return
    scope.launch {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(soloCuentasExistentes)
                .setServerClientId(activity.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(activity, request)
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.credential.data)
            onToken(googleIdTokenCredential.idToken)
        } catch (_: GetCredentialCancellationException) {
        } catch (e: Exception) {
            onError(
                "Error: ${e::class.simpleName} - ${e.message}"
            )
        }
    }
}
