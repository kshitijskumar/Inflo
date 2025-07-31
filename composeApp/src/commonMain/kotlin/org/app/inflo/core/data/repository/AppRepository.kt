package org.app.inflo.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.app.inflo.core.data.models.OnboardedUser
import org.app.inflo.core.data.models.UserAppModel

interface AppRepository {

    fun loggedInUser(): Flow<UserAppModel?>
    
    fun onboardedUser(): Flow<OnboardedUser?>

}