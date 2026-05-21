package com.example.gamenews

import android.app.Application
import com.example.gamenews.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import com.example.gamenews.core.di.androidModule

class GameNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(
            platformModules = listOf(androidModule)
        ) {
            androidContext(androidContext = this@GameNewsApplication)
        }
    }
}