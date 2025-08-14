package org.app.inflo.db

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AppDatabase(
	private val driverFactory: () -> SqlDriver,
	private val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

	private val actionTypeAdapter = object : ColumnAdapter<CampaignActionType, String> {
		override fun decode(databaseValue: String): CampaignActionType = CampaignActionType.valueOf(databaseValue)
		override fun encode(value: CampaignActionType): String = value.name
	}

	private val decisionStatusAdapter = object : ColumnAdapter<CampaignDecisionStatus, String> {
		override fun decode(databaseValue: String): CampaignDecisionStatus = CampaignDecisionStatus.valueOf(databaseValue)
		override fun encode(value: CampaignDecisionStatus): String = value.name
	}

	private val database: InfloDatabase by lazy {
		InfloDatabase(
			driverFactory(),
			campaign_decisionsAdapter = Campaign_decisions.Adapter(
				actionAdapter = actionTypeAdapter,
				statusAdapter = decisionStatusAdapter
			)
		)
	}

	private val decisionsQueries: CampaignDecisionsQueries by lazy { database.campaignDecisionsQueries }

	val appDao: AppDao by lazy {
		AppDaoImpl(queries = decisionsQueries, ioDispatcher = ioDispatcher)
	}
} 