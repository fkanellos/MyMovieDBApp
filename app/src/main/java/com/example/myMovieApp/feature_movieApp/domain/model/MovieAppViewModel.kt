package com.example.myMovieApp.feature_movieApp.domain.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class MovieAppViewModel @Inject constructor(
    private val repo: Repository,
) : ViewModel(){

    private val currentQuery = MutableLiveData(Constants.DEFAULT_QUERY_VALUE)
    private val currentTrailerQuery = MutableLiveData(Constants.DEFAULT_VIDEO_QUERY_VALUE)

    val resultData = currentQuery.switchMap {
        repo.getMoviesSearch(URLEncoder.encode(it,Constants.ENCODE)).cachedIn(viewModelScope)
    }

    fun searchMovies(query: String){
        currentQuery.value=query
    }

    val trailerResult = currentTrailerQuery.switchMap {
        repo.getTrailerSearch(URLEncoder.encode(it,Constants.ENCODE)).cachedIn(viewModelScope)
    }

    fun searchMovieTrailer(movieId: Int){
        currentTrailerQuery.value = movieId.toString()

    }
}