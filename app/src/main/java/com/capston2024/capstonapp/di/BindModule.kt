package com.capston2024.capstonapp.di

import com.capston2024.capstonapp.data.datasource.AuthDataSource
import com.capston2024.capstonapp.data.datasourceImpl.AuthDataSourceImpl
import com.capston2024.capstonapp.data.repositoryImpl.AuthRepositoryImpl
import com.capston2024.capstonapp.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun provideAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource
}