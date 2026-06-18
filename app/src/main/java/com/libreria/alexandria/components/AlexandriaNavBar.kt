package com.libreria.alexandria.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// La barrita de navegación inferior que tienen
// todas las apps de este estilo.

sealed class NavBarItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Buscar : NavBarItem(
        route = Screen.BookList.route,
        label = "Buscar",
        icon = Icons.Default.Search
    )

    object Libreria : NavBarItem(
        route = Screen.BookLibrary.route,
        label = "Librería",
        icon = Icons.Default.Book
    )

    object Perfil : NavBarItem(
        route = Screen.UsuarioPerfil.route,
        label = "Perfil",
        icon = Icons.Default.Person
    )
}

val bottomNavItems = listOf(
    NavBarItem.Buscar,
    NavBarItem.Libreria,
    NavBarItem.Perfil
)

@Composable
fun AlexandriaNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color(0xFF241F17)
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.BookList.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
