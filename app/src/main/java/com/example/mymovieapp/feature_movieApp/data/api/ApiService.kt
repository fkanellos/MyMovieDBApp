package com.example.mymovieapp.feature_movieApp.data.api

import com.example.mymovieapp.feature_movieApp.data.api.repository.dao.SearchMoviesDao
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("search/multi")
    suspend fun getMovies(
        @Query("query") query : String,
        @Query("page") page : Int,
        @Query("api_key") apiKey : String
    ): Response<SearchMoviesDao>
}