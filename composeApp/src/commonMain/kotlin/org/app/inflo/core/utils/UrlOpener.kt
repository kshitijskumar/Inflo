package org.app.inflo.core.utils

interface UrlOpener {
    fun openUrl(url: String): Boolean
    fun openInstagram(username: String): Boolean {
        // Remove @ symbol if present and open in browser
        val cleanUsername = username.removePrefix("@")
        return openUrl("https://instagram.com/$cleanUsername")
    }
} 