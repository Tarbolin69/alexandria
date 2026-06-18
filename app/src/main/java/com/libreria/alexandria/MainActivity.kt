package com.libreria.alexandria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.libreria.alexandria.components.AlexandriaNavBar
import com.libreria.alexandria.components.Screen
import com.libreria.alexandria.components.bottomNavItems
import com.libreria.alexandria.components.detalle.LibroDetallePantalla
import com.libreria.alexandria.components.detalle.LibroDetalleUiState
import com.libreria.alexandria.components.detalle.LibroDetalleViewModel
import com.libreria.alexandria.components.libreria.LibreriaPantalla
import com.libreria.alexandria.components.libreria.LibreriaViewModel
import com.libreria.alexandria.components.listado.LibroListadoPantalla
import com.libreria.alexandria.components.listado.LibroListadoViewModel
import com.libreria.alexandria.components.login.LoginEstadoUI
import com.libreria.alexandria.components.login.LoginPantalla
import com.libreria.alexandria.components.login.LoginViewModel
import com.libreria.alexandria.components.perfil.PerfilPantalla
import com.libreria.alexandria.components.perfil.PerfilPantallaViewModel
import com.libreria.alexandria.components.critica.CriticaPantalla
import com.libreria.alexandria.components.critica.CriticaViewModel
import com.libreria.alexandria.components.splash.SplashPantalla
import com.libreria.alexandria.components.splash.SplashViewModel
import com.libreria.alexandria.ui.theme.AlexandriaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlexandriaTheme {
                AlexandriaMainScreen()
            }
        }
    }
}

@Composable
fun AlexandriaMainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = remember { bottomNavItems.map { it.route }.toSet() }
    val showBottomBar = currentRoute in bottomNavRoutes

    // La barra de navegación no se muestra en la pantalla de inicio de sesión.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                AlexandriaNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        AlexandriaNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AlexandriaNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Splash.route) {
            val splashViewModel: SplashViewModel = hiltViewModel()
            val splashEstado by splashViewModel.uiState.collectAsStateWithLifecycle()
            SplashPantalla(
                uiState = splashEstado,
                onSplashFinished = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            val loginEstado by loginViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(loginEstado) {
                if (loginEstado is LoginEstadoUI.Autenticado) {
                    navController.navigate(Screen.BookList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginPantalla(
                estado = loginEstado,
                onAutenticarConGoogle = { loginViewModel.autenticarConGoogle(it) },
                onEstablecerError = { loginViewModel.establecerError(it) }
            )
        }
        composable(Screen.BookList.route) {
            val listadoViewModel: LibroListadoViewModel = hiltViewModel()
            val uiState by listadoViewModel.uiState.collectAsStateWithLifecycle()
            val generoElegido by listadoViewModel.selectedSubject.collectAsStateWithLifecycle()
            val query by listadoViewModel.query.collectAsStateWithLifecycle()

            LibroListadoPantalla(
                uiState = uiState,
                generoElegido = generoElegido,
                query = query,
                onQueryChange = { listadoViewModel.actualizarQuery(it) },
                onSearch = { listadoViewModel.buscarLibros(it) },
                onBuscarPorGenero = { listadoViewModel.buscarPorGenero(it) },
                onCargarSiguientePagina = { listadoViewModel.cargarSiguientePagina() },
                onNavigateToDetail = { libroId, autor, pubFecha ->
                    navController.navigate(Screen.BookDetail.createRoute(libroId, autor, pubFecha))
                }
            )
        }
        composable(
            route = Screen.BookDetail.ROUTE_PATTERN,
            arguments = listOf(
                navArgument("pubFecha") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val detalleViewModel: LibroDetalleViewModel = hiltViewModel()
            val estado by detalleViewModel.uiState.collectAsStateWithLifecycle()
            val esMarcado by detalleViewModel.esMarcado.collectAsStateWithLifecycle()
            val criticas by detalleViewModel.criticas.collectAsStateWithLifecycle()
            val yaCalificado by detalleViewModel.yaCalificado.collectAsStateWithLifecycle()
            LibroDetallePantalla(
                estado = estado,
                esMarcado = esMarcado,
                criticas = criticas,
                yaCalificado = yaCalificado,
                onRegresar = { navController.popBackStack() },
                onAlternarMarcador = { detalleViewModel.alternarMarcador() },
                onCalificar = {
                    val st = estado
                    if (st is LibroDetalleUiState.Completado) {
                        navController.navigate(
                            Screen.BookReview.createRoute(
                                detalleViewModel.libroId,
                                st.autor,
                                st.pubFecha,
                            )
                        )
                    }
                }
            )
        }
        composable(
            route = Screen.BookReview.ROUTE_PATTERN,
            arguments = listOf(
                navArgument("pubFecha") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val criticaViewModel: CriticaViewModel = hiltViewModel()
            val criticaEstado by criticaViewModel.uiState.collectAsStateWithLifecycle()
            CriticaPantalla(
                uiState = criticaEstado,
                onRegresar = { navController.popBackStack() },
                onPublicar = { puntuacion, texto ->
                    criticaViewModel.publicar(puntuacion, texto)
                },
            )
        }
        composable(Screen.BookLibrary.route) {
            val libreriaViewModel: LibreriaViewModel = hiltViewModel()
            val libreriaEstado by libreriaViewModel.uiState.collectAsStateWithLifecycle()
            val aiDialogState by libreriaViewModel.aiDialogState.collectAsStateWithLifecycle()
            val apiKeyInput by libreriaViewModel.apiKeyInput.collectAsStateWithLifecycle()
            LibreriaPantalla(
                uiState = libreriaEstado,
                aiDialogState = aiDialogState,
                apiKeyInput = apiKeyInput,
                onNavigateToDetail = { libroId, autor, pubFecha ->
                    navController.navigate(Screen.BookDetail.createRoute(libroId, autor, pubFecha))
                },
                onRobotClick = { libreriaViewModel.abrirDialogoAI() },
                onCerrarDialogoAI = { libreriaViewModel.cerrarDialogoAI() },
                onApiKeyInputChange = { libreriaViewModel.actualizarApiKeyInput(it) },
                onGuardarApiKey = { libreriaViewModel.guardarApiKey() }
            )
        }
        composable(Screen.UsuarioPerfil.route) {
            val perfilViewModel: PerfilPantallaViewModel = hiltViewModel()
            val perfilEstado by perfilViewModel.uiState.collectAsStateWithLifecycle()
            PerfilPantalla(
                uiState = perfilEstado,
                onAcercaDeMiChange = { perfilViewModel.actualizarAcercaDeMi(it) },
                onEmailChange = { perfilViewModel.actualizarEmail(it) },
                onTelefonoChange = { perfilViewModel.actualizarTelefono(it) },
                onSitioWebChange = { perfilViewModel.actualizarSitioWeb(it) },
                onEditar = { perfilViewModel.iniciarEdicion() },
                onGuardar = { perfilViewModel.guardar() },
                onCancelarEdicion = { perfilViewModel.cancelarEdicion() },
                onCerrarSesion = {
                    perfilViewModel.cerrarSesion()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onMostrarCerrarSesion = { perfilViewModel.mostrarDialogoCerrarSesion() },
                onOcultarDialogoCerrarSesion = { perfilViewModel.ocultarDialogoCerrarSesion() }
            )
        }
    }
}
