package com.example.mymovieapp.feature_movieApp.data.api.repository.dao

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchMoviesDao(
    val page: Int,
    val results: List<MovieResultModel>,
    val total_pages: Int,
    val total_results: Int
) : Parcelable
