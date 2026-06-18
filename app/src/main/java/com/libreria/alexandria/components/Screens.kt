package com.libreria.alexandria.components

// Maneja la navegación de la app. Lo copié
// del github de la clase y lo fue modificando
// mientras fui llendo.

sealed class Screen(val route: String) {
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object BookList: Screen("book_list_screen")
    // Dado que cada página de detalles es diferente, aca
    // use parámetros para esta ruta (lo recomienda Google).
    object BookDetail: Screen("book_detail_screen") {
        const val ROUTE_PATTERN = "book_detail_screen/{bookId}/{autor}?pubFecha={pubFecha}"
        fun createRoute(bookId: String, autor: String, pubFecha: String = ""): String {
            val fechaEncoded = android.net.Uri.encode(pubFecha)
            return "book_detail_screen/$bookId/${android.net.Uri.encode(autor)}?pubFecha=$fechaEncoded"
        }
    }
    // Lo mismo que BookDetail, ya que cada pantalla es
    // diferente por cada libro (y es buena práctica)
    object BookReview: Screen("book_review_screen") {
        const val ROUTE_PATTERN = "book_review_screen/{bookId}/{autor}?pubFecha={pubFecha}"
        fun createRoute(bookId: String, autor: String, pubFecha: String = ""): String {
            val fechaEncoded = android.net.Uri.encode(pubFecha)
            return "book_review_screen/$bookId/${android.net.Uri.encode(autor)}?pubFecha=$fechaEncoded"
        }
    }
    object BookLibrary: Screen("book_library_screen")
    object UsuarioPerfil: Screen("usuario_perfil_screen")
}