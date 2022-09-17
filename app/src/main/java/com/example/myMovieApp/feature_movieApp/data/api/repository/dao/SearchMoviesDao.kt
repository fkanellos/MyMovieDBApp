package com.example.myMovieApp.feature_movieApp.data.api.repository.dao

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchMoviesDao(
    val page: Int,
    val results: MutableList<MovieResultModel>,
    val total_pages: Int,
    val total_results: Int
) : Parcelable
