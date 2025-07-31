package org.app.inflo

import android.app.Application
import org.app.inflo.di.KoinHelper
import org.koin.android.ext.koin.androidContext

class InfloApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        KoinHelper.initKoin {
            androidContext(this@InfloApplication)
        }
    }
} 