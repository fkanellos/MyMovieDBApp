package com.example.myMovieApp.feature_movieApp.data.api.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.myMovieApp.feature_movieApp.data.RecyclerDataSource
import com.example.myMovieApp.feature_movieApp.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject
constructor(private val service: ApiService) {

    fun getMoviesSearch(query : String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { RecyclerDataSource(service,query) }
    ).liveData

    fun getTrailerSearch(query : String) = Pager(
        config = PagingConfig(
            pageSize = 20,
            maxSize = 100,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { RecyclerDataSource(service,query) }
    ).liveData


}