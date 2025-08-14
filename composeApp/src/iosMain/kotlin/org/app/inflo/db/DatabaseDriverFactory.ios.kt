package org.app.inflo.db

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.db.SqlDriver

class DatabaseDriverFactory {
	fun createDriver(): SqlDriver = NativeSqliteDriver(InfloDatabase.Schema, "inflo.db")
} 