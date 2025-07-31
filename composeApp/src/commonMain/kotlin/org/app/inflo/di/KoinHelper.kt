package org.app.inflo.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

object KoinHelper {

    fun initKoin(
        initialApplicationSetup: KoinApplication.() -> Unit = {}
    ) {
        startKoin {
            initialApplicationSetup.invoke(this)
            modules(
                getSharedCoreModule()
            )
        }
    }
} 