package com.libreria.alexandria.data

object ServiceLocator {
    private val dataSource = LibroRemoteDataSource(ApiClient.api)
    val libroRepositorio: LibroRepositorio = LibroRepositorioImpl(dataSource)
    val authRepositorio: AuthRepositorio = FirebaseAuthRepositorio()
}
