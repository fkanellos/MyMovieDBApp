package com.example.myMovieApp.feature_movieApp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myMovieApp.R
import com.example.myMovieApp.feature_movieApp.data.api.repository.Repo
import com.example.myMovieApp.feature_movieApp.db.AppDatabase
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MovieAppViewModel

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val movieSeriesRepository = Repo(AppDatabase(this))
        val viewModelProviderFactory = NewsViewModelProviderFactory(application, movieSeriesRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MovieAppViewModel::class.java)

//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
//        navController = navHostFragment.findNavController()
//
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController,appBarConfiguration)
    }

//    val newsRepository = NewsRepository(ArticleDatabase(this))
//    val viewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
//    viewModel = ViewModelProvider(this, viewModelProviderFactory).get(NewsViewModel::class.java)
//    bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
}