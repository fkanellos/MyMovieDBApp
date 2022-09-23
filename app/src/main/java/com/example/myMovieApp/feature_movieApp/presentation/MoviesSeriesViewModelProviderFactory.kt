package com.example.myMovieApp.feature_movieApp.presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repo
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel

/*  instantiate ViewModel */
class MoviesSeriesViewModelProviderFactory (
    val app: Application,
    val movieSeriesRepo: Repo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieAppViewModel(app, movieSeriesRepo) as T
    }
}