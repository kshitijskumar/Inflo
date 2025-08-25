package org.app.inflo.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

class AndroidUrlOpener(private val context: Context) : UrlOpener {
    
    override fun openUrl(url: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }
} 