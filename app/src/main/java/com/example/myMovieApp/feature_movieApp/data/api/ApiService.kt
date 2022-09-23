package com.example.myMovieApp.feature_movieApp.data.api

import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.GenreModel
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.SearchMoviesDao
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.VideoResultModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/multi")
    suspend fun getMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String
    ): Response<SearchMoviesDao>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<VideoResultModel>

    @GET("/tv/{tv_id}/videos")
    suspend fun getTVSerieVideos(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): Response<VideoResultModel>

    @GET("/3/tv/{tv_id}")
    suspend fun getTVSerie(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendString: String = "videos"
    ): Response<GenreModel>

    @GET("/3/movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendString: String = "videos"
    ): Response<GenreModel>
}