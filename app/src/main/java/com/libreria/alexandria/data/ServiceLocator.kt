package com.libreria.alexandria.data

// Singleton contenedor de dependencias.

object ServiceLocator {
    private val dataSource = LibroRemoteDataSource(ApiClient.api)
    val libroRepositorio: LibroRepositorio = LibroRepositorioImpl(dataSource)
    val authRepositorio: AuthRepositorio = FirebaseAuthRepositorio()
}
