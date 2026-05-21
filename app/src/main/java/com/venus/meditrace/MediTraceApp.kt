package com.venus.meditrace

import android.app.Application
import com.venus.meditrace.data.api.RetrofitClient
import timber.log.Timber

/**
 * Application entry point.
 *
 * Production improvements over prototype:
 *  - [RetrofitClient.init] is called here so the OkHttpClient is built once
 *    with the application context and is never recreated on screen rotation.
 *  - Timber is planted in DEBUG builds only; release builds are silent.
 *    Swap [Timber.DebugTree] for a crash-reporting tree (e.g. Firebase
 *    Crashlytics) in release when you add that dependency.
 */
class MediTraceApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Logging — debug only
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Networking — initialise once with application context
        RetrofitClient.init(this)
    }
}