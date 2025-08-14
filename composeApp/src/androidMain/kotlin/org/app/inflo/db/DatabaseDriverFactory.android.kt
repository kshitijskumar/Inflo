package org.app.inflo.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver

class DatabaseDriverFactory(private val context: Context) {
	fun createDriver(): SqlDriver = AndroidSqliteDriver(InfloDatabase.Schema, context, "inflo.db")
} 