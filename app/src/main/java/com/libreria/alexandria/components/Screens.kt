package com.libreria.alexandria.components

sealed class Screen(val route: String) {
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object BookList: Screen("book_list_screen")
    object BookDetail: Screen("book_detail_screen") {
        const val ROUTE_PATTERN = "book_detail_screen/{bookId}/{autor}"
        fun createRoute(bookId: String, autor: String): String {
            return "book_detail_screen/$bookId/${android.net.Uri.encode(autor)}"
        }
    }
    object BookReview: Screen("book_review_screen")
    object BookLibrary: Screen("book_library_screen")
}