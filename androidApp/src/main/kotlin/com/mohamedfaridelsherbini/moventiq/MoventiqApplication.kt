package com.mohamedfaridelsherbini.moventiq

import android.app.Application
import com.mohamedfaridelsherbini.moventiq.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MoventiqApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoventiqApplication)
            modules(appModule)
        }
    }
}
