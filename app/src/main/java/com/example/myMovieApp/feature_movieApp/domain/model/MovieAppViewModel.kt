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
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repo
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

//    Edw logika tha prepei na kanw init kai na vlepw an uparxoun apothikevmenes tainies
    init {
        getSavedMovSer()
    }
    private fun getSavedMovSer() = viewModelScope.launch {
        safeGetMoviesSeries()
    }

    fun safeGetMoviesSeries(): Boolean {
        val response = repo.hasDBItems()
        return response.id.toString().isNotEmpty()
    }



    fun saveMovieSeries(movieSeries: MovieResultModel) = viewModelScope.launch {
        repo.insertMoviesSeries(movieSeries)
    }

    fun deleteSavedMovieSeries(movieSeries: MovieResultModel) = viewModelScope.launch {
        repo.deleteSavedMoviesSeries(movieSeries)
    }

    fun isMovieInDB(id: Int) = repo.isMovieInDB(id)

    fun searchMovies(query: String) = viewModelScope.launch {
        safeSearchMovieSeriesCall(query)
    }
    fun getFavMoviesSeries(query: String) = repo.getSavedMoviesSeries(query)
//        viewModelScope.launch {
//
//        safeSearchMoviesSeriesDB(query)


//    private suspend fun safeSearchMoviesSeriesDB(query: String) = viewModelScope.launch{
//        newSearchQuery = query
//        searchMovieSeries.postValue(Resource.Loading())
//        try {
//            val response = repo.getSavedMoviesSeries(query)
//            searchMovieSeries.postValue(handleSearchNewsResponse(response))
//        }
//
//    }


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

    private fun handleSearchNewsResponse(response: Response<SearchMoviesDao>): Resource<SearchMoviesDao> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchMovieSeriesResponse == null || newSearchQuery != oldSearchQuery) {
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





//    private val _itemExistingDb = MutableLiveData<Boolean>() // We make private variable so that UI/View can't modify directly
//    val itemExistingDb: LiveData<Boolean>
//    get() = _itemExistingDb
//
//
//    fun getMovieSeriesExistStatus(id: Int) {
//        _itemExistingDb.value = true// Rather than returning LiveData, we set value to our local MutableLiveData
//    }
//
//    fun observeServerTime(): LiveData<Boolean> {
//        return itemExistingDb //Here we expose our MutableLiveData as LiveData to avoid modification from UI/View
//    }


}