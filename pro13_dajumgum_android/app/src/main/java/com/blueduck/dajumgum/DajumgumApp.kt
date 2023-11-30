package com.blueduck.dajumgum

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for the Dajumgum app with Hilt integration.
 * Provides a singleton instance of the application and sets up Hilt for dependency injection.
 */

@HiltAndroidApp
class DajumgumApp : Application() {

    /**
     * Called when the application is created.
     * Sets the singleton instance of the application.
     */
    override fun onCreate() {
        super.onCreate()
        dajumgumApp = this
    }

    companion object {
        // Singleton instance of the DajumgumApp
        var dajumgumApp: DajumgumApp? = null
            private set
        private val TAG = DajumgumApp::class.java.simpleName
    }
}