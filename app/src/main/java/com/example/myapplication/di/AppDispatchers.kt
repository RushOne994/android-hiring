package com.example.myapplication.di

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {
    val ioDispatcher: CoroutineDispatcher
}