package com.example.myMovieApp.feature_movieApp.data.api.repository.dao

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoResultModel(
    val id: Int?,
    val results: List<Result>?
) : Parcelable