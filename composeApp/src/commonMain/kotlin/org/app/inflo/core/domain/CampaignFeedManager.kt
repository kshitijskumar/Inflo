package org.app.inflo.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.app.inflo.core.data.models.CampaignDisplayDataAppModel
import org.app.inflo.core.data.models.UserAppModel
import org.app.inflo.core.data.repository.AppRepository

interface CampaignFeedManager {

    val data: StateFlow<CampaignFeedStatus>

    fun fetch(
        scope: CoroutineScope,
        shouldFetchFresh: Boolean = false
    )

}

data class CampaignFeedStatus(
    val data: List<CampaignDisplayDataAppModel>?,
    val isFetching: Boolean,
    val error: String?,
    val currentPage: Int,
    val isLastPage: Boolean
)

class CampaignFeedManagerImpl(
    private val fetchCampaignFeedUseCase: FetchCampaignFeedUseCase,
    private val appRepository: AppRepository
): CampaignFeedManager {

    private val mutex = Mutex()

    private val _data = MutableStateFlow(
        CampaignFeedStatus(
            data = null,
            isFetching = false,
            error = null,
            currentPage = 0,
            isLastPage = false
        )
    )
    override val data: StateFlow<CampaignFeedStatus> = _data.asStateFlow()

    override fun fetch(
        scope: CoroutineScope,
        shouldFetchFresh: Boolean
    ) {
        scope.launch {
            mutex.withLock {
                if (data.value.isLastPage) {
                    // no more data available
                    return@launch
                }
                val userId = (appRepository.loggedInUser().firstOrNull() as? UserAppModel.Creator)?.id ?: return@launch
                val pageToFetch = if (shouldFetchFresh) 0 else data.value.currentPage + 1

                _data.update {
                    it.copy(
                        data = if (shouldFetchFresh) null else it.data,
                        isFetching = true,
                        error = null,
                        currentPage = pageToFetch
                    )
                }

                val result = fetchCampaignFeedUseCase.invoke(
                    creatorId = userId,
                    page = pageToFetch
                )

                when(result) {
                    is CampaignFeedResult.Error -> {
                        _data.update {
                            it.copy(
                                isFetching = false,
                                error = "Something went wrong"
                            )
                        }
                    }
                    is CampaignFeedResult.Success -> {
                        _data.update {
                            it.copy(
                                isFetching = false,
                                error = null,
                                data = (it.data ?: listOf()) + result.data.data,
                                isLastPage = result.data.isLastPage,
                                currentPage = result.data.currentPage
                            )
                        }
                    }
                }
            }
        }
    }
}