package com.oddzmint.slipstash

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SlipStashApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}