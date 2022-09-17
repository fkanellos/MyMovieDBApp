package com.example.myMovieApp.feature_movieApp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.feature_movieApp.data.api.ApiService
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repo
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import retrofit2.HttpException
import java.io.IOException

class RecyclerDataSource(
//    private val api: ApiService,
    private val query: String,
    private val repo: Repo
) : PagingSource<Int, MovieResultModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResultModel> {
        val position : Int = params.key ?: Constants.STARTING_PAGE
        return try {
            val response = repo.searchMoviesSeries(query, position, Constants.API_KEY)
//            val response = api.getMovies(query, position, Constants.API_KEY)
            LoadResult.Page(
                data = response.body()!!.results,
                prevKey = if ( position == Constants.STARTING_PAGE) null else position -1,
                nextKey = if (response.body()!!.results.isEmpty()) null else position + 1
            )

        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieResultModel>): Int? {
        TODO("Not yet implemented")
    }
}