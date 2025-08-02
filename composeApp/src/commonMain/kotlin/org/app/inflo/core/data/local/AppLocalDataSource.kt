package org.app.inflo.core.data.local

import kotlinx.coroutines.flow.Flow
import org.app.inflo.core.data.models.AppUserStatus
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel

interface AppLocalDataSource {

    fun storedUser(): Flow<UserAppModel?>
    
    fun onboardedUser(): Flow<OnboardedUser?>

    fun userStatus(): Flow<AppUserStatus>
    
    suspend fun storeUser(user: UserAppModel)
    
    suspend fun storeOnboardedUser(onboardedUser: OnboardedUser)
    
    suspend fun clearAllUserData()

}