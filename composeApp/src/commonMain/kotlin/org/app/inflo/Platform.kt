package org.app.inflo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform