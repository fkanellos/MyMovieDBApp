package com.example.myMovieApp.feature_movieApp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myMovieApp.R
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repo
import com.example.myMovieApp.feature_movieApp.db.AppDatabase
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MovieAppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val movieSeriesRepository = Repo(AppDatabase(this))
        val viewModelProviderFactory =
            MoviesSeriesViewModelProviderFactory(application, movieSeriesRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MovieAppViewModel::class.java)

    }
}