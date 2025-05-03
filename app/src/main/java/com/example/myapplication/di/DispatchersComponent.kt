package com.example.myapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersComponent {

    @Provides
    fun provideDispatchers() = object : AppDispatchers {
        override val ioDispatcher = Dispatchers.IO

    }
}