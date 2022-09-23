package com.example.myMovieApp.feature_movieApp.domain.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myMovieApp.MovieApp
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.common.Resource
import com.example.myMovieApp.feature_movieApp.data.api.repository.ItemType
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repo
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.GenreModel
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.SearchMoviesDao
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject


class MovieAppViewModel @Inject constructor(
    app: Application,
    private val repo: Repo,
) : AndroidViewModel(app) {

    val searchMovieSeries: MutableLiveData<Resource<SearchMoviesDao>> = MutableLiveData()
    var searchMovieSeriesPage = 1
    var searchMovieSeriesResponse: SearchMoviesDao? = null
    var newSearchQuery: String? = null
    var oldSearchQuery: String? = null
    val movieSeriesGenre: MutableLiveData<Resource<GenreModel>> = MutableLiveData()

    /* initialize getSavedMovSer() so to be aware if the db has items or isEmpty  */
    init {
        getSavedMovSer()
    }

    private fun getSavedMovSer() = viewModelScope.launch {
        safeGetMoviesSeries()
    }

    /* check if db isEmpty or not*/
    fun safeGetMoviesSeries(): Boolean {
        val response = repo.hasDBItems()
        return response?.id.toString().isNullOrEmpty()
    }

    fun saveMovieSeries(movieSeries: MovieResultModel) = viewModelScope.launch {
        repo.insertMoviesSeries(movieSeries)
    }

    fun deleteSavedMovieSeries(movieSeries: MovieResultModel) = viewModelScope.launch {
        repo.deleteSavedMoviesSeries(movieSeries)
    }

    /* check if db has movies or series*/
    fun isMovieInDB(id: Int): Boolean {
        return repo.isMovieInDB(id)
    }


    fun searchMovies(query: String) = viewModelScope.launch {
        safeSearchMovieSeriesCall(query)
    }

    /* search in db for movies or series */
    fun getFavMoviesSeries(query: String) = repo.getSavedMoviesSeries(query)

    fun getMovieSeriesGenre(itemType: ItemType, id: Int) = viewModelScope.launch {
        val response = repo.getMoviesSeriesGenre(itemType, id, Constants.API_KEY)

        if (response.body() == null) {
        } else {
            movieSeriesGenre.postValue(Resource.Success(response.body()!!))
        }
    }

    /* if we don't have problem with internet connection we call for movies or series or show the appropriate error */
    private suspend fun safeSearchMovieSeriesCall(query: String) {
        newSearchQuery = query
        searchMovieSeries.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response =
                    repo.searchMoviesSeries(query, searchMovieSeriesPage, Constants.API_KEY)
                searchMovieSeries.postValue(handleSearchNewsResponse(response))
            } else {
                searchMovieSeries.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMovieSeries.postValue(Resource.Error("Network Failure"))
                else -> searchMovieSeries.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    /* handle the response either is successful or not */
    private fun handleSearchNewsResponse(response: Response<SearchMoviesDao>): Resource<SearchMoviesDao> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchMovieSeriesResponse == null || newSearchQuery != oldSearchQuery) {
                    searchMovieSeriesPage = 1
                    oldSearchQuery = newSearchQuery
                    searchMovieSeriesResponse = resultResponse
                } else {
                    searchMovieSeriesPage++
                    val oldMovieSeries = searchMovieSeriesResponse?.results
                    val newMovieSeries = resultResponse.results

                    oldMovieSeries?.addAll(newMovieSeries)
                }
                return Resource.Success(searchMovieSeriesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<MovieApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
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