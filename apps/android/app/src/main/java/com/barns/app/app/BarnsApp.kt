package com.barns.app.app

import android.app.Application

/**
 * Application entry point. Builds and holds the app environment.
 * The Compose host (Activity) and navigation are added in a later task.
 */
class BarnsApp : Application() {
    lateinit var environment: AppEnvironment
        private set

    override fun onCreate() {
        super.onCreate()
        environment = AppEnvironment.makeDefault()
    }
}
