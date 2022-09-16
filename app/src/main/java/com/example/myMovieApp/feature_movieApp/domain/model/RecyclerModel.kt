package com.example.myMovieApp.feature_movieApp.domain.model

import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel

data class RecyclerModel(
    val result: List<MovieResultModel>
)