package com.example.myMovieApp.feature_movieApp.domain.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.example.myMovieApp.MovieApp
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.common.Resource
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repo
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.SearchMoviesDao
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject


class MovieAppViewModel @Inject constructor(
    app: Application,
    private val repo: Repo,
) : AndroidViewModel(app){

    val searchMovieSeries: MutableLiveData<Resource<SearchMoviesDao>> = MutableLiveData()
    var searchMovieSeriesPage = 1
    var searchMovieSeriesResponse: SearchMoviesDao? = null
    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null


    fun searchMovies(query: String) = viewModelScope.launch {
        safeSearchMovieSeriesCall(query)
    }

    private suspend fun safeSearchMovieSeriesCall(query: String) {
        newSearchQuery = query
        searchMovieSeries.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                // TODO NA MPEI ELEGXOS GIA TO AN EXEI PATISEI TO FAVBTN GIA NA KANEI SEARCH APO TIN DB
                val response = repo.searchMoviesSeries(query, searchMovieSeriesPage,Constants.API_KEY)
                searchMovieSeries.postValue(handleSearchNewsResponse(response))
            } else {
                searchMovieSeries.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchMovieSeries.postValue(Resource.Error("Network Failure"))
                else -> searchMovieSeries.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun handleSearchNewsResponse(response: Response<SearchMoviesDao>) : Resource<SearchMoviesDao> {
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if(searchMovieSeriesResponse == null || newSearchQuery != oldSearchQuery) {
                    searchMovieSeriesPage = 1
                    oldSearchQuery = newSearchQuery
                    searchMovieSeriesResponse = resultResponse
                } else {
                    searchMovieSeriesPage++
                    val oldArticles = searchMovieSeriesResponse?.results
                    val newArticles = resultResponse.results

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchMovieSeriesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MovieApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}