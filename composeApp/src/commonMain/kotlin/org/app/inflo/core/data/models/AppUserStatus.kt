package org.app.inflo.core.data.models

sealed class AppUserStatus {

    data class LoggedIn(val user: UserAppModel) : AppUserStatus()

    data class Onboarded(val user: OnboardedUser) : AppUserStatus()

    data object NotLoggedIn : AppUserStatus()

}