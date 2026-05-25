package com.libreria.alexandria.components

sealed class Screen(val route: String) {
    object Splash: Screen("splash_screen")
    object Login: Screen("login_screen")
    object BookList: Screen("book_list_screen")
    object BookDetail: Screen("book_detail_screen")
    object BookReview: Screen("book_review_screen")
    object BookLibrary: Screen("book_library_screen")
}