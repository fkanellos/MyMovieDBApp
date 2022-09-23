package com.example.myMovieApp.feature_movieApp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel

@Dao
interface MovieDbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieSeries(movie: MovieResultModel)

    @Query("SELECT * FROM 'movie and TV Series table' WHERE title LIKE '%' || :query_ || '%' OR name LIKE '%' || :query_ || '%'")
    fun getMovies(query_: String): LiveData<List<MovieResultModel>>

    @Query("SELECT EXISTS(SELECT * FROM 'movie and TV Series table' WHERE id = :id_)")
    fun isMovieInDb(id_: Int): Boolean

    @Query("SELECT * FROM 'movie and TV Series table'")
    fun getAll(): MovieResultModel

    @Delete
    suspend fun deleteMovieSeries(movie: MovieResultModel)
}