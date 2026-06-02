package com.mohamedfaridelsherbini.moventiq

import android.app.Application
import com.mohamedfaridelsherbini.moventiq.di.appModule
import com.mohamedfaridelsherbini.moventiq.di.initKoin
import org.koin.android.ext.koin.androidContext

class MoventiqApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MoventiqApplication)
            modules(appModule)
        }
    }
}
