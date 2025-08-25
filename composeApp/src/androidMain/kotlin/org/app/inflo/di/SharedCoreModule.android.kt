package org.app.inflo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import org.app.inflo.core.constants.DataStoreConstants
import org.app.inflo.core.utils.AndroidUrlOpener
import org.app.inflo.core.utils.UrlOpener
import org.app.inflo.utils.DataStorePathHelper.productPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.app.inflo.db.DatabaseDriverFactory
import app.cash.sqldelight.db.SqlDriver

internal actual fun platformModule(): Module = module {
    // DataStore for app preferences
    single<DataStore<Preferences>>(qualifier = named(DataStoreConstants.APP_DATA_STORE)) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { androidContext().productPath(DataStoreConstants.APP_DATA_STORE) }
        )
    }

    // SQLDelight Android driver
    single<SqlDriver> { DatabaseDriverFactory(androidContext()).createDriver() }
    
    // URL Opener
    single<UrlOpener> { AndroidUrlOpener(androidContext()) }
} 