package com.example.myMovieApp.feature_movieApp.data.api.repository.dao

data class GenreModel(
    val videos: VideoResultModel,
    val genres: List<Genre>
)