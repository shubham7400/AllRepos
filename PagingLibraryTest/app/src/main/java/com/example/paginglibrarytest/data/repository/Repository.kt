package com.example.paginglibrarytest.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.paginglibrarytest.data.local.UnsplashDatabase
import com.example.paginglibrarytest.data.paging.SearchPagingSource
import com.example.paginglibrarytest.data.paging.UnsplashRemoteMediator
import com.example.paginglibrarytest.data.remote.UnsplashApi
import com.example.paginglibrarytest.model.UnsplashImage
import com.example.paginglibrarytest.util.Constants.ITEMS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Query
import javax.inject.Inject

class Repository @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val unsplashDatabase: UnsplashDatabase
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getAllImages() : Flow<PagingData<UnsplashImage>> {
        val pagingSourceFactory = { unsplashDatabase.unsplashImageDao().getAllImages() }
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            remoteMediator = UnsplashRemoteMediator(
                unsplashApi = unsplashApi,
                unsplashDatabase = unsplashDatabase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun searchImages( query: String) : Flow<PagingData<UnsplashImage>> {
        return Pager(
            config = PagingConfig(pageSize = ITEMS_PER_PAGE),
            pagingSourceFactory = {
                SearchPagingSource(unsplashApi = unsplashApi, query = query)
            }
        ).flow
    }
}