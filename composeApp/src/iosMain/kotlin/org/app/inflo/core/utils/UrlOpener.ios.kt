package org.app.inflo.core.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IOSUrlOpener : UrlOpener {
    
    override fun openUrl(url: String): Boolean {
        return try {
            val nsUrl = NSURL.URLWithString(url) ?: return false
            if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
                UIApplication.sharedApplication.openURL(nsUrl)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
} 