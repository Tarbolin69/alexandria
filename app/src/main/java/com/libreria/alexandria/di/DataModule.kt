package com.libreria.alexandria.di

import android.content.Context
import androidx.room.Room
import com.libreria.alexandria.data.AuthRepositorio
import com.libreria.alexandria.data.DescripcionAdapter
import com.libreria.alexandria.data.FirebaseAuthRepositorio
import com.libreria.alexandria.data.LibroRemoteDataSource
import com.libreria.alexandria.data.LibroRepositorio
import com.libreria.alexandria.data.LibroRepositorioImpl
import com.libreria.alexandria.data.OpenLibraryAPI
import com.libreria.alexandria.data.PerfilFirebaseRepositorio
import com.libreria.alexandria.data.PerfilRepositorio
import com.libreria.alexandria.data.local.AppDatabase
import com.libreria.alexandria.data.local.PerfilDao
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(DescripcionAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOpenLibraryApi(moshi: Moshi): OpenLibraryAPI = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenLibraryAPI::class.java)

    @Provides
    @Singleton
    fun provideLibroRemoteDataSource(api: OpenLibraryAPI): LibroRemoteDataSource =
        LibroRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideLibroRepositorio(dataSource: LibroRemoteDataSource): LibroRepositorio =
        LibroRepositorioImpl(dataSource)

    @Provides
    @Singleton
    fun provideAuthRepositorio(): AuthRepositorio = FirebaseAuthRepositorio()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "alexandria.db").build()

    @Provides
    fun providePerfilDao(database: AppDatabase): PerfilDao = database.perfilDao()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
