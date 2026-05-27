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
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.libreria.alexandria.components.Screen
import com.libreria.alexandria.components.detalle.LibroDetallePantalla
import com.libreria.alexandria.components.detalle.LibroDetalleViewModel
import com.libreria.alexandria.components.listado.LibroListadoPantalla
import com.libreria.alexandria.components.listado.LibrosViewModel
import com.libreria.alexandria.components.login.LoginEstadoUI
import com.libreria.alexandria.components.login.LoginPantalla
import com.libreria.alexandria.components.login.LoginViewModel
import com.libreria.alexandria.components.splash.SplashPantalla
import com.libreria.alexandria.components.splash.SplashViewModel
import com.libreria.alexandria.data.ServiceLocator
import com.libreria.alexandria.ui.theme.AlexandriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlexandriaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlexandriaNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AlexandriaNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(Screen.Splash.route) {
            val splashViewModel: SplashViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        SplashViewModel(ServiceLocator.authRepositorio) as T
                }
            )
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
            val loginViewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        LoginViewModel(ServiceLocator.authRepositorio) as T
                }
            )
            val loginEstado by loginViewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(loginEstado) {
                if (loginEstado is LoginEstadoUI.Autenticado) {
                    navController.navigate(Screen.BookList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
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
            val listadoViewModel: LibrosViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        LibrosViewModel(ServiceLocator.libroRepositorio) as T
                }
            )
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
                onNavigateToDetail = { libroId, autor ->
                    navController.navigate(Screen.BookDetail.createRoute(libroId, autor))
                },
                onCerrarSesion = {
                    ServiceLocator.authRepositorio.cerrarSesion()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.BookDetail.ROUTE_PATTERN) { backStackEntry ->
            val libroId = backStackEntry.arguments?.getString("bookId") ?: ""
            val autor = backStackEntry.arguments?.getString("autor") ?: ""
            val detalleViewModel: LibroDetalleViewModel = viewModel(
                key = libroId,
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        LibroDetalleViewModel(ServiceLocator.libroRepositorio, libroId, autor) as T
                }
            )
            val estado by detalleViewModel.uiState.collectAsStateWithLifecycle()
            LibroDetallePantalla(
                estado = estado,
                onRegresar = { navController.popBackStack() }
            )
        }
    }
}
