package com.example.myMovieApp.feature_movieApp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.SearchMoviesDao

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieSeries(movie: MovieResultModel)

    @Query("SELECT * FROM 'movie table' WHERE name LIKE :query_ OR title LIKE :query_")
    fun getMovies(query_: String): MovieResultModel

    @Delete
    suspend fun deleteMovieSeries(movie: MovieResultModel)
}