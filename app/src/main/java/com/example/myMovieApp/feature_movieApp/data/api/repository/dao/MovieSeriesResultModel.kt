package com.example.myMovieApp.feature_movieApp.data.api.repository.dao

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "movie and TV Series table"
)
@Parcelize
data class MovieResultModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val adult: Boolean?,
    val backdrop_path: String?,
    var genre: String?,
    val media_type: String?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val release_date: String?,
    val title: String?,
    val video: Boolean?,
    val first_air_date: String?,
    val name: String?,
    val original_name: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    var videos: String?
) : Parcelable