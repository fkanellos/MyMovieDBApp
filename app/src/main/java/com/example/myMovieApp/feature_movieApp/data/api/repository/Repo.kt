package com.example.myMovieApp.feature_movieApp.data.api.repository

import com.example.myMovieApp.feature_movieApp.data.api.RetrofitInstance
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.GenreModel
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import com.example.myMovieApp.feature_movieApp.db.AppDatabase
import retrofit2.Response

enum class ItemType {
    MOVIE, SERIES
}
class Repo (
    val db: AppDatabase
) {
    // API calls
    suspend fun searchMoviesSeries(query: String, position: Int, apiKey: String) =
        RetrofitInstance.api.getMovies(query, position, apiKey)

    suspend fun getMoviesSeriesGenre(type: ItemType, id: Int, apiKey: String): Response<GenreModel> {
        return when (type) {
            ItemType.MOVIE -> RetrofitInstance.api.getMovieDetails(id, apiKey)
            ItemType.SERIES -> RetrofitInstance.api.getTVSerie(id, apiKey)
        }
    }

    // DB functions
    suspend fun insertMoviesSeries(movieSeries: MovieResultModel) = db.movieDao().insertMovieSeries(movieSeries)
    fun getSavedMoviesSeries(query: String) = db.movieDao().getMovies(query)
    suspend fun deleteSavedMoviesSeries(movieSeries: MovieResultModel) = db.movieDao().deleteMovieSeries(movieSeries)
    fun isMovieInDB(id: Int) = db.movieDao().isMovieInDb(id)
    fun hasDBItems() = db.movieDao().getAll()

}