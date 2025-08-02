package org.app.inflo.utils

actual object AppSystem {
    actual fun currentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }
}