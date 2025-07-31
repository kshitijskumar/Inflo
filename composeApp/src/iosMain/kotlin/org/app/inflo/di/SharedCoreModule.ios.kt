package org.app.inflo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import org.app.inflo.core.constants.DataStoreConstants
import org.app.inflo.utils.DataStorePathHelper
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual fun platformModule(): Module = module {
    // DataStore for app preferences
    single<DataStore<Preferences>>(qualifier = named(DataStoreConstants.APP_DATA_STORE)) {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { DataStorePathHelper.producePath(DataStoreConstants.APP_DATA_STORE) }
        )
    }
} 