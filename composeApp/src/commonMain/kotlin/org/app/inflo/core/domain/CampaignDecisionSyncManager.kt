package org.app.inflo.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.app.inflo.core.data.models.CampaignDecisionSyncResult
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.db.AppDao
import kotlin.math.min
import kotlin.time.Duration.Companion.minutes

interface CampaignDecisionSyncManager {
    fun start(scope: CoroutineScope)
    fun stop()
}

class CampaignDecisionSyncManagerImpl(
    private val appRepository: AppRepository,
    private val syncCampaignDecisionsUseCase: SyncCampaignDecisionsUseCase
) : CampaignDecisionSyncManager {

    companion object {
        private const val SYNC_INTERVAL_MS = 2 * 10 * 1000L // 2 minutes
        private const val MAX_BACKOFF_MS = 10 * 60 * 1000L // 10 minutes
        private const val BACKOFF_INCREMENT_MS = 2 * 60 * 1000L // 2 minutes
        private const val INITIAL_BACKOFF_MS = 2 * 60 * 1000L // 2 minutes
    }

    private var managerJob: Job? = null
    private var syncJob: Job? = null

    @OptIn(FlowPreview::class)
    override fun start(scope: CoroutineScope) {
        if (managerJob?.isActive == true) return

        managerJob = scope.launch(Dispatchers.IO) {
            val userId = appRepository.loggedInUser().firstOrNull()?.id ?: return@launch
            appRepository.allPendingDecisionsForUser(userId)
                .map { it.isNotEmpty() }
                .distinctUntilChanged()
                .debounce(500)
                .collectLatest { containsPendingEntries ->
                    val pendingEntries = appRepository.allPendingDecisionsForUser(userId).firstOrNull()
                    if (pendingEntries.isNullOrEmpty()) {
                        // no entries, we can stop the manager insider timer loop
                        syncJob?.cancel()
                    } else {
                        syncJob?.cancel()
                        syncJob = launch {
                            var delay = SYNC_INTERVAL_MS
                            while (isActive) {
                                delay(delay)
                                when(val result = performSync()) {
                                    is CampaignDecisionSyncResult.Success -> {
                                        delay = SYNC_INTERVAL_MS
                                    }
                                    is CampaignDecisionSyncResult.Failure -> {
                                        val incrementedDelay = delay + BACKOFF_INCREMENT_MS
                                        delay = min(incrementedDelay, MAX_BACKOFF_MS)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    override fun stop() {
        managerJob?.cancel()
        syncJob?.cancel()
        syncJob = null
        managerJob = null
    }
    
    private suspend fun performSync(): CampaignDecisionSyncResult {
        return syncCampaignDecisionsUseCase.invoke()
    }
} 