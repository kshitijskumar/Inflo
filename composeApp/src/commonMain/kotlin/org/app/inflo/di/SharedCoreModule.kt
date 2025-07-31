package org.app.inflo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.app.inflo.core.constants.DataStoreConstants
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.core.data.repository.AppRepositoryImpl
import org.app.inflo.core.data.local.AppLocalDataSource
import org.app.inflo.core.data.local.AppLocalDataSourceImpl
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloNavigationManagerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun getSharedCoreModule() = module {
    includes(commonModule(), platformModule())
}

private fun commonModule() = module {
    // Navigation
    singleOf(::InfloNavigationManagerImpl) { bind<InfloNavigationManager>() }
    
    // Repository
    singleOf(::AppRepositoryImpl) { bind<AppRepository>() }
    
    // Data Sources
    single<AppLocalDataSource> {
        AppLocalDataSourceImpl(
            dataStore = get(named(DataStoreConstants.APP_DATA_STORE))
        )
    }
    
    // Remote data source will be provided when network module is added
    // factory<AppRemoteDataSource> { ... }
}

internal expect fun platformModule(): Module 