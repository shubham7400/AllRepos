package com.app.core.data.remotemediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.app.core.data.model.asEntity
import com.app.core.data.providers.sort.SortTypeProvider
import com.app.core.data.util.DataConstants
import com.app.core.database.SpaceXDatabase
import com.app.core.database.model.HistoryEventEntity
import com.app.core.database.model.RemoteKeysEntity
import com.app.core.model.sort.HistoryEventSortType
import com.app.core.network.SpaceXService
import com.app.core.network.model.NetworkHistoryEvent
import com.app.core.network.model.Options
import com.app.core.network.model.QueryBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@ExperimentalPagingApi
class HistoryEventsRemoteMediator(
    private val spaceXService: SpaceXService,
    private val database: SpaceXDatabase,
    private val sortTypeProvider: SortTypeProvider<HistoryEventSortType>,
) : BaseRemoteMediator<HistoryEventEntity>(database.remoteKeysDao()) {

    override suspend fun initialize(): InitializeAction {
        var firstHistoryEvent: HistoryEventEntity? = null
        database.withTransaction {
            firstHistoryEvent = database.historyEventsDao().getLast()
        }
        return getInitializeAction(firstHistoryEvent?.createdAt)
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HistoryEventEntity>,
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> getPageForRefreshLoadType(state)

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }


            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        try {
            val sortType = sortTypeProvider.getSortType()
            val sortTypeField = when(sortType) {
                HistoryEventSortType.NAME_ASC, HistoryEventSortType.NAME_DESC -> NetworkHistoryEvent.TITLE_FIELD
                HistoryEventSortType.DATE_ASC, HistoryEventSortType.DATE_DESC -> NetworkHistoryEvent.DATE_FIELD
            }
            val sortParameter = mapOf(sortTypeField to sortType.value)
            val options = Options(page, DataConstants.PAGE_SIZE, sortParameter)
            val queryBody = QueryBody(options)
            val apiResponse = spaceXService.getHistoryEvents(queryBody)
            val endOfPaginationReached = page >= apiResponse.totalPages
            val historyEvents = apiResponse.historyEvents
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.historyEventsDao().clearAll()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = historyEvents.map {
                    RemoteKeysEntity(it.id, prevKey, nextKey)
                }
                database.remoteKeysDao().insertAll(keys)
                database.historyEventsDao()
                    .insertAll(historyEvents.map { it.asEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached)
        } catch (exception: IOException) {
            Timber.e("load exception $exception")
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            Timber.e("load exception $exception")
            return MediatorResult.Error(exception)
        } catch (exception: Exception) {
            Timber.e("load exception $exception")
            return MediatorResult.Error(exception)
        }
    }
}