package org.app.inflo.utils

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual object AppSystem {
    actual fun currentTimeInMillis(): Long {
        return NSDate().timeIntervalSince1970.toLong() * 1000
    }
}