package org.app.inflo.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.utils.AppJson
import org.app.inflo.utils.decodeFromStringSafely

class AppLocalDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : AppLocalDataSource {

    private val userKey = stringPreferencesKey("user")
    private val onboardedUserKey = stringPreferencesKey("onboarded_user")

    override fun storedUser(): Flow<UserAppModel?> {
        return dataStore.data.map { preferences ->
            val userJson = preferences[userKey]
            userJson?.let {
                AppJson.decodeFromStringSafely<UserAppModel>(it)
            }
        }
    }

    override fun onboardedUser(): Flow<OnboardedUser?> {
        return dataStore.data.map { preferences ->
            val onboardedUserJson = preferences[onboardedUserKey]
            onboardedUserJson?.let {
                AppJson.decodeFromStringSafely<OnboardedUser>(it)
            }
        }
    }
    
    override suspend fun storeUser(user: UserAppModel) {
        dataStore.edit { preferences ->
            // Store user and clear onboarded user since only one can exist
            preferences[userKey] = AppJson.encodeToString(user)
            preferences.remove(onboardedUserKey)
        }
    }
    
    override suspend fun storeOnboardedUser(onboardedUser: OnboardedUser) {
        dataStore.edit { preferences ->
            // Store onboarded user and clear regular user since only one can exist
            preferences[onboardedUserKey] = AppJson.encodeToString(onboardedUser)
            preferences.remove(userKey)
        }
    }
    
    override suspend fun clearAllUserData() {
        dataStore.edit { preferences ->
            preferences.remove(userKey)
            preferences.remove(onboardedUserKey)
        }
    }
}