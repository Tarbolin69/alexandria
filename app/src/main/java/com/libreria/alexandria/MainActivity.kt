package com.libreria.alexandria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.libreria.alexandria.components.Screen
import com.libreria.alexandria.components.detalle.LibroDetallePantalla
import com.libreria.alexandria.components.listado.LibroListadoPantalla
import com.libreria.alexandria.components.splash.SplashPantalla
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
            SplashPantalla(navController = navController)
        }
        composable(Screen.BookList.route) {
            LibroListadoPantalla(navController = navController)
        }
        composable(Screen.BookDetail.ROUTE_PATTERN) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val autor = backStackEntry.arguments?.getString("autor") ?: ""
            LibroDetallePantalla(
                        navController = navController,
                        libroId = bookId,
                        autor = autor,
                )
        }
    }
}