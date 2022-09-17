package com.example.myMovieApp.feature_movieApp.data.api.repository

import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.feature_movieApp.data.api.RetrofitInstance
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import com.example.myMovieApp.feature_movieApp.db.AppDatabase

class Repo (
    val db: AppDatabase
        ) {
    suspend fun searchMoviesSeries(query: String, position: Int, apiKey: String) =
        RetrofitInstance.api.getMovies(query, position, apiKey)

    suspend fun insertMoviesSeries(movieSeries: MovieResultModel) = db.movieDao().insertMovieSeries(movieSeries)

    fun getSavedMoviesSeries(query: String) = db.movieDao().getMovies(query)

    suspend fun deleteSavedMoviesSeries(movieSeries: MovieResultModel) = db.movieDao().deleteMovieSeries(movieSeries)
}