package org.app.inflo.utils

import android.content.Context
import okio.Path
import okio.Path.Companion.toPath

object DataStorePathHelper {

    fun Context.productPath(name: String): Path {
        return this.applicationContext.filesDir.resolve("datastore/$name").absolutePath.toPath()
    }

} 