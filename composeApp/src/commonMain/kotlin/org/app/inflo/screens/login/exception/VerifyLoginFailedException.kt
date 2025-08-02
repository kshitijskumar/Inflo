package org.app.inflo.screens.login.exception

class VerifyLoginFailedException(
    val errorCode: Int,
    message: String? = null
) : Exception(message)